package net.sf.sevenzipjbinding;

import java.util.Date;

/**
 * Tar specific archive item callback interface. Gather archive item properties during compression or update operations.<br>
 * <br>
 * For the archive format independent (generic) archive item callback interface see {@link IOutItemCallback}.
 * 
 * @see IOutItemCallback
 * @author Boris Brodski
 * @since 2.0
 */
public interface IOutItemCallbackTar extends IOutItemCallbackBase {
    /**
     * Get property {@link PropID#POSIX_ATTRIB} of the created or updated archive item. See {@link PropID#POSIX_ATTRIB}
     * for details.
     * 
     * @see PropID#POSIX_ATTRIB
     * @return posix attributes
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public Integer getPosixAttributes() throws SevenZipException;

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
     * Get property {@link PropID#USER} of the created or updated archive item. See {@link PropID#USER} for details.
     * 
     * @see PropID#USER
     * @return user
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public String getUser() throws SevenZipException;

    /**
     * Get property {@link PropID#GROUP} of the created or updated archive item. See {@link PropID#GROUP} for details.
     * 
     * @see PropID#GROUP
     * @return group
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public String getGroup() throws SevenZipException;

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
