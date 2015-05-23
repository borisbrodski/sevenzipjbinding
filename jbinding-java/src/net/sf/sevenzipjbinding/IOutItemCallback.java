package net.sf.sevenzipjbinding;

/**
 * Archive item callback interface. Gather archive item properties during compression or update operations. Supports all
 * archive formats.<br>
 * <br>
 * Note: for different archive formats some properties may be mandatory, optional or even not supported.<br>
 * <br>
 * For the archive format specific archive item callback interface see <code>IOutItemCallbackXxx</code> interfaces.
 * 
 * @see PropID
 * @see IOutItemCallback
 * @author Boris Brodski
 * @since 2.0
 */
public interface IOutItemCallback extends //
        IOutItemCallback7z, //
        IOutItemCallbackGZip, //
        IOutItemCallbackTar, //
        IOutItemCallbackZip, //
        IOutItemCallbackBZip2 {

}
