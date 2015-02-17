package net.sf.sevenzipjbinding;

/**
 * Interface to provide information needed to update an archive (create a new archive based on an existing one) Based on
 * {@link IOutCreateCallback}.
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutUpdateCallback<E extends IOutItemCallbackBase> extends IOutUpdateCallbackBase,
        IOutCreateCallback<E> {

}
