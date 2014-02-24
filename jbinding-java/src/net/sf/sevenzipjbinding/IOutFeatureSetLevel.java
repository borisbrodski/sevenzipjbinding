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
     * Note, that the meaning of compression level can differ through algorithms.
     * 
     * @param compressionLevel
     *            compression level to set. <code>-1</code> - use default
     */
    public void setLevel(int compressionLevel);
    // TODO Add SevenZipException

}
