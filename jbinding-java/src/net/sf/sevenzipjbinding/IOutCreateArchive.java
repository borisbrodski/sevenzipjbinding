package net.sf.sevenzipjbinding;

import java.io.Closeable;

/**
 * The interface provides functionality to create a new archive or update an existing archive.<br>
 * TODO Split creation and update. <br>
 * Standard way to get implementation is to use {@link SevenZip}.<br>
 * <br>
 * The last call should be a call to the method {@link IInArchive#close()}. After this call no more Methods should be
 * called. TODO Remove this one.
 * 
 * @author Boris Brodski
 * @version 9.04-2.0
 * 
 */
public interface IOutCreateArchive<E extends IOutItemCallbackBase> extends Closeable {

    /**
     * Create new archive. To update an existing archive open it first and then use
     * {@link IInArchive#getConnectedOutArchive()} to get an instance of {@link IOutUpdateArchive}.
     * 
     * @param outStream
     *            output stream to get the new archive
     * @param numberOfItems
     *            number of items in the new archive
     * @param outCreateCallback
     *            create call back object to provide more information for archive create/update operation.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void createArchive(ISequentialOutStream outStream, int numberOfItems,
            IOutCreateCallback<? extends E> outCreateCallback) throws SevenZipException;

    /**
     * Return archive format used with this instance of {@link IOutStream}
     * 
     * @return archive format used with this instance of {@link IOutStream}
     */
    public ArchiveFormat getArchiveFormat();
}
