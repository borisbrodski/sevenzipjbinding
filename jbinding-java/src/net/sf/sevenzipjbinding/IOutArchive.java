package net.sf.sevenzipjbinding;

/**
 * Interface combining {@link IOutCreateArchive} and {@link IOutUpdateArchive}. Supports creating new archives and
 * updating existing archives. More information see JavaDocs of both interfaces.
 * 
 * @see IOutCreateArchive
 * @see IOutUpdateArchive
 * 
 * @param <E>
 *            Type of the call back class
 * 
 * @author Boris Brodski
 * @since 2.0
 */
public interface IOutArchive<E extends IOutItemCallbackBase> extends IOutCreateArchive<E>, IOutUpdateArchive<E> {

}
