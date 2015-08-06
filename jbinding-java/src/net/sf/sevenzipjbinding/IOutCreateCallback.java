package net.sf.sevenzipjbinding;

import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Interface to provide information needed to create a new archive or update existing one.
 * 
 * @param <E>
 *            Implementation of the {@link IOutItemAllFormats} interface or of a one of the specified
 *            <code>IOutItem*</code> interfaces.
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutCreateCallback<E extends IOutItemBase> extends IProgress {
    /**
     * Notify about last archive create/update operation result.
     * 
     * @param operationResultOk
     *            <code>true</code> - last archive create/update operation was a success, <code>false</code> - last
     *            archive update operation failed.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setOperationResult(boolean operationResultOk) throws SevenZipException;

    /**
     * Get information about archive item with index <code>index</code> being created or updated. Example:
     * 
     * <pre>
     * IOutItemZip outItem = outItemFactory.createOutItem()
     * 
     * outItem.setPropertySize(size);
     * outItem.setPropertyPath("readme.txt");
     * // ...
     * 
     * return outItem;
     * </pre>
     * 
     * @param index
     *            0-based index of the item get data. Same index returned by {@link IOutItemBase#getIndex()} of the
     *            <code>outItem</code> object.
     * @param outItemFactory
     *            or object to be filled with information about the archive item with index <code>index</code> for the
     *            create/update operation
     * @return <code>outItem</code> object with all necessary information for the current operation about the archive
     *         item
     * @throws SevenZipException
     *             any errors or exceptions in the user code wrapped into the {@link SevenZipException}. The exception
     *             will be passed to the original call to the 7-Zip-JBinding method, like
     *             {@link IOutArchive#createArchive(ISequentialOutStream, int, IOutCreateCallback)}
     */
    public E getItemInformation(int index, OutItemFactory<E> outItemFactory) throws SevenZipException;

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
     *            preinitialized instance of the {@link IOutItemBase} (actually type E) to be filled with the data
     *            relevant for the create/update operation being processed.
     * @throws SevenZipException
     *             any errors or exceptions in the user code wrapped into the {@link SevenZipException}. The exception
     *             will be passed to the original call to the 7-Zip-JBinding method, like
     *             {@link IOutArchive#createArchive(ISequentialOutStream, int, IOutCreateCallback)}
     * 
     * @see IOutCreateCallback#getItemInformation(int, OutItemFactory)
     * @see IOutItemBase#setUserData(Object)
     */
    public void freeResources(int index, E outItem) throws SevenZipException;
}
