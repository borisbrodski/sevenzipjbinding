package net.sf.sevenzipjbinding.impl;

import java.util.Date;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;

public class OutItem implements IOutItemAllFormats {
    private int index;
    private Object userData;

    private ISequentialInStream dataStream;

    private Long propertySize;
    private Integer propertyAttributes;
    private Integer propertyPosixAttributes;
    private String propertyPath;
    private Boolean propertyIsDir;
    private Date propertyLastModificationTime;
    private Date propertyLastAccessTime;
    private Date propertyCreationTime;
    private String propertyUser;
    private String propertyGroup;
    private Boolean propertyIsAnti;

    private Boolean updateIsNewData;
    private Boolean updateIsNewProperties;
    private Integer updateOldArchiveItemIndex;

    private IOutArchive<?> outArchive;

    OutItem(IOutArchive<?> outArchive, int index) {
        this.outArchive = outArchive;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    public int getIndex() {
        return index;
    }

    public IOutArchive<?> getOutArchive() {
        return outArchive;
    }

    /**
     * {@inheritDoc}
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * {@inheritDoc}
     */
    public ArchiveFormat getArchiveFormat() {
        return outArchive.getArchiveFormat();
    }

    /**
     * {@inheritDoc}
     */
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    /**
     * {@inheritDoc}
     */
    public Long getPropertySize() {
        return propertySize;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertySize(Long size) {
        this.propertySize = size;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getPropertyAttributes() {
        return propertyAttributes;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyAttributes(Integer attributes) {
        this.propertyAttributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getPropertyPosixAttributes() {
        return propertyPosixAttributes;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyPosixAttributes(Integer posixAttributes) {
        this.propertyPosixAttributes = posixAttributes;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyPath() {
        return propertyPath;
    }

    /**
     * {@inheritDoc}
     */

    public void setPropertyPath(String path) {
        this.propertyPath = path;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean getPropertyIsDir() {
        return propertyIsDir;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyIsDir(Boolean dir) {
        this.propertyIsDir = dir;
    }

    /**
     * {@inheritDoc}
     */
    public Date getPropertyLastModificationTime() {
        return propertyLastModificationTime;
    }

    /**
     * {@inheritDoc}
     */

    public void setPropertyLastModificationTime(Date modificationTime) {
        this.propertyLastModificationTime = modificationTime;
    }

    /**
     * {@inheritDoc}
     */
    public Date getPropertyLastAccessTime() {
        return propertyLastAccessTime;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyLastAccessTime(Date lastAccessTime) {
        this.propertyLastAccessTime = lastAccessTime;
    }

    /**
     * {@inheritDoc}
     */
    public Date getPropertyCreationTime() {
        return propertyCreationTime;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyCreationTime(Date creationTime) {
        this.propertyCreationTime = creationTime;
    }

    /**
     * {@inheritDoc}
     */
    public ISequentialInStream getDataStream() {
        return dataStream;
    }

    /**
     * {@inheritDoc}
     */
    public void setDataStream(ISequentialInStream stream) {
        this.dataStream = stream;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyUser() {
        return propertyUser;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyUser(String user) {
        this.propertyUser = user;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyGroup() {
        return propertyGroup;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyGroup(String group) {
        this.propertyGroup = group;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean getPropertyIsAnti() {
        return propertyIsAnti;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyIsAnti(Boolean isAnti) {
        this.propertyIsAnti = isAnti;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean getUpdateIsNewData() {
        return updateIsNewData;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdateIsNewData(Boolean updateIsNewData) {
        this.updateIsNewData = updateIsNewData;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean getUpdateIsNewProperties() {
        return updateIsNewProperties;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdateIsNewProperties(Boolean updateIsNewProperties) {
        this.updateIsNewProperties = updateIsNewProperties;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getUpdateOldArchiveItemIndex() {
        return updateOldArchiveItemIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdateOldArchiveItemIndex(Integer updateOldArchiveItemIndex) {
        this.updateOldArchiveItemIndex = updateOldArchiveItemIndex;
    }
}
