package net.sf.sevenzipjbinding;

/**
 * Interface to provide information needed to create a new archive. Extends {@link IOutCreateCallbackBase} adding
 * archive item property non-generic callback. For generic archive item callback see {@link IOutCreateCallbackGeneric}
 * interface.
 * 
 * @see IOutCreateCallbackGeneric
 * @see IOutCreateCallbackBase
 * @see IOutUpdateCallback
 * @param <E>
 *            Implementation of the {@link IOutItemCallback} interface or of a one of the specified
 *            <code>IOutItemCallback*</code> interfaces.
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutCreateCallback<E extends OutItem> extends IOutCreateCallbackBase {
    /**
     * Get information about archive items being created or updated.
     * 
     * @param index
     *            index of the item get data
     * @return information about the archive items for archive create/update operation
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public E getOutItem(int index) throws SevenZipException;
}
