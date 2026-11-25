package net.sf.sevenzipjbinding.util;

import java.util.OptionalInt;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * Kind of an archive item: regular file, directory, symbolic link, hard link, device, ... Archive formats describe the
 * kind in different properties, and most of them are optional: {@link PropID#IS_FOLDER}, {@link PropID#SYM_LINK} and
 * {@link PropID#HARD_LINK} (tar), the Unix mode in {@link PropID#POSIX_ATTRIB} (tar, cpio, ...) or in the upper 16
 * bits of {@link PropID#ATTRIBUTES} when its {@link WindowsAttributes#UNIX_EXTENSION} bit is set (zip, 7z), and the
 * Windows attributes themselves ({@link WindowsAttributes#FILE_ATTRIBUTE_REPARSE_POINT}). {@link #of(IInArchive, int)}
 * combines them, so no user code needs the bit masks.
 * <p>
 * A tar hard link carries the mode of a regular file; only {@link PropID#HARD_LINK} tells it apart. Links stored as
 * their target's content (7z without the symbolic link option) are regular files, as the archive says.
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public enum ItemType {
    /** A file with content. */
    REGULAR_FILE,
    /** A directory. */
    DIRECTORY,
    /** A symbolic link; {@link PropID#SYM_LINK} holds the target when the format stores it as a property. */
    SYMBOLIC_LINK,
    /** A hard link; {@link PropID#HARD_LINK} holds the target. */
    HARD_LINK,
    /** A Windows reparse point (a junction or a Windows symbolic link), from {@link PropID#ATTRIBUTES}. */
    REPARSE_POINT,
    /** A character or block device, see {@link UnixMode#S_IFCHR} and {@link UnixMode#S_IFBLK}. */
    DEVICE,
    /** A named pipe. */
    FIFO,
    /** A Unix socket. */
    SOCKET,
    /** The format tells nothing about the kind of the item (no mode type bits, no directory flag). */
    UNKNOWN;

    /**
     * Classify the item <code>index</code> of the archive.
     *
     * @param inArchive
     *            open archive
     * @param index
     *            index of the item
     * @return the kind of the item, {@link #UNKNOWN} if the archive doesn't tell
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs
     */
    public static ItemType of(IInArchive inArchive, int index) throws SevenZipException {
        if (!isEmpty(inArchive.getStringProperty(index, PropID.SYM_LINK))) {
            return SYMBOLIC_LINK;
        }
        if (!isEmpty(inArchive.getStringProperty(index, PropID.HARD_LINK))) {
            return HARD_LINK;
        }
        Object attributes = inArchive.getProperty(index, PropID.ATTRIBUTES);
        int windowsAttributes = attributes instanceof Integer ? ((Integer) attributes).intValue() : 0;
        if ((windowsAttributes & WindowsAttributes.FILE_ATTRIBUTE_REPARSE_POINT) != 0) {
            return REPARSE_POINT;
        }
        OptionalInt mode = unixMode(inArchive, index);
        if (mode.isPresent()) {
            ItemType type = fromUnixMode(mode.getAsInt());
            if (type != UNKNOWN) {
                return type;
            }
        }
        if (Boolean.TRUE.equals(inArchive.getProperty(index, PropID.IS_FOLDER))
                || (windowsAttributes & WindowsAttributes.FILE_ATTRIBUTE_DIRECTORY) != 0) {
            return DIRECTORY;
        }
        if (Boolean.FALSE.equals(inArchive.getProperty(index, PropID.IS_FOLDER)) || attributes instanceof Integer) {
            return REGULAR_FILE;
        }
        return UNKNOWN;
    }

    /**
     * The Unix mode of the item (type and permission bits, see {@link UnixMode}): {@link PropID#POSIX_ATTRIB} if the
     * format stores it (tar, cpio, ...), else the upper 16 bits of {@link PropID#ATTRIBUTES} if its
     * {@link WindowsAttributes#UNIX_EXTENSION} bit is set (zip and 7z archives written on Unix).
     *
     * @param inArchive
     *            open archive
     * @param index
     *            index of the item
     * @return the Unix mode, or empty if the archive doesn't store one for this item
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs
     */
    public static OptionalInt unixMode(IInArchive inArchive, int index) throws SevenZipException {
        Object posixAttributes = inArchive.getProperty(index, PropID.POSIX_ATTRIB);
        if (posixAttributes instanceof Integer) {
            return OptionalInt.of(((Integer) posixAttributes).intValue());
        }
        Object attributes = inArchive.getProperty(index, PropID.ATTRIBUTES);
        if (attributes instanceof Integer) {
            return WindowsAttributes.unixMode(((Integer) attributes).intValue());
        }
        return OptionalInt.empty();
    }

    /**
     * Classify by the type bits of a Unix mode ({@link UnixMode#S_IFMT}).
     *
     * @param mode
     *            Unix mode, for example <code>0100644</code>
     * @return the kind, {@link #UNKNOWN} if the mode has no type bits (permissions only)
     */
    public static ItemType fromUnixMode(int mode) {
        switch (mode & UnixMode.S_IFMT) {
        case UnixMode.S_IFREG:
            return REGULAR_FILE;
        case UnixMode.S_IFDIR:
            return DIRECTORY;
        case UnixMode.S_IFLNK:
            return SYMBOLIC_LINK;
        case UnixMode.S_IFCHR:
        case UnixMode.S_IFBLK:
            return DEVICE;
        case UnixMode.S_IFIFO:
            return FIFO;
        case UnixMode.S_IFSOCK:
            return SOCKET;
        default:
            return UNKNOWN;
        }
    }

    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}
