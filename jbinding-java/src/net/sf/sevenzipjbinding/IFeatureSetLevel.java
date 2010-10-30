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
     * Set compression level
     * 
     * @param compressionLevel
     *            compression level to set
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void setLevel(int compressionLevel) throws SevenZipException;

}
