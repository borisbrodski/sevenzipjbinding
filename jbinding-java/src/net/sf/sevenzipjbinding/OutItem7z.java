package net.sf.sevenzipjbinding;

import java.util.Date;

public class OutItem7z extends OutItem {
    private Long size;
    private Integer attributes;
    private String path;
    private Boolean dir;
    private Date modificationTime;
    private Date lastAccessTime;
    private Date creationTime;

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
     * Get property {@link PropID#ATTRIBUTES}.
     * 
     * @see PropID#ATTRIBUTES
     * @return attributes
     */
    public Integer getAttributes() {
        return attributes;
    }

    /**
     * Set property {@link PropID#ATTRIBUTES}.
     * 
     * @see PropID#ATTRIBUTES
     * @param attributes
     *            see {@link PropID#ATTRIBUTES}
     */
    public void setAttributes(Integer attributes) {
        this.attributes = attributes;
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
     * Get property {@link PropID#IS_FOLDER}.
     * 
     * @see PropID#IS_FOLDER
     * @return dir
     */
    public Boolean isDir() {
        return dir;
    }

    /**
     * Set property {@link PropID#IS_FOLDER}.
     * 
     * @see PropID#IS_FOLDER
     * @param dir
     *            see {@link PropID#IS_FOLDER}
     */
    public void setDir(Boolean dir) {
        this.dir = dir;
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

    /**
     * Get property {@link PropID#LAST_ACCESS_TIME}.
     * 
     * @see PropID#LAST_ACCESS_TIME
     * @return last access time
     */
    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * Set property {@link PropID#LAST_ACCESS_TIME}.
     * 
     * @see PropID#LAST_ACCESS_TIME
     * @param lastAccessTime
     *            see {@link PropID#LAST_ACCESS_TIME}
     */
    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    /**
     * Get property {@link PropID#CREATION_TIME}.
     * 
     * @see PropID#CREATION_TIME
     * @return creation time
     */
    public Date getCreationTime() {
        return creationTime;
    }

    /**
     * Set property {@link PropID#CREATION_TIME}.
     * 
     * @see PropID#CREATION_TIME
     * @param creationTime
     *            see {@link PropID#CREATION_TIME}
     */
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
