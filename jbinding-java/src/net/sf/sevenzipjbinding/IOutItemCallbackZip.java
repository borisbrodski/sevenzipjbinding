package net.sf.sevenzipjbinding;

import java.util.Date;

/**
 * Zip specific archive item callback interface. Gather archive item properties during compression or update operations.<br>
 * <br>
 * For the archive format independent (generic) archive item callback interface see {@link IOutItemCallback}.
 * 
 * @see IOutItemCallback
 * @author Boris Brodski
 * @since 2.0
 */
public interface IOutItemCallbackZip extends IOutItemCallbackBase {
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
     * Get property {@link PropID#ATTRIBUTES} of the created or updated archive item. See {@link PropID#ATTRIBUTES} for
     * details.
     * 
     * @see PropID#ATTRIBUTES
     * @return attributes
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public Integer getAttributes() throws SevenZipException;

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
     * Get property {@link PropID#IS_FOLDER} of the created or updated archive item. See {@link PropID#IS_FOLDER} for
     * details.
     * 
     * @see PropID#IS_FOLDER
     * @return <code>true</code> archive item is a folder, <code>false</code> otherwise
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public boolean isDir() throws SevenZipException;

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

    /**
     * Get property {@link PropID#LAST_ACCESS_TIME} of the created or updated archive item. See
     * {@link PropID#LAST_ACCESS_TIME} for details.
     * 
     * @see PropID#LAST_ACCESS_TIME
     * @return last access time
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public Date getLastAccessTime() throws SevenZipException;

    /**
     * Get property {@link PropID#CREATION_TIME} of the created or updated archive item. See
     * {@link PropID#CREATION_TIME} for details.
     * 
     * @see PropID#CREATION_TIME
     * @return creation time
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public Date getCreationTime() throws SevenZipException;
}
