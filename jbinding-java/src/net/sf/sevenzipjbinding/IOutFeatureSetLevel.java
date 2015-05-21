package net.sf.sevenzipjbinding;

/**
 * Feature interface for setting compression level. Use {@link SevenZip#openOutArchive(ArchiveFormat)} or one of the
 * <code>SevenZip.openOutArchiveXxx()</code> methods to get implementation of this interface.
 * 
 * @see IOutCreateArchive
 * @author Boris Brodski
 * @version 9.13-2.00
 * 
 */
public interface IOutFeatureSetLevel {

    /**
     * Set compression level:
     * <ul>
     * <li>0 - Copy mode (no compression)
     * <li>1 - Fastest
     * <li>3 - Fast
     * <li>5 - Normal
     * <li>7 - Maximum
     * <li>9 - Ultra
     * </ul>
     * Note, that the meaning of compression level can differ through different archive formats.
     * 
     * @param compressionLevel
     *            compression level to set. <code>-1</code> - use default
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setLevel(int compressionLevel) throws SevenZipException;
}
