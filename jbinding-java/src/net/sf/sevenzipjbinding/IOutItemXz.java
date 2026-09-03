package net.sf.sevenzipjbinding;

/**
 * Xz specific archive item data class. It contains all information about a single archive item, that is needed for a
 * create or an update archive operation. See {@link IOutItemBase} for details.
 * <p>
 * Like BZip2 and GZip, Xz is a single-stream archive format that stores no filename inside the archive, so this
 * interface adds no extra properties on top of {@link IOutItemBase}.
 *
 * @see IOutItemBase
 *
 * @author Boris Brodski
 * @since 23.01
 */
public interface IOutItemXz extends IOutItemBase {

}
