package net.sf.sevenzipjbinding;

import net.sf.sevenzipjbinding.impl.OutItem;

/**
 * Base interface of the archive item data interfaces:
 * <ul>
 * <li>archive format specific interfaces <code>IOutItemXxx</code>, like {@link IOutItem7z}
 * <li>archive format independent interface {@link IOutItemAllFormats}
 * </ul>
 * The single known implementation is {@link OutItem}. This base interface provides access to the getters and setters
 * methods, that are shared by all archive formats.<br>
 * <br>
 * The purpose of the archive format specific interfaces <code>IOutItemXxx</code> is to hide methods unrelated to the
 * corresponding archive format. For example, GZip format doesn't support the <code>attributes</code> property and so
 * the {@link IOutItemGZip} interface doesn't contain corresponding getters and setter. The Zip archive format on the
 * other hand does support the <code>attributes</code> property defining the methods:
 * <ul>
 * <li> {@link IOutItemZip#getPropertyAttributes()}
 * <li> {@link IOutItemZip#setPropertyAttributes(Integer)}
 * </ul>
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public interface IOutItemBase {
    /**
     * Return corresponding IOutArchive object.
     * 
     * @return IOutArchive
     */
    public IOutArchive<?> getOutArchive();

    /**
     * Return current archive format
     * 
     * @return archive format
     */
    public ArchiveFormat getArchiveFormat();

    /**
     * Return the index of the item in the archive that this object describes.
     * 
     * @return the index of the item in the archive
     */
    public int getIndex();

    /**
     * Return some custom user data associates with this object.
     * 
     * @return custom data previously saved by the user
     * @see #getUserData()
     */
    public Object getUserData();

    /**
     * Set some custom user data associates with this object.
     * 
     * @param userData
     *            custom data
     * @see #getUserData()
     */
    public void setUserData(Object userData);

    /**
     * Return sequential in-stream for the archive item to read and compress the content.<br>
     * <br>
     * <i>Note:</i> {@link ISequentialInStream} interface doesn't require closing, but in most real world applications a
     * special care should be taken to properly close corresponding implementation objects.
     * 
     * @return sequential in-stream
     */
    public ISequentialInStream getDataStream();

    /**
     * Set sequential in-stream for the archive item to read and compress the content.<br>
     * <br>
     * <i>Note:</i> {@link ISequentialInStream} interface doesn't require closing, but in most real world applications a
     * special care should be taken to properly close corresponding implementation objects.
     * 
     * @param stream
     *            sequential in-stream
     */
    public void setDataStream(ISequentialInStream stream);

    /**
     * Get property {@link PropID#SIZE}.
     * 
     * @see PropID#SIZE
     * @return size.
     */
    public Long getPropertySize();

    /**
     * Set property {@link PropID#SIZE}.
     * 
     * @see PropID#SIZE
     * @param size
     *            see {@link PropID#SIZE}
     */
    public void setPropertySize(Long size);

    /**
     * Get whether the archive item data has changed. <br>
     * <br>
     * <i>Note:</i> only relevant for archive update operations!
     * 
     * @return <code>true</code> - the archive item has new data (always true for new archives),<br>
     *         <code>false</code> - the archive item data from the old archive can be reused.
     */
    public Boolean getUpdateIsNewData();

    /**
     * Set whether the archive item data has changed. <br>
     * <br>
     * <i>Note:</i> only relevant for archive update operations!
     * 
     * @param updateIsNewData
     *            <code>true</code> - the archive item has new data (always true for new archives),<br>
     *            <code>false</code> - the archive item data from the old archive can be reused.
     */
    public void setUpdateIsNewData(Boolean updateIsNewData);

    /**
     * Get whether the archive item properties have changed. <br>
     * <br>
     * <i>Note:</i> only relevant for archive update operations!
     * 
     * @return <code>true</code> - the archive item has new properties (always true for new archives).<br>
     *         <code>false</code> - the archive item properties from the old archive can be reused.
     */
    public Boolean getUpdateIsNewProperties();

    /**
     * Set whether the archive item properties have changed. <br>
     * <br>
     * <i>Note:</i> only relevant for archive update operations!
     * 
     * @param updateIsNewProperties
     *            <code>true</code> - the archive item has new properties (always true for new archives).<br>
     *            <code>false</code> - the archive item properties from the old archive can be reused.
     */
    public void setUpdateIsNewProperties(Boolean updateIsNewProperties);

    /**
     * Get the index of the archive item in the archive being updated (old archive). <br>
     * <br>
     * <i>Note:</i> only relevant for archive update operations!
     * 
     * @return corresponding index of the archive item in the old archive (starting from 0).<br>
     *         <code>-1</code> if there is no corresponding archive item in the old archive or if doesn't matter (for
     *         new archives).
     */
    public Integer getUpdateOldArchiveItemIndex();

    /**
     * Set the index of the archive item in the archive being updated (old archive). <br>
     * <br>
     * <i>Note:</i> only relevant for archive update operations!
     * 
     * @param updateOldArchiveItemIndex
     *            corresponding index of the archive item in the old archive (starting from 0).<br>
     *            <code>-1</code> if there is no corresponding archive item in the old archive or if doesn't matter (for
     *            new archives).
     */
    public void setUpdateOldArchiveItemIndex(Integer updateOldArchiveItemIndex);
}
