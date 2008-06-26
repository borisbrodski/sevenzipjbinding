package net.sf.sevenzip.simple;

import java.util.Date;

/**
 * This java interface represents a archive item. There are no corresponding
 * interface in original SevenZip api.<br>
 * <br>
 * 
 * This interface is a part of simplified 7-Zip interface.
 * 
 * 
 * @see IInArchive
 * @author Boris Brodski
 * @since 0.3
 */
public interface IInArchiveItem {

	/**
	 * Return path, name and extenstion of the file (item) in archive.<br>
	 * <br>
	 * Supported by all methods, <b>but</b>
	 * <ul>
	 * <li> <code>BZ2</code>
	 * <li> <code>RPM</code>
	 * <li> <code>Z</code>
	 * </ul>
	 * 
	 * @return name of item.<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public String getPath();

	/**
	 * Original size of the item. <code>-1</code> is returned, if no size
	 * known for this item.<br>
	 * <br>
	 * Supported by all methods, <b>but</b>
	 * <ul>
	 * <li> <code>BZ2</code>
	 * <li> <code>RPM</code>
	 * </ul>
	 * 
	 * @return Original size of the item or <code>-1</code> <br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Long getSize();

	/**
	 * Packed size of item in archive.<br>
	 * <br>
	 * Supported by all methods, <b>but</b>
	 * <ul>
	 * <li> <code>CAB</code>
	 * <li> <code>Chm</code>
	 * </ul>
	 * 
	 * @return packed size of item in archive<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Long getPackedSize();

	/**
	 * Return <code>true</code> if item represents a folder, otherwise
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> item is a folder, otherwise
	 *         <code>false</code><br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Boolean isFolder();

	/**
	 * Return item attributes.<br>
	 * <br>
	 * Supported by all methods
	 * <ul>
	 * <li> <code>Zip</code>
	 * <li> <code>7Z</code>
	 * <li> <code>Arj</code>
	 * <li> <code>Cab</code>
	 * <li> <code>Rar</code>
	 * </ul>
	 * 
	 * @return attributes of item<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Integer getAttributes();

	/**
	 * Return creation date and time of the item.<br>
	 * <br>
	 * Supported by all methods
	 * <ul>
	 * <li> <code>7Z</code>
	 * <li> <code>Rar</code>
	 * </ul>
	 * 
	 * @return creation date and time of the item<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Date getCreationTime();

	/**
	 * Return last access date and time of the item.<br>
	 * <br>
	 * Supported by all methods
	 * <ul>
	 * <li> <code>7Z</code>
	 * <li> <code>Rar</code>
	 * </ul>
	 * 
	 * @return last access date and time of the item.<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Date getLastAccessTime();

	/**
	 * Return last write date and time of the item.<br>
	 * <br>
	 * Supported by all methods, <b>but</b>
	 * <ul>
	 * <li> <code>BZ2</code>
	 * <li> <code>Chm</code>
	 * <li> <code>RPM</code>
	 * <li> <code>Split</code>
	 * <li> <code>Z</code>
	 * </ul>
	 * 
	 * @return last write date and time of the item<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Date getLastWriteTime();

	/**
	 * Return encryption status of the item.
	 * 
	 * @return <code>true</code> if the item is encrypted, otherwise
	 *         <code>false</code>.<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Boolean isEncrypted();

	/**
	 * Return <code>true</code> if item was commented, otherwise
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if item was commented, otherwise
	 *         <code>false</code>.<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Boolean isCommented();

	/**
	 * Return CRC checksum of the item content.
	 * 
	 * @return CRC checksum of the item content<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public Integer getCRC();

	/**
	 * Return string title of item compress method.
	 * 
	 * @return string title of item compress method<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public String getMethod();

	/**
	 * TODO: the purpose of position isn't clear
	 * 
	 * @return
	 */
	public Integer getPosition();

	/**
	 * Return host OS of item. TODO: The meening of the hostOS property is
	 * unknown
	 * 
	 * @return host OS of item.<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public String getHostOS();

	/**
	 * Return parent user of the item.
	 * 
	 * @return parent user of the item.<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public String getUser();

	/**
	 * Return parent group of the item.
	 * 
	 * @return parent group of the item.<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public String getGroup();

	/**
	 * Return comments for the item.
	 * 
	 * @return comments for the item.<br>
	 *         <code>null</code> will be returnd, if current archiv type
	 *         doesn't support this property.
	 */
	public String getComment();
}
