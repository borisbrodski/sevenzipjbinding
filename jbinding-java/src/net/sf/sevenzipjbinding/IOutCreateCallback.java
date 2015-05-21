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
public interface IOutCreateCallback<E extends IOutItemCallbackBase> extends IOutCreateCallbackBase {
    /**
     * Get the implementation of the item callback. This callback provides meta data for archive items being created or
     * updated.
     * 
     * @param index
     *            index of the item get meta data
     * @return create call back object to provide information for archive create/update operations. Should implements
     *         {@link IOutItemCallback} or one of the specified <code>IOutItemCallback*</code> interfaces.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public E getOutItemCallback(int index) throws SevenZipException;
}
