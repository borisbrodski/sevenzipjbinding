package net.sf.sevenzipjbinding;

import java.util.Date;

public interface IOutItemGZip extends IOutItemBase {

    /**
     * Get property {@link PropID#PATH}.
     * 
     * @see PropID#PATH
     * @return path
     */
    public String getPropertyPath();

    /**
     * Set property {@link PropID#PATH}.
     * 
     * @see PropID#PATH
     * @param path
     *            see {@link PropID#PATH}
     */
    public void setPropertyPath(String path);

    /**
     * Get property {@link PropID#LAST_MODIFICATION_TIME}.
     * 
     * @see PropID#LAST_MODIFICATION_TIME
     * @return last modification time
     */
    public Date getPropertyLastModificationTime();

    /**
     * Set property {@link PropID#LAST_MODIFICATION_TIME}.
     * 
     * @see PropID#LAST_MODIFICATION_TIME
     * @param modificationTime
     *            see {@link PropID#LAST_MODIFICATION_TIME}
     */
    public void setPropertyLastModificationTime(Date modificationTime);
}
