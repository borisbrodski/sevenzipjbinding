package net.sf.sevenzipjbinding;

/**
 * Interface to provide information needed to create a new archive (base class).
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
     */
    public ISequentialInStream getStream(int index) throws SevenZipException;

    /**
     * Notify about last archive update operation result.
     * 
     * @param operationResultOk
     *            <code>true</code> - last archive update operation was a success, <code>false</code> - last archive
     *            update operation failed.
     */
    public void setOperationResult(boolean operationResultOk) throws SevenZipException;
}
