package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assume;
import org.junit.Test;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.InArchiveImpl;
import net.sf.sevenzipjbinding.impl.OutArchiveImpl;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;

/**
 * The ELF native library must export its JNI entry points and nothing else (PR #95). With the C++ runtime linked in
 * statically and exported, its GNU-unique symbols (the locale facet ids) bind process-wide to the host's
 * <code>libstdc++.so.6</code>, so C++ code loaded after the binding crashes as soon as it formats a number. Exported
 * 7-Zip internals could likewise be interposed by other libraries in the process.
 * <p>
 * The tests read the dynamic symbol table of the libraries in the platform jar, the files that get loaded. Non-ELF
 * platforms (Windows, macOS) are skipped.
 */
public class NativeLibraryExportsTest extends JUnitNativeTestBase<VoidContext> {
    private static final int SHT_DYNSYM = 11;
    private static final int SHN_UNDEF = 0;
    private static final int STB_LOCAL = 0;
    private static final int STB_GNU_UNIQUE = 10;

    /**
     * Symbols that old linkers define in every shared object. They are no C++ runtime and no 7-Zip code.
     */
    private static final Set<String> LINKER_DEFINED_SYMBOLS = new HashSet<String>(
            Arrays.asList("_init", "_fini", "_edata", "_end", "__bss_start"));

    private static final Class<?>[] CLASSES_WITH_NATIVE_METHODS = { SevenZip.class, InArchiveImpl.class,
            OutArchiveImpl.class };

    private static class ElfSymbol {
        final String name;
        final int binding;

        ElfSymbol(String name, int binding) {
            this.name = name;
            this.binding = binding;
        }
    }

    @Test
    public void elfLibraryExportsOnlyJniEntryPoints() throws Exception {
        for (String library : getElfLibraries()) {
            List<String> unexpected = new ArrayList<String>();
            for (ElfSymbol symbol : readExportedSymbols(library)) {
                if (!symbol.name.startsWith("Java_") && !symbol.name.startsWith("JNI_On")
                        && !LINKER_DEFINED_SYMBOLS.contains(symbol.name)) {
                    unexpected.add(symbol.name);
                }
            }
            assertEquals(library + " exports symbols other than the JNI entry points (first 20 shown): "
                    + unexpected.subList(0, Math.min(20, unexpected.size())), 0, unexpected.size());
        }
    }

    @Test
    public void elfLibraryExportsNoGnuUniqueSymbols() throws Exception {
        for (String library : getElfLibraries()) {
            List<String> unique = new ArrayList<String>();
            for (ElfSymbol symbol : readExportedSymbols(library)) {
                if (symbol.binding == STB_GNU_UNIQUE) {
                    unique.add(symbol.name);
                }
            }
            assertEquals(library + " exports GNU-unique symbols (first 20 shown): "
                    + unique.subList(0, Math.min(20, unique.size())), 0, unique.size());
        }
    }

    @Test
    public void elfLibraryExportsAllNativeMethods() throws Exception {
        for (String library : getElfLibraries()) {
            Set<String> exported = new HashSet<String>();
            for (ElfSymbol symbol : readExportedSymbols(library)) {
                exported.add(symbol.name);
            }
            Set<String> missing = new TreeSet<String>();
            int nativeMethodCount = 0;
            for (Class<?> clazz : CLASSES_WITH_NATIVE_METHODS) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (Modifier.isNative(method.getModifiers())) {
                        nativeMethodCount++;
                        String jniName = "Java_" + mangle(clazz.getName()) + "_" + mangle(method.getName());
                        if (!exported.contains(jniName)) {
                            missing.add(jniName);
                        }
                    }
                }
            }
            assertTrue("no native methods found", nativeMethodCount > 20);
            assertEquals(library + " doesn't export native methods", new TreeSet<String>(), missing);
        }
    }

    private List<String> getElfLibraries() throws IOException {
        String platform = SevenZip.getUsedPlatform();
        assertNotNull("native library wasn't loaded from a platform jar", platform);
        Properties properties = new Properties();
        InputStream propertiesStream = getPlatformResource(platform, "sevenzipjbinding-lib.properties");
        try {
            properties.load(propertiesStream);
        } finally {
            propertiesStream.close();
        }
        List<String> elfLibraries = new ArrayList<String>();
        for (int i = 1; properties.getProperty("lib." + i + ".name") != null; i++) {
            String library = platform + "/" + properties.getProperty("lib." + i + ".name");
            byte[] header = Arrays.copyOf(readPlatformResource(library), 4);
            if (Arrays.equals(header, new byte[] { 0x7F, 'E', 'L', 'F' })) {
                elfLibraries.add(library);
            }
        }
        Assume.assumeTrue("no ELF libraries on platform " + platform, !elfLibraries.isEmpty());
        return elfLibraries;
    }

    private static InputStream getPlatformResource(String platform, String name) {
        InputStream inputStream = SevenZip.class.getResourceAsStream("/" + platform + "/" + name);
        assertNotNull("resource not found: /" + platform + "/" + name, inputStream);
        return inputStream;
    }

    private static byte[] readPlatformResource(String library) throws IOException {
        InputStream inputStream = SevenZip.class.getResourceAsStream("/" + library);
        assertNotNull("resource not found: /" + library, inputStream);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[64 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return outputStream.toByteArray();
        } finally {
            inputStream.close();
        }
    }

    /**
     * Read the defined non-local symbols of the dynamic symbol table (<code>.dynsym</code>): the exports.
     */
    private static List<ElfSymbol> readExportedSymbols(String library) throws IOException {
        ByteBuffer elf = ByteBuffer.wrap(readPlatformResource(library));
        boolean is64 = elf.get(4) == 2;
        elf.order(elf.get(5) == 2 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

        long sectionHeaderOffset = is64 ? elf.getLong(0x28) : elf.getInt(0x20) & 0xFFFFFFFFL;
        int sectionHeaderSize = elf.getShort(is64 ? 0x3A : 0x2E) & 0xFFFF;
        int sectionCount = elf.getShort(is64 ? 0x3C : 0x30) & 0xFFFF;

        List<ElfSymbol> exported = new ArrayList<ElfSymbol>();
        boolean dynsymFound = false;
        for (int i = 0; i < sectionCount; i++) {
            int section = (int) (sectionHeaderOffset + (long) i * sectionHeaderSize);
            if (elf.getInt(section + 4) != SHT_DYNSYM) {
                continue;
            }
            dynsymFound = true;
            long offset = is64 ? elf.getLong(section + 24) : elf.getInt(section + 16) & 0xFFFFFFFFL;
            long size = is64 ? elf.getLong(section + 32) : elf.getInt(section + 20) & 0xFFFFFFFFL;
            int link = elf.getInt(section + (is64 ? 40 : 24));
            int entrySize = (int) (is64 ? elf.getLong(section + 56) : elf.getInt(section + 36) & 0xFFFFFFFFL);

            int stringTable = (int) (sectionHeaderOffset + (long) link * sectionHeaderSize);
            int stringTableOffset = (int) (is64 ? elf.getLong(stringTable + 24) : elf.getInt(stringTable + 16));

            for (long entry = offset; entry < offset + size; entry += entrySize) {
                int symbol = (int) entry;
                int nameIndex = elf.getInt(symbol);
                int info = elf.get(symbol + (is64 ? 4 : 12)) & 0xFF;
                int sectionIndex = elf.getShort(symbol + (is64 ? 6 : 14)) & 0xFFFF;
                int binding = info >> 4;
                if (sectionIndex == SHN_UNDEF || binding == STB_LOCAL) {
                    continue;
                }
                exported.add(new ElfSymbol(readString(elf, stringTableOffset + nameIndex), binding));
            }
        }
        assertTrue(library + ": no .dynsym section", dynsymFound);
        assertTrue(library + ": doesn't export SevenZip.nativeInitSevenZipLibrary",
                containsSymbol(exported, "Java_net_sf_sevenzipjbinding_SevenZip_nativeInitSevenZipLibrary"));
        return exported;
    }

    private static boolean containsSymbol(List<ElfSymbol> symbols, String name) {
        for (ElfSymbol symbol : symbols) {
            if (symbol.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static String readString(ByteBuffer elf, int offset) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = offset; elf.get(i) != 0; i++) {
            stringBuilder.append((char) (elf.get(i) & 0xFF));
        }
        return stringBuilder.toString();
    }

    /**
     * JNI short name mangling (JNI specification, "Resolving Native Method Names").
     */
    private static String mangle(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (c == '.' || c == '/') {
                stringBuilder.append('_');
            } else if (c == '_') {
                stringBuilder.append("_1");
            } else if (c == ';') {
                stringBuilder.append("_2");
            } else if (c == '[') {
                stringBuilder.append("_3");
            } else if (c < 128 && Character.isLetterOrDigit(c)) {
                stringBuilder.append(c);
            } else {
                stringBuilder.append(String.format("_0%04x", Integer.valueOf(c)));
            }
        }
        return stringBuilder.toString();
    }
}
