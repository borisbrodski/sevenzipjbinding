package net.sf.sevenzipjbinding;

import java.util.Date;

/**
 * GZip specific archive item callback interface. Gather archive item properties during compression or update
 * operations.<br>
 * <br>
 * For the archive format independent (generic) archive item callback interface see {@link IOutItemCallback}.
 * 
 * @see IOutItemCallback
 * @author Boris Brodski
 * @since 2.0
 */
public interface IOutItemCallbackGZip extends IOutItemCallbackBase {
    /**
     * Get property {@link PropID#SIZE} of the created or updated archive item. See {@link PropID#SIZE} for details.
     * 
     * @see PropID#SIZE
     * @return size
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public long getSize() throws SevenZipException;

    /**
     * Get property {@link PropID#PATH} of the created or updated archive item. See {@link PropID#PATH} for details.
     * 
     * @see PropID#PATH
     * @return path
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public String getPath() throws SevenZipException;

    /**
     * Get property {@link PropID#LAST_MODIFICATION_TIME} of the created or updated archive item. See
     * {@link PropID#LAST_MODIFICATION_TIME} for details.
     * 
     * @see PropID#LAST_MODIFICATION_TIME
     * @return last modification time
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public Date getModificationTime() throws SevenZipException;
}
