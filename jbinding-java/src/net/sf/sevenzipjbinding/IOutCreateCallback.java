package net.sf.sevenzipjbinding;

/**
 * Interface to provide information needed to create a new archive
 * 
 * @param <E>
 *            one of the {@link IOutItemCallbackBase} call back types
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
     * @return implementation of the item callback (extending one of the {@link IOutItemCallbackBase} interfaces)
     */
    public E getOutItemCallback(int index) throws SevenZipException;
}
