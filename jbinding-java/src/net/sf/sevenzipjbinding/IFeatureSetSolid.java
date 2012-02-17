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
public interface IFeatureSetSolid {

    /**
     * Put all files in a single solid block. If deactivated, some other solid options like 'solid files' will take no
     * effect.
     * 
     * @param solid
     *            <code>true</code> - use default configuration, <code>false</code> - deactivate solid block mode
     */
    public void setSolid(boolean solid);

    /**
     * Put specified count of files in a single solid block.
     * 
     * @param countOfFilesPerBlock
     *            count of files per solid block. <code>-1</code> - use default configuration
     */
    public void setSolidFiles(int countOfFilesPerBlock);

    /**
     * Put specified count of bytes in a single solid block.
     * 
     * @param countOfBytesPerBlock
     *            count of bytes per solid block. <code>-1</code> - use default configuration.
     */
    public void setSolidSize(long countOfBytesPerBlock);

    /**
     * Put all files with the same extension following each other in the order of packing in a solid block
     * 
     * @param solidExtension
     *            <code>true</code> - activate this feature, <code>false</code> - use default configuration
     */
    public void setSolidExtension(boolean solidExtension);
}
