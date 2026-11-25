package net.sf.sevenzipjbinding.util;

/**
 * Bits of a Unix file mode as stored by tar, cpio and, in the upper half of the Windows attributes, by zip and 7z
 * ({@link ItemType#unixMode(net.sf.sevenzipjbinding.IInArchive, int)}). Values as in <code>sys/stat.h</code>, written
 * in octal.
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public final class UnixMode {
    /** Mask of the file type bits. */
    public static final int S_IFMT = 0170000;
    /** Socket. */
    public static final int S_IFSOCK = 0140000;
    /** Symbolic link. */
    public static final int S_IFLNK = 0120000;
    /** Regular file. */
    public static final int S_IFREG = 0100000;
    /** Block device. */
    public static final int S_IFBLK = 0060000;
    /** Directory. */
    public static final int S_IFDIR = 0040000;
    /** Character device. */
    public static final int S_IFCHR = 0020000;
    /** Named pipe. */
    public static final int S_IFIFO = 0010000;

    /** Set-user-id bit. */
    public static final int S_ISUID = 04000;
    /** Set-group-id bit. */
    public static final int S_ISGID = 02000;
    /** Sticky bit. */
    public static final int S_ISVTX = 01000;

    /** Owner read, write, execute. */
    public static final int S_IRWXU = 0700;
    /** Owner read. */
    public static final int S_IRUSR = 0400;
    /** Owner write. */
    public static final int S_IWUSR = 0200;
    /** Owner execute. */
    public static final int S_IXUSR = 0100;
    /** Group read, write, execute. */
    public static final int S_IRWXG = 070;
    /** Group read. */
    public static final int S_IRGRP = 040;
    /** Group write. */
    public static final int S_IWGRP = 020;
    /** Group execute. */
    public static final int S_IXGRP = 010;
    /** Others read, write, execute. */
    public static final int S_IRWXO = 07;
    /** Others read. */
    public static final int S_IROTH = 04;
    /** Others write. */
    public static final int S_IWOTH = 02;
    /** Others execute. */
    public static final int S_IXOTH = 01;

    /** Mask of the permission bits including set-id and sticky bits. */
    public static final int PERMISSION_MASK = 07777;

    private UnixMode() {
    }

    /**
     * @param mode
     *            Unix mode
     * @return the file type bits of the mode, one of the <code>S_IF*</code> constants, or 0 if the mode has no type
     *         bits
     */
    public static int typeBits(int mode) {
        return mode & S_IFMT;
    }

    /**
     * @param mode
     *            Unix mode
     * @return the permission bits of the mode (including set-id and sticky bits), for example <code>0644</code>
     */
    public static int permissionBits(int mode) {
        return mode & PERMISSION_MASK;
    }
}
