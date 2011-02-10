package net.sf.sevenzipjbinding;

/**
 * 
 * Feature interface for setting solid. Use {@link SevenZip#openOutArchive(ArchiveFormat)} to get
 * implementation of this interface.
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 * 
 */
public interface IFeatureSetSolid {

    /**
     * Put all files in a single solid block.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void setSolid() throws SevenZipException;

    /**
     * Put specified count of files in a single solid block.
     * 
     * @param countOfFilesPerBlock count of files per solid block.
     *
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void setSolidFiles(int countOfFilesPerBlock) throws SevenZipException;

    /**
     * Put specified count of bytes in a single solid block.
     * 
     * @param countOfBytesPerBlock count of bytes per solid block.
     *
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void setSolidSize(long countOfBytesPerBlock) throws SevenZipException;
}
