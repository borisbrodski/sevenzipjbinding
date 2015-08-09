package net.sf.sevenzipjbinding;

import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Interface to provide the progress information and get the information needed to create a new archive or update an
 * existing one.
 * 
 * @param <T>
 *            the type of the corresponding archive item data class (out item), like {@link IOutItem7z} or
 *            {@link IOutItemZip}. Use {@link IOutItemAllFormats} interface to support all available archive formats.
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutCreateCallback<T extends IOutItemBase> extends IProgress {
    /**
     * Notify about last archive create/update operation result.
     * 
     * @param operationResultOk
     *            <code>true</code> - last archive create/update operation was a success, <code>false</code> - last
     *            archive update operation failed.
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There are no guarantee, that there are no further call back methods will get called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     */
    public void setOperationResult(boolean operationResultOk) throws SevenZipException;

    /**
     * Get information about archive item with index <code>index</code> being created or updated. Implementation
     * example:
     * 
     * <pre>
     * IOutItemZip outItem = outItemFactory.createOutItem()
     * 
     * outItem.setDataSize(size);
     * outItem.setPropertyPath("readme.txt");
     * // ...
     * 
     * return outItem;
     * </pre>
     * 
     * @param index
     *            0-based index of the item to get data. Same index returned by {@link IOutItemBase#getIndex()} of the
     *            <code>outItem</code> object.
     * @param outItemFactory
     *            a factory to create a pre-initialized instance of the data object. The created object should be filled
     *            with information about the archive item with index <code>index</code>
     * @return data object created with the <code>outItemFactory</code> filled with all necessary information for the
     *         current operation
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There are no guarantee, that there are no further call back methods will get called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     */
    public T getItemInformation(int index, OutItemFactory<T> outItemFactory) throws SevenZipException;

    /**
     * Callback method to free or close user specific resources associated with the archive item with index
     * <code>index</code>. <br>
     * <br>
     * <i>Note:</i> Use {@link IOutItemBase#setUserData(Object)} method to pass additional parameters to this method.
     * 
     * @param index
     *            0-based index of the item get data. Same index returned by {@link IOutItemBase#getIndex()} of the
     *            <code>outItem</code> object.
     * @param outItem
     *            instance of the {@link IOutItemBase} (actually type E) returned by the corresponding
     *            {@link #getItemInformation(int, OutItemFactory)} method call
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There are no guarantee, that there are no further call back methods will get called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     * 
     * @see IOutCreateCallback#getItemInformation(int, OutItemFactory)
     * @see IOutItemBase#setUserData(Object)
     */
    public void freeResources(int index, T outItem) throws SevenZipException;
}
