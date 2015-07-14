package net.sf.sevenzipjbinding;

import java.util.Date;

public class OutItemGZip extends OutItem {
    private Long size;
    private String path;
    private Date modificationTime;

    /**
     * Get property {@link PropID#SIZE}.
     * 
     * @see PropID#SIZE
     * @return size.
     */
    public Long getSize() {
        return size;
    }

    /**
     * Set property {@link PropID#SIZE}.
     * 
     * @see PropID#SIZE
     * @param size
     *            see {@link PropID#SIZE}
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * Get property {@link PropID#PATH}.
     * 
     * @see PropID#PATH
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set property {@link PropID#PATH}.
     * 
     * @see PropID#PATH
     * @param path
     *            see {@link PropID#PATH}
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get property {@link PropID#LAST_MODIFICATION_TIME}.
     * 
     * @see PropID#LAST_MODIFICATION_TIME
     * @return last modification time
     */
    public Date getModificationTime() {
        return modificationTime;
    }

    /**
     * Set property {@link PropID#LAST_MODIFICATION_TIME}.
     * 
     * @see PropID#LAST_MODIFICATION_TIME
     * @param modificationTime
     *            see {@link PropID#LAST_MODIFICATION_TIME}
     */
    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }
}
