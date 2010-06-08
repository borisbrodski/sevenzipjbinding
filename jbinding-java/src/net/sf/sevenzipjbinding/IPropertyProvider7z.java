package net.sf.sevenzipjbinding;

import java.util.Date;

/**
 * Provide properties for updating 7z archive items
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IPropertyProvider7z {
    /**
     * {@link PropID#PATH} Property.
     * 
     * @param index
     *            index of the archive item being queried. (0-based)
     * @return value of the property of the item with index <code>index</code>
     */
    public String getPath(int index);

    /**
     * {@link PropID#IS_FOLDER} Property.
     * 
     * @param index
     *            index of the archive item being queried. (0-based)
     * @return value of the property of the item with index <code>index</code>
     */
    public boolean isDirectory(int index);

    /**
     * {@link PropID#IS_ANTI} Property.
     * 
     * @param index
     *            index of the archive item being queried. (0-based)
     * @return value of the property of the item with index <code>index</code>
     */
    public boolean isAnti(int index);

    /**
     * {@link PropID#SIZE} Property.
     * 
     * @param index
     *            index of the archive item being queried. (0-based)
     * @return value of the property of the item with index <code>index</code>
     */
    public long getSize(int index);

    /**
     * {@link PropID#ATTRIBUTES} Property.
     * 
     * @param index
     *            index of the archive item being queried. (0-based)
     * @return value of the property of the item with index <code>index</code>
     */
    public int getAttributes(int index);

    /**
     * {@link PropID#CREATION_TIME} Property. 7-Zip asks for this property only, if extra configured . TODO
     * 
     * @param index
     *            index of the archive item being queried. (0-based)
     * @return value of the property of the item with index <code>index</code>
     */
    public Date getCreationTime(int index);

    /**
     * {@link PropID#LAST_MODIFICATION_TIME} Property.
     * 
     * @param index
     *            index of the archive item being queried. (0-based)
     * @return value of the property of the item with index <code>index</code>
     */
    public Date getLastModificationTime(int index);

    /**
     * {@link PropID#LAST_ACCESS_TIME} Property. 7-Zip asks for this property only, if extra configured. TODO
     * 
     * @param index
     *            index of the archive item being queried. (0-based)
     * @return value of the property of the item with index <code>index</code>
     */
    public Date getLastAccessTime(int index);

}
