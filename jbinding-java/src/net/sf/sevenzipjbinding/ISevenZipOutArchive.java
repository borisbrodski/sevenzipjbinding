package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to create a new archive or update an existing archive.<br>
 * <br>
 * Standard way to get implementation is to use {@link SevenZip}.<br>
 * <br>
 * The last call should be a call of the method {@link ISevenZipInArchive#close()}. After this call no more Methods
 * should be called.
 * 
 * @author Boris Brodski
 * @version 9.04-2.0
 * 
 */
public interface ISevenZipOutArchive {

    /**
     * Create/update items in archive. If {@link ISequentialOutStream} was created stand alone via {@link SevenZip}
     * class, only new archive creation is possible. To update an existing archive open it first and then use
     * {@link ISevenZipInArchive} to get a {@link ISevenZipOutArchive} implementation.
     * 
     * @param outStream
     *            output stream to get the new archive
     * @param numberOfItems
     *            number of items in the new archive
     * @param archiveUpdateCallback
     *            update call back object to provide more information for archive create/update operation.
     */
    public void updateItems(ISequentialOutStream outStream, int numberOfItems,
            IArchiveUpdateCallback archiveUpdateCallback);

    /**
     * Return archive format used with this instance of {@link IOutStream}
     * 
     * @return archive format used with this instance of {@link IOutStream}
     */
    public ArchiveFormat getArchiveFormat();

    /**
     * Set compression level
     * 
     * @param compressionLevel
     *            compression level to set
     */
    public void setLevel(int compressionLevel);
}
