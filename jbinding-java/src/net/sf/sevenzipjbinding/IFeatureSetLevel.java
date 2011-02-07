package net.sf.sevenzipjbinding;

/**
 * 
 * Feature interface for setting compression level. Use {@link SevenZip#openOutArchive(ArchiveFormat)} to get
 * implementation of this interface.
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 * 
 */
public interface IFeatureSetLevel {

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
     * Note, that the meaning of compression level can differ through algorithms.
     * 
     * @param compressionLevel
     *            compression level to set
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void setLevel(int compressionLevel) throws SevenZipException;

}
