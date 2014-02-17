package net.sf.sevenzipjbinding;

/**
 * Interface to provide information needed to create a new archive
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IArchiveCreateCallback extends IProgress {
    /**
     * Retrieve value of property <code>propID</code> for the archive item with index <code>index</code>.
     * 
     * @param index
     *            index of an archive item to retrieve value from (starting from 0)
     * @param propID
     *            the property to retrieve
     * @return retrieved value
     */
    public Object getProperty(int index, PropID propID) throws SevenZipException;

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
