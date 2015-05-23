package net.sf.sevenzipjbinding;

/**
 * Bzip2 specific archive item callback interface. Gather archive item properties during compression or update
 * operations.<br>
 * <br>
 * For the archive format independent (generic) archive item callback interface see {@link IOutItemCallback}.
 * 
 * @see IOutItemCallback
 * @author Boris Brodski
 * @since 2.0
 */
public interface IOutItemCallbackBZip2 extends IOutItemCallbackBase {
    /**
     * Get property {@link PropID#SIZE} of the created or updated archive item. See {@link PropID#SIZE} for details.
     * 
     * @see PropID#SIZE
     * @return size
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public long getSize() throws SevenZipException;
}
