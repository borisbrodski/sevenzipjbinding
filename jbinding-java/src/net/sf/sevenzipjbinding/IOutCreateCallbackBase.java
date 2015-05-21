package net.sf.sevenzipjbinding;

/**
 * Base interface to provide information needed to create a new archive or to update existing archive.
 * 
 * @see IOutCreateCallback
 * @see IOutUpdateCallback
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutCreateCallbackBase extends IProgress {
    /**
     * Return sequential in-stream for the archive item with index <code>index</code> to read and compress the content
     * of the item.
     * 
     * @param index
     *            index of the item to read content of (starting from 0)
     * @return sequential in-stream pointed to the content of the archive item with index <code>index</code>
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public ISequentialInStream getStream(int index) throws SevenZipException;

    /**
     * Notify about last archive update operation result.
     * 
     * @param operationResultOk
     *            <code>true</code> - last archive update operation was a success, <code>false</code> - last archive
     *            update operation failed.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setOperationResult(boolean operationResultOk) throws SevenZipException;
}
