package net.sf.sevenzipjbinding;

/**
 * Enumeration of the file-time formats used by 7-Zip, mirroring 7-Zip's native <code>NFileTimeType</code> constants.
 * 
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public enum NFileTimeType {
    /**
     * Microsoft Windows file time format
     */
    WINDOWS, //

    /**
     * Unix file time format
     */
    UNIX, //

    /**
     * Old Microsoft DOS file time format
     */
    DOS //
}
