package net.sf.sevenzipjbinding;

/**
 * 
 * Feature interface for setting solid. Use {@link SevenZip#openOutArchive(ArchiveFormat)} to get implementation of this
 * interface.
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 * 
 */
public interface IOutFeatureSetSolid {

    /**
     * Set solid archive option. If deactivated, some other solid options like 'solid files' will take no effect.
     * 
     * @param solid
     *            <code>true</code> - use default configuration, <code>false</code> - deactivate solid block mode
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setSolid(boolean solid) throws SevenZipException;

    /**
     * Put specified count of files in a single solid block. See {@link #setSolid(boolean)}.
     * 
     * @see #setSolid(boolean)
     * @param countOfFilesPerBlock
     *            count of files per solid block. <code>-1</code> - use default configuration
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setSolidFiles(int countOfFilesPerBlock) throws SevenZipException;

    /**
     * Put specified count of bytes in a single solid block. See {@link #setSolid(boolean)}.
     * 
     * @see #setSolid(boolean)
     * @param countOfBytesPerBlock
     *            count of bytes per solid block. <code>-1</code> - use default configuration.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setSolidSize(long countOfBytesPerBlock) throws SevenZipException;

    /**
     * Put all files with the same extension following each other in the order of packing in a solid block. See
     * {@link #setSolid(boolean)}.
     * 
     * @see #setSolid(boolean)
     * @param solidExtension
     *            <code>true</code> - activate this feature, <code>false</code> - use default configuration
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setSolidExtension(boolean solidExtension) throws SevenZipException;
}
