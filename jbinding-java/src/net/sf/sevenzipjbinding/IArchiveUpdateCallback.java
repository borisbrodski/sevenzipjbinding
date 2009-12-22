package net.sf.sevenzipjbinding;

/**
 * Interface to receive and provide information needed to open an archive for update
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IArchiveUpdateCallback extends IProgress {
    /**
     * Determine, whether the archive item data has changed.
     * 
     * @param index
     *            index of the archive item in archive (starting from 0)
     * @return <code>true</code> - the archive item with index <code>index</code> has new data (always true for new
     *         archives). <code>false</code> - the archive item data from the old archive can be reused.
     */
    public boolean isNewData(int index);

    /**
     * Determine, whether the archive item properties have changed.
     * 
     * @param index
     *            index of the archive item in archive (starting from 0)
     * @return <code>true</code> - the archive item with index <code>index</code> has new properties (always true for
     *         new archives). <code>false</code> - the archive item properties from the old archive can be reused.
     */
    public boolean isNewProperties(int index);

    /**
     * Determine index of the archive item in the archive being updated.
     * 
     * @param index
     *            index of the archive item in the new archive (starting from 0)
     * @return corresponding index of the archive item in the old archive (starting from 0). <code>-1</code> if there is
     *         no corresponding archive item in the old archive or if doesn't matter.
     */
    public int getOldArchiveItemIndex(int index);

    /**
     * Retrieve value of property <code>propID</code> for the archive item with index <code>index</code>.
     * 
     * @param index
     *            index of an archive item to retrieve value from (starting from 0)
     * @param propID
     *            the property to retrieve
     * @return retrieved value
     */
    public Object getProperty(int index, PropID propID);

    /**
     * Return sequential in-stream for the archive item with index <code>index</code> to read and compress the content
     * of the item.
     * 
     * @param index
     *            index of the item to read content of (starting from 0)
     * @return sequential in-stream pointed to the content of the archive item with index <code>index</code>
     */
    public ISequentialInStream getStream(int index);

    /**
     * Notify about last archive update operation result.
     * 
     * @param operationResultOk
     *            <code>true</code> - last archive update operation was a success, <code>false</code> - last archive
     *            update operation failed.
     */
    public void setOperationResult(boolean operationResultOk);
}
