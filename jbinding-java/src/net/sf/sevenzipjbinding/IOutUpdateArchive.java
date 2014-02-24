package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to update an existing archive.<br>
 * Standard way to get implementation is to use {@link IInArchive#getConnectedOutArchive()}.<br>
 * No explicit closing is necessary. Connected out-archive get closed automatically when corresponding in-archive get
 * closed.
 * 
 * @author Boris Brodski
 * @version 9.04-2.0
 * 
 */
public interface IOutUpdateArchive<E extends IOutItemCallbackBase> {

    /**
     * Create/update items in archive. If {@link ISequentialOutStream} was created stand alone via {@link SevenZip}
     * class, only new archive creation is possible. To update an existing archive open it first and then use
     * {@link IInArchive} to get a {@link IOutUpdateArchive} implementation.
     * 
     * @param outStream
     *            output stream to get the new archive
     * @param numberOfItems
     *            number of items in the new archive
     * @param archiveUpdateCallback
     *            update call back object to provide more information for archive create/update operation.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void updateItems(ISequentialOutStream outStream, int numberOfItems,
            IOutUpdateCallback<E> archiveUpdateCallback) throws SevenZipException;

    /**
     * Return archive format used with this instance of {@link IOutStream}
     * 
     * @return archive format used with this instance of {@link IOutStream}
     */
    public ArchiveFormat getArchiveFormat();
}
