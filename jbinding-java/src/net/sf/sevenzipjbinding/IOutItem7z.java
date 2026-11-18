package net.sf.sevenzipjbinding;

import java.util.Date;

/**
 * 7-zip specific archive item data class. It contains all information about a single archive item, that is needed for a
 * create or an update archive operation. See {@link IOutItemBase} for details.
 * 
 * @see IOutItemBase
 * 
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public interface IOutItem7z extends IOutItemBase {

    /**
     * Get property {@link PropID#ATTRIBUTES}.
     * 
     * @see PropID#ATTRIBUTES
     * @return attributes
     */
    public Integer getPropertyAttributes();

    /**
     * Set property {@link PropID#ATTRIBUTES}.
     * 
     * @see PropID#ATTRIBUTES
     * @param attributes
     *            see {@link PropID#ATTRIBUTES}
     */
    public void setPropertyAttributes(Integer attributes);

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
     * Get property {@link PropID#IS_FOLDER}.
     * 
     * @see PropID#IS_FOLDER
     * @return dir
     */
    public Boolean getPropertyIsDir();

    /**
     * Set property {@link PropID#IS_FOLDER}.
     * 
     * @see PropID#IS_FOLDER
     * @param dir
     *            see {@link PropID#IS_FOLDER}
     */
    public void setPropertyIsDir(Boolean dir);

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

    /**
     * Get property {@link PropID#IS_ANTI}.
     * 
     * @see PropID#IS_ANTI
     * @return isAnti flag
     */
    public Boolean getPropertyIsAnti();

    /**
     * Set property {@link PropID#IS_ANTI}.
     * 
     * @see PropID#IS_ANTI
     * @param isAnti
     *            see {@link PropID#IS_ANTI}.
     */
    public void setPropertyIsAnti(Boolean isAnti);

    /**
     * Get property {@link PropID#COMMENT}.
     * 
     * @see PropID#COMMENT
     * @return comment
     */
    public String getPropertyComment();

    /**
     * Set property {@link PropID#COMMENT}.
     * <p>
     * The 7z format writer currently ignores this property.
     * 
     * @see PropID#COMMENT
     * @param comment
     *            see {@link PropID#COMMENT}
     */
    public void setPropertyComment(String comment);

    /**
     * Get property {@link PropID#ENCRYPTED}.
     * 
     * @see PropID#ENCRYPTED
     * @return encrypted flag
     */
    public Boolean getPropertyEncrypted();

    /**
     * Set property {@link PropID#ENCRYPTED}.
     * <p>
     * The 7z format writer currently ignores this property; to encrypt, provide a password
     * through {@link ICryptoGetTextPassword}.
     * 
     * @see PropID#ENCRYPTED
     * @param encrypted
     *            see {@link PropID#ENCRYPTED}
     */
    public void setPropertyEncrypted(Boolean encrypted);

    /**
     * Get property {@link PropID#CRC}.
     * 
     * @see PropID#CRC
     * @return CRC checksum
     */
    public Integer getPropertyCRC();

    /**
     * Set property {@link PropID#CRC}.
     * <p>
     * The 7z format writer currently ignores this property.
     * 
     * @see PropID#CRC
     * @param crc
     *            see {@link PropID#CRC}
     */
    public void setPropertyCRC(Integer crc);
}
