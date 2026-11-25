package net.sf.sevenzipjbinding.util;

import java.util.OptionalInt;

/**
 * Bits of the Windows file attributes ({@link net.sf.sevenzipjbinding.PropID#ATTRIBUTES}) as reported by 7-Zip,
 * including 7-Zip's convention for Unix modes in zip and 7z archives: if {@link #UNIX_EXTENSION} is set, the upper 16
 * bits hold the Unix mode ({@link UnixMode}).
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public final class WindowsAttributes {
    /** FILE_ATTRIBUTE_READONLY */
    public static final int FILE_ATTRIBUTE_READONLY = 0x1;
    /** FILE_ATTRIBUTE_HIDDEN */
    public static final int FILE_ATTRIBUTE_HIDDEN = 0x2;
    /** FILE_ATTRIBUTE_SYSTEM */
    public static final int FILE_ATTRIBUTE_SYSTEM = 0x4;
    /** FILE_ATTRIBUTE_DIRECTORY */
    public static final int FILE_ATTRIBUTE_DIRECTORY = 0x10;
    /** FILE_ATTRIBUTE_ARCHIVE */
    public static final int FILE_ATTRIBUTE_ARCHIVE = 0x20;
    /** FILE_ATTRIBUTE_NORMAL */
    public static final int FILE_ATTRIBUTE_NORMAL = 0x80;
    /** FILE_ATTRIBUTE_TEMPORARY */
    public static final int FILE_ATTRIBUTE_TEMPORARY = 0x100;
    /** FILE_ATTRIBUTE_SPARSE_FILE */
    public static final int FILE_ATTRIBUTE_SPARSE_FILE = 0x200;
    /** FILE_ATTRIBUTE_REPARSE_POINT: a junction or a Windows symbolic link. */
    public static final int FILE_ATTRIBUTE_REPARSE_POINT = 0x400;
    /** FILE_ATTRIBUTE_COMPRESSED */
    public static final int FILE_ATTRIBUTE_COMPRESSED = 0x800;
    /** FILE_ATTRIBUTE_ENCRYPTED */
    public static final int FILE_ATTRIBUTE_ENCRYPTED = 0x4000;
    /** 7-Zip's flag: the upper 16 bits of the attributes hold the Unix mode (zip and 7z archives written on Unix). */
    public static final int UNIX_EXTENSION = 0x8000;

    private WindowsAttributes() {
    }

    /**
     * @param attributes
     *            Windows attributes of an item
     * @return the Unix mode stored in the upper 16 bits, or empty if {@link #UNIX_EXTENSION} isn't set
     */
    public static OptionalInt unixMode(int attributes) {
        if ((attributes & UNIX_EXTENSION) == 0) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(attributes >>> 16);
    }
}
