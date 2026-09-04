package net.sf.sevenzipjbinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Detects the concrete native-library "arch" suffix for the current runtime so that
 * {@link SevenZip#getPlatformBestMatch()} can pick the right library out of a multi-platform jar
 * (e.g. {@code AllPlatforms}).
 * <p>
 * The hard case is <b>32-bit ARM</b>: the JVM reports {@code os.arch=arm} for <i>every</i> 32-bit ARM
 * (armv5/armv6/armv7), and the correct choice depends on two independent axes:
 * <ul>
 * <li>the CPU <b>architecture level</b> (v5 / v6 / v7 / v8), and</li>
 * <li>the userspace <b>float ABI</b> (soft-float = <i>armel</i>, hard-float = <i>armhf</i>).</li>
 * </ul>
 * A ladder of detection methods is used, cheapest/simplest first, each failing safely to
 * {@code null}. All the Linux methods are pure-Java (reading {@code /proc}) so they also work inside
 * stripped containers that have no {@code uname}. Trying each native library in-process is
 * deliberately avoided (it would SIGSEGV with core dumps and load large libraries); see the
 * subprocess probe in {@code SevenZip} as the very last resort.
 *
 * @see SevenZip#getPlatformBestMatch()
 */
final class PlatformArchDetector {

    /** AT_PLATFORM entry type in the ELF auxiliary vector ({@code /proc/self/auxv}). */
    private static final int AT_PLATFORM = 15;
    /** {@code e_flags} bit: hard-float ABI (VFP registers used for parameter passing). */
    private static final int EF_ARM_ABI_FLOAT_HARD = 0x00000400;
    /** {@code e_flags} bit: soft-float ABI. */
    private static final int EF_ARM_ABI_FLOAT_SOFT = 0x00000200;

    /** Float ABI of the ARM userspace. */
    enum FloatAbi {
        HARD, SOFT, UNKNOWN
    }

    /** C library flavour of the Linux userspace. musl (e.g. Alpine) is ABI-incompatible with glibc. */
    enum LibC {
        GNU, MUSL, UNKNOWN
    }

    /** Diagnostic trail of what each detection method saw; folded into error messages. */
    private final List<String> diagnostics = new ArrayList<String>();

    private PlatformArchDetector() {
    }

    /**
     * Returns an ordered (best first), de-duplicated list of arch suffixes to try against the
     * available platforms, e.g. {@code ["arm64"]} or {@code ["armv7", "armv6"]}. Never returns
     * {@code null}; may be empty if nothing could be determined.
     *
     * @param osArch value of the {@code os.arch} system property
     * @param diagnosticsOut if non-{@code null}, receives a human-readable detection trail
     */
    static List<String> getArchCandidates(String osArch, List<String> diagnosticsOut) {
        PlatformArchDetector d = new PlatformArchDetector();
        List<String> result = d.detect(osArch);
        // On a musl userspace (e.g. Alpine) a glibc-built .so cannot be loaded (and vice-versa), so
        // map the candidates to the musl-specific platform variants (e.g. arm64 -> arm64-musl). This
        // only fires when a musl libc is actually detected; on glibc / non-Linux the candidates are
        // returned unchanged. We deliberately do NOT keep the glibc names as a fallback on musl:
        // loading the wrong-libc library would crash the JVM rather than fail gracefully.
        LibC libc = d.detectLibC();
        diagnostics_addLibc(d, libc);
        if (libc == LibC.MUSL) {
            result = muslVariants(result);
        }
        if (diagnosticsOut != null) {
            diagnosticsOut.addAll(d.diagnostics);
        }
        return result;
    }

    private static void diagnostics_addLibc(PlatformArchDetector d, LibC libc) {
        d.diagnostics.add("libc=" + libc);
    }

    /** Map each arch candidate to its musl-specific platform variant, e.g. {@code arm64 -> arm64-musl}. */
    static List<String> muslVariants(List<String> candidates) {
        List<String> out = new ArrayList<String>(candidates.size());
        for (String candidate : candidates) {
            out.add(candidate + "-musl");
        }
        return out;
    }

    private List<String> detect(String osArch) {
        String arch = osArch == null ? "" : osArch.trim().toLowerCase(Locale.ROOT);
        diagnostics.add("os.arch=" + osArch);

        // --- non-ARM and 64-bit ARM: a simple normalization is enough (exact match downstream) ---
        if (arch.equals("amd64") || arch.equals("x86_64") || arch.equals("x64")) {
            return ordered("amd64", "x86_64");
        }
        if (arch.equals("aarch64") || arch.equals("arm64")) {
            return ordered("arm64", "aarch64");
        }
        if (arch.matches("i[3-6]86") || arch.equals("x86") || arch.equals("ia32")) {
            return ordered("i386", "x86");
        }
        // Some JVMs already encode the ARM level in os.arch (e.g. "armv7l", "armv6l").
        if (arch.startsWith("armv") && arch.length() >= 5 && Character.isDigit(arch.charAt(4))) {
            int levelFromOsArch = arch.charAt(4) - '0';
            diagnostics.add("os.arch encodes ARM level " + levelFromOsArch);
            return armCandidates(levelFromOsArch, detectFloatAbi());
        }
        if (!arch.equals("arm")) {
            // Unknown/other arch: fall back to the raw value (exact match may still work).
            return ordered(arch);
        }

        // --- 32-bit ARM: os.arch == "arm" -> determine (level, floatAbi) ---
        int level = detectArmLevel();
        FloatAbi floatAbi = detectFloatAbi();
        diagnostics.add("resolved ARM level=" + (level > 0 ? level : "?") + ", floatAbi=" + floatAbi);
        return armCandidates(level, floatAbi);
    }

    /**
     * Builds the ordered ARM candidate list from a detected level and float ABI, honouring float-ABI
     * compatibility (a hard-float library needs a hard-float userspace and vice versa) and allowing a
     * higher-level system to fall back to a lower-level (but same-float) library.
     */
    private List<String> armCandidates(int level, FloatAbi floatAbi) {
        // Soft-float userspace (armel): only the soft-float armv5 build is ABI-compatible; it runs on
        // any ARMv5+ CPU.
        if (floatAbi == FloatAbi.SOFT) {
            return ordered("armv5");
        }
        // Hard-float userspace (armhf): pick the highest hard-float build the CPU can run, then
        // degrade. armv7 code needs a v7 CPU; armv6 code runs on v6 and v7.
        if (floatAbi == FloatAbi.HARD) {
            if (level >= 7) {
                return ordered("armv7", "armv6");
            }
            if (level == 6) {
                return ordered("armv6");
            }
            // Hard-float ARMv5 has no VFP and no build exists; nothing safe to offer.
            diagnostics.add("hard-float but level<6: no compatible ARM build");
            return ordered();
        }
        // Float ABI unknown: offer everything ordered by level as a best effort (downstream picks the
        // first that both exists and, ideally, loads).
        diagnostics.add("float ABI unknown: offering all ARM builds by level (best effort)");
        if (level >= 7) {
            return ordered("armv7", "armv6", "armv5");
        }
        if (level == 6) {
            return ordered("armv6", "armv5");
        }
        return ordered("armv5");
    }

    // ------------------------------------------------------------------ ARM level detection ------

    /** @return ARM architecture level (5/6/7/8) or -1 if undetermined. */
    private int detectArmLevel() {
        int level = detectArmLevelViaUname();
        if (level > 0) {
            return level;
        }
        level = detectArmLevelViaAuxv();
        if (level > 0) {
            return level;
        }
        // /proc/cpuinfo is a last resort: the "CPU architecture" field is unreliable (e.g. the
        // Raspberry Pi ARMv6 reports 7). Kept only for environments where the above fail.
        return detectArmLevelViaCpuinfo();
    }

    /** {@code uname -m} -> {@code armv7l} / {@code armv6l} / {@code armv5tejl} ... (may be absent). */
    private int detectArmLevelViaUname() {
        String m = readProcessLine("uname", "-m");
        if (m == null) {
            diagnostics.add("uname -m: unavailable");
            return -1;
        }
        m = m.trim().toLowerCase(Locale.ROOT);
        int level = parseArmvLevel(m);
        diagnostics.add("uname -m=" + m + " -> level " + (level > 0 ? level : "?"));
        return level;
    }

    /**
     * ELF auxiliary vector ({@code /proc/self/auxv}) AT_PLATFORM -> {@code "v7l"} etc. Kernel-provided,
     * present even in stripped containers, and correct on the Raspberry Pi. AT_PLATFORM's value is a
     * pointer into this process's own address space, dereferenced via {@code /proc/self/mem}.
     */
    private int detectArmLevelViaAuxv() {
        RandomAccessFile auxv = null;
        RandomAccessFile mem = null;
        try {
            File auxvFile = new File("/proc/self/auxv");
            if (!auxvFile.canRead()) {
                diagnostics.add("/proc/self/auxv: not readable");
                return -1;
            }
            byte[] raw = readAllBytes(auxvFile, 4096);
            boolean le = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
            // 32-bit ARM: each auxv entry is two 32-bit words (type, value).
            long platformPtr = -1;
            for (int i = 0; i + 8 <= raw.length; i += 8) {
                long type = readU32(raw, i, le);
                long value = readU32(raw, i + 4, le);
                if (type == 0 && value == 0) {
                    break; // AT_NULL terminator
                }
                if (type == AT_PLATFORM) {
                    platformPtr = value;
                    break;
                }
            }
            if (platformPtr <= 0) {
                diagnostics.add("/proc/self/auxv: no AT_PLATFORM");
                return -1;
            }
            mem = new RandomAccessFile("/proc/self/mem", "r");
            mem.seek(platformPtr);
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 16; i++) {
                int b = mem.read();
                if (b <= 0) {
                    break;
                }
                sb.append((char) b);
            }
            String platform = sb.toString().toLowerCase(Locale.ROOT); // e.g. "v7l"
            int level = parseVLevel(platform);
            diagnostics.add("auxv AT_PLATFORM=" + platform + " -> level " + (level > 0 ? level : "?"));
            return level;
        } catch (Throwable t) {
            diagnostics.add("auxv detection failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return -1;
        } finally {
            closeQuietly(auxv);
            closeQuietly(mem);
        }
    }

    /** {@code /proc/cpuinfo} "CPU architecture: N" (unreliable -- RPi ARMv6 reports 7). */
    private int detectArmLevelViaCpuinfo() {
        try {
            for (String line : readLines(new File("/proc/cpuinfo"))) {
                String l = line.toLowerCase(Locale.ROOT);
                if (l.startsWith("cpu architecture")) {
                    int colon = l.indexOf(':');
                    if (colon >= 0) {
                        String v = l.substring(colon + 1).trim();
                        // values like "7", "5TEJ" -> take the leading digit
                        for (int i = 0; i < v.length(); i++) {
                            if (Character.isDigit(v.charAt(i))) {
                                int level = v.charAt(i) - '0';
                                diagnostics.add("cpuinfo 'CPU architecture'=" + v
                                        + " -> level " + level + " (UNRELIABLE)");
                                return level;
                            }
                        }
                    }
                }
            }
            diagnostics.add("/proc/cpuinfo: no 'CPU architecture'");
        } catch (Throwable t) {
            diagnostics.add("cpuinfo detection failed: " + t.getMessage());
        }
        return -1;
    }

    // ------------------------------------------------------------------ float ABI detection ------

    private FloatAbi detectFloatAbi() {
        FloatAbi abi = detectFloatAbiViaElf();
        if (abi != FloatAbi.UNKNOWN) {
            return abi;
        }
        return detectFloatAbiViaLdLinux();
    }

    /** Read the ARM {@code e_flags} of {@code /proc/self/exe} (the running JVM binary). */
    private FloatAbi detectFloatAbiViaElf() {
        RandomAccessFile exe = null;
        try {
            File exeFile = new File("/proc/self/exe");
            exe = new RandomAccessFile(exeFile, "r");
            byte[] ident = new byte[20];
            exe.readFully(ident);
            if (ident[0] != 0x7f || ident[1] != 'E' || ident[2] != 'L' || ident[3] != 'F') {
                diagnostics.add("/proc/self/exe: not ELF");
                return FloatAbi.UNKNOWN;
            }
            if (ident[4] != 1) { // EI_CLASS: 1 = 32-bit (float ABI flags only apply to 32-bit ARM)
                diagnostics.add("/proc/self/exe: not 32-bit ELF (float ABI n/a)");
                return FloatAbi.UNKNOWN;
            }
            boolean le = ident[5] == 1; // EI_DATA: 1 = little-endian
            // e_flags is at offset 0x24 in the 32-bit ELF header.
            exe.seek(0x24);
            byte[] fb = new byte[4];
            exe.readFully(fb);
            long flags = readU32(fb, 0, le);
            if ((flags & EF_ARM_ABI_FLOAT_HARD) != 0) {
                diagnostics.add("ELF e_flags=0x" + Long.toHexString(flags) + " -> hard-float");
                return FloatAbi.HARD;
            }
            if ((flags & EF_ARM_ABI_FLOAT_SOFT) != 0) {
                diagnostics.add("ELF e_flags=0x" + Long.toHexString(flags) + " -> soft-float");
                return FloatAbi.SOFT;
            }
            diagnostics.add("ELF e_flags=0x" + Long.toHexString(flags) + " -> float ABI unset");
            return FloatAbi.UNKNOWN;
        } catch (Throwable t) {
            diagnostics.add("ELF float detection failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return FloatAbi.UNKNOWN;
        } finally {
            closeQuietly(exe);
        }
    }

    /** Presence of the hard-float vs soft-float dynamic loader. */
    private FloatAbi detectFloatAbiViaLdLinux() {
        if (new File("/lib/ld-linux-armhf.so.3").exists()
                || new File("/lib/arm-linux-gnueabihf/ld-linux-armhf.so.3").exists()) {
            diagnostics.add("ld-linux-armhf.so.3 present -> hard-float");
            return FloatAbi.HARD;
        }
        if (new File("/lib/ld-linux.so.3").exists()
                || new File("/lib/arm-linux-gnueabi/ld-linux.so.3").exists()) {
            diagnostics.add("ld-linux.so.3 (gnueabi) present -> soft-float");
            return FloatAbi.SOFT;
        }
        diagnostics.add("no known ld-linux -> float ABI unknown");
        return FloatAbi.UNKNOWN;
    }

    // ------------------------------------------------------------------------- libc detection ----

    /**
     * Detect the C library flavour (glibc vs musl). musl systems (e.g. Alpine) need musl-built
     * libraries; a glibc {@code .so} won't load there and vice-versa. All methods are pure Java so
     * they work in stripped containers.
     */
    private LibC detectLibC() {
        LibC libc = detectLibCViaMaps();
        if (libc != LibC.UNKNOWN) {
            return libc;
        }
        return detectLibCViaLoaderFile();
    }

    /**
     * Primary: read {@code /proc/self/maps} and look at the dynamic loader / libc actually mapped into
     * this JVM. musl maps {@code ld-musl-<arch>.so.1}; glibc maps {@code ld-linux*.so*} / {@code libc.so.6}.
     * This reflects the real runtime libc even in a stripped container.
     */
    private LibC detectLibCViaMaps() {
        try {
            for (String line : readLines(new File("/proc/self/maps"))) {
                String l = line.toLowerCase(Locale.ROOT);
                if (l.contains("ld-musl") || l.contains("libc.musl")) {
                    diagnostics.add("/proc/self/maps: musl loader mapped -> musl");
                    return LibC.MUSL;
                }
                if (l.contains("ld-linux") || l.contains("/libc.so.6") || l.contains("/libc-2.")) {
                    diagnostics.add("/proc/self/maps: glibc loader/libc mapped -> glibc");
                    return LibC.GNU;
                }
            }
            diagnostics.add("/proc/self/maps: no libc marker");
        } catch (Throwable t) {
            diagnostics.add("libc-via-maps failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        return LibC.UNKNOWN;
    }

    /** Backup: presence of the musl vs glibc dynamic loader on disk ({@code /lib/ld-musl-*.so.1}). */
    private LibC detectLibCViaLoaderFile() {
        String[] libNames = new File("/lib").list();
        if (libNames != null) {
            for (String name : libNames) {
                if (name.startsWith("ld-musl-")) {
                    diagnostics.add("/lib/" + name + " present -> musl");
                    return LibC.MUSL;
                }
            }
            for (String name : libNames) {
                if (name.startsWith("ld-linux") || name.startsWith("ld-2.")) {
                    diagnostics.add("/lib/" + name + " present -> glibc");
                    return LibC.GNU;
                }
            }
        }
        diagnostics.add("no ld-musl/ld-linux loader on disk -> libc unknown");
        return LibC.UNKNOWN;
    }

    // ------------------------------------------------------------------------------- helpers -----

    /** {@code armv7l} -> 7, {@code armv5tejl} -> 5, {@code aarch64} -> 8. */
    private static int parseArmvLevel(String s) {
        if (s.startsWith("aarch64") || s.startsWith("arm64")) {
            return 8;
        }
        if (s.startsWith("armv") && s.length() >= 5 && Character.isDigit(s.charAt(4))) {
            return s.charAt(4) - '0';
        }
        return -1;
    }

    /** {@code v7l} -> 7, {@code v5tel} -> 5, {@code aarch64} -> 8. */
    private static int parseVLevel(String s) {
        if (s.startsWith("aarch64")) {
            return 8;
        }
        if (s.startsWith("v") && s.length() >= 2 && Character.isDigit(s.charAt(1))) {
            return s.charAt(1) - '0';
        }
        return -1;
    }

    private static List<String> ordered(String... items) {
        Set<String> set = new LinkedHashSet<String>();
        for (String item : items) {
            if (item != null && item.length() > 0) {
                set.add(item);
            }
        }
        return new ArrayList<String>(set);
    }

    private static long readU32(byte[] b, int off, boolean littleEndian) {
        if (littleEndian) {
            return (b[off] & 0xffL) | ((b[off + 1] & 0xffL) << 8)
                    | ((b[off + 2] & 0xffL) << 16) | ((b[off + 3] & 0xffL) << 24);
        }
        return (b[off + 3] & 0xffL) | ((b[off + 2] & 0xffL) << 8)
                | ((b[off + 1] & 0xffL) << 16) | ((b[off] & 0xffL) << 24);
    }

    private static byte[] readAllBytes(File file, int max) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        try {
            byte[] buf = new byte[max];
            int total = 0;
            int read;
            while (total < max && (read = raf.read(buf, total, max - total)) > 0) {
                total += read;
            }
            byte[] out = new byte[total];
            System.arraycopy(buf, 0, out, 0, total);
            return out;
        } finally {
            closeQuietly(raf);
        }
    }

    private static List<String> readLines(File file) throws Exception {
        List<String> lines = new ArrayList<String>();
        BufferedReader r = new BufferedReader(new java.io.FileReader(file));
        try {
            String line;
            while ((line = r.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            r.close();
        }
        return lines;
    }

    /** Run a short-lived command and return its first stdout line, or {@code null} on any failure. */
    private static String readProcessLine(String... command) {
        Process process = null;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(false).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            try {
                line = reader.readLine();
            } finally {
                reader.close();
            }
            process.waitFor();
            return line;
        } catch (Throwable t) {
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static void closeQuietly(java.io.Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable ignore) {
                // ignore
            }
        }
    }
}
