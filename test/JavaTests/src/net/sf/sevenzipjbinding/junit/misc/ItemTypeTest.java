package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.util.ItemType;
import net.sf.sevenzipjbinding.util.UnixMode;
import net.sf.sevenzipjbinding.util.WindowsAttributes;

/**
 * {@link ItemType}: item kinds over the properties of tar, zip (Unix and Windows made), 7z and gzip (issue #91).
 * Fixtures in <code>testdata/misc/item-types</code>: <code>types.tar</code> (GNU tar: directory, files, symbolic link,
 * hard link, fifo), <code>types-unix.zip</code> (Info-ZIP with Unix modes, symbolic link stored by mode only),
 * <code>types-windows.zip</code> (Windows attributes only: a junction with FILE_ATTRIBUTE_REPARSE_POINT, a file, a
 * read-only file), <code>types.7z</code> (7-Zip on Linux, Unix modes in the attributes) and <code>regular.txt.gz</code>
 * (no attributes at all).
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public class ItemTypeTest extends JUnitNativeTestBase<VoidContext> {
    private static final String TEST_DATA_PATH = "testdata/misc/item-types/";

    @Test
    public void tar() throws Exception {
        IInArchive inArchive = open("types.tar", ArchiveFormat.TAR);
        Map<String, ItemType> types = types(inArchive);
        assertEquals(ItemType.DIRECTORY, types.get("dir"));
        assertEquals(ItemType.REGULAR_FILE, types.get("dir/file.txt"));
        assertEquals(ItemType.REGULAR_FILE, types.get("regular.txt"));
        assertEquals(ItemType.SYMBOLIC_LINK, types.get("symlink.txt"));
        assertEquals(ItemType.HARD_LINK, types.get("hardlink.txt")); // mode of a regular file, HARD_LINK property
        assertEquals(ItemType.FIFO, types.get("fifo"));
        assertEquals(6, types.size());

        assertEquals(OptionalInt.of(UnixMode.S_IFDIR | 0775), ItemType.unixMode(inArchive, index(inArchive, "dir")));
        assertEquals(OptionalInt.of(UnixMode.S_IFLNK | 0777),
                ItemType.unixMode(inArchive, index(inArchive, "symlink.txt")));
        assertEquals(OptionalInt.of(UnixMode.S_IFREG | 0664),
                ItemType.unixMode(inArchive, index(inArchive, "hardlink.txt")));
    }

    @Test
    public void zipWithUnixModes() throws Exception {
        IInArchive inArchive = open("types-unix.zip", ArchiveFormat.ZIP);
        Map<String, ItemType> types = types(inArchive);
        assertEquals(ItemType.DIRECTORY, types.get("dir"));
        assertEquals(ItemType.REGULAR_FILE, types.get("dir/file.txt"));
        assertEquals(ItemType.REGULAR_FILE, types.get("regular.txt"));
        assertEquals(ItemType.SYMBOLIC_LINK, types.get("symlink.txt")); // no SYM_LINK property, S_IFLNK in the mode
        assertEquals(4, types.size());
        assertEquals(OptionalInt.of(UnixMode.S_IFREG | 0664),
                ItemType.unixMode(inArchive, index(inArchive, "dir/file.txt")));
    }

    @Test
    public void zipWithWindowsAttributes() throws Exception {
        IInArchive inArchive = open("types-windows.zip", ArchiveFormat.ZIP);
        Map<String, ItemType> types = types(inArchive);
        assertEquals(ItemType.REPARSE_POINT, types.get("junction"));
        assertEquals(ItemType.REGULAR_FILE, types.get("file.txt"));
        assertEquals(ItemType.REGULAR_FILE, types.get("readonly.txt"));
        assertEquals(3, types.size());
        assertFalse(ItemType.unixMode(inArchive, index(inArchive, "file.txt")).isPresent());
        assertEquals(WindowsAttributes.FILE_ATTRIBUTE_DIRECTORY | WindowsAttributes.FILE_ATTRIBUTE_REPARSE_POINT,
                inArchive.getProperty(index(inArchive, "junction"), PropID.ATTRIBUTES));
    }

    @Test
    public void sevenZipWithUnixModes() throws Exception {
        IInArchive inArchive = open("types.7z", ArchiveFormat.SEVEN_ZIP);
        Map<String, ItemType> types = types(inArchive);
        assertEquals(ItemType.DIRECTORY, types.get("dir"));
        assertEquals(ItemType.REGULAR_FILE, types.get("dir/file.txt"));
        assertEquals(ItemType.REGULAR_FILE, types.get("regular.txt"));
        assertEquals(ItemType.REGULAR_FILE, types.get("symlink.txt")); // stored as a file with the target as content
        assertEquals(4, types.size());
        assertEquals(OptionalInt.of(UnixMode.S_IFDIR | 0775), ItemType.unixMode(inArchive, index(inArchive, "dir")));
    }

    @Test
    public void gzipWithoutAttributes() throws Exception {
        IInArchive inArchive = open("regular.txt.gz", ArchiveFormat.GZIP);
        assertEquals(ItemType.REGULAR_FILE, ItemType.of(inArchive, 0));
        assertFalse(ItemType.unixMode(inArchive, 0).isPresent());
    }

    @Test
    public void fromUnixMode() {
        assertEquals(ItemType.REGULAR_FILE, ItemType.fromUnixMode(0100644));
        assertEquals(ItemType.DIRECTORY, ItemType.fromUnixMode(040755));
        assertEquals(ItemType.SYMBOLIC_LINK, ItemType.fromUnixMode(0120777));
        assertEquals(ItemType.DEVICE, ItemType.fromUnixMode(020660));
        assertEquals(ItemType.DEVICE, ItemType.fromUnixMode(060660));
        assertEquals(ItemType.FIFO, ItemType.fromUnixMode(010644));
        assertEquals(ItemType.SOCKET, ItemType.fromUnixMode(0140755));
        assertEquals(ItemType.UNKNOWN, ItemType.fromUnixMode(0644)); // permissions only
        assertEquals(0100000, UnixMode.typeBits(0100644));
        assertEquals(0644, UnixMode.permissionBits(0100644));
        assertEquals(04755, UnixMode.permissionBits(0104755));
    }

    @Test
    public void unixModeInWindowsAttributes() {
        assertEquals(OptionalInt.of(0100664), WindowsAttributes.unixMode(0x81b48000));
        assertEquals(OptionalInt.of(040775), WindowsAttributes.unixMode(0x41fd8010));
        assertFalse(WindowsAttributes.unixMode(WindowsAttributes.FILE_ATTRIBUTE_ARCHIVE).isPresent());
        assertFalse(WindowsAttributes.unixMode(0x81b40000).isPresent()); // upper bits without the flag
    }

    private IInArchive open(String name, ArchiveFormat format) throws Exception {
        RandomAccessFileInStream inStream = new RandomAccessFileInStream(
                new RandomAccessFile(TEST_DATA_PATH + name, "r"));
        addCloseable(inStream);
        IInArchive inArchive = SevenZip.openInArchive(format, inStream);
        addCloseable(inArchive);
        return inArchive;
    }

    private static Map<String, ItemType> types(IInArchive inArchive) throws Exception {
        Map<String, ItemType> types = new LinkedHashMap<String, ItemType>();
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            types.put(path(inArchive, i), ItemType.of(inArchive, i));
        }
        return types;
    }

    private static int index(IInArchive inArchive, String path) throws Exception {
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            if (path.equals(path(inArchive, i))) {
                return i;
            }
        }
        throw new AssertionError(path + " not in the archive");
    }

    /**
     * 7-Zip reports paths with the separator of the running OS ('\\' on Windows).
     */
    private static String path(IInArchive inArchive, int index) throws Exception {
        return inArchive.getStringProperty(index, PropID.PATH).replace('\\', '/');
    }
}
