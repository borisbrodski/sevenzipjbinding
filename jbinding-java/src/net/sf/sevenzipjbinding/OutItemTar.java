package net.sf.sevenzipjbinding;

import java.util.Date;

public class OutItemTar extends OutItem {
    private Long size;
    private String path;
    private Boolean dir;
    private Integer posixAttributes;
    private Date modificationTime;
    private String user;
    private String group;

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
     * Get property {@link PropID#POSIX_ATTRIB}.
     * 
     * @see PropID#POSIX_ATTRIB
     * @return posix attributes
     */
    public Integer getPosixAttributes() {
        return posixAttributes;
    }

    /**
     * Set property {@link PropID#POSIX_ATTRIB}.
     * 
     * @see PropID#POSIX_ATTRIB
     * @param posixAttributes
     *            see {@link PropID#POSIX_ATTRIB}
     */
    public void setPosixAttributes(Integer posixAttributes) {
        this.posixAttributes = posixAttributes;
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
     * Get property {@link PropID#USER}.
     * 
     * @see PropID#USER
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * Set property {@link PropID#USER}.
     * 
     * @see PropID#USER
     * @param user
     *            see {@link PropID#USER}
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Get property {@link PropID#GROUP}.
     * 
     * @see PropID#GROUP
     * @return group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set property {@link PropID#GROUP}.
     * 
     * @see PropID#GROUP
     * @param group
     *            see {@link PropID#GROUP}
     */
    public void setGroup(String group) {
        this.group = group;
    }
}
