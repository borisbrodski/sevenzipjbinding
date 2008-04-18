package net.sf.sevenzip;

import java.util.Date;

/**
 * This java interface represents a archive item. There are no corresponding
 * interface in original SevenZip api.<br>
 * <br>
 * 
 * This interface is a part of simplified interface for C++
 * <code>IInArchive</code> class.
 * 
 * 
 * 
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
	 * @return name of item
	 * 
	 * @exception PropertyNotSupportedException
	 *                raised, if current archive format doesn't support this
	 *                property.
	 */
	public String getPath() throws PropertyNotSupportedException;

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
	 * @return Original size of the item or <code>-1</code>
	 */
	public long getSize();

	/**
	 * Packed size of item in archive.<br>
	 * <br>
	 * Supported by all methods, <b>but</b>
	 * <ul>
	 * <li> <code>CAB</code>
	 * <li> <code>Chm</code>
	 * </ul>
	 * 
	 * @return packed size of item in archive
	 */
	public long getPackedSize();

	/**
	 * Return <code>true</code> if item represents a folder, otherwise
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> item is a folder, otherwise
	 *         <code>false</code>
	 */
	public boolean isFolder();

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
	 * @return attributes of item
	 */
	public int getAttributes();

	/**
	 * Return creation date and time of the item.<br>
	 * <br>
	 * Supported by all methods
	 * <ul>
	 * <li> <code>7Z</code>
	 * <li> <code>Rar</code>
	 * </ul>
	 * 
	 * @return creation date and time of the item
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
	 * @return last access date and time of the item.
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
	 * @return last write date and time of the item
	 */
	public Date getLastWriteTime();

	/**
	 * Return encryption status of the item.
	 * 
	 * @return <code>true</code> if the item is encrypted, otherwise
	 *         <code>false</code>.
	 */
	public boolean isEncrypted();

	/**
	 * Return <code>true</code> if item was commented, otherwise
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if item was commented, otherwise
	 *         <code>false</code>
	 */
	public boolean isCommented();

	/**
	 * Return CRC checksum of the item content.
	 * 
	 * @return CRC checksum of the item content
	 */
	public int getCRC();

	/**
	 * Return string title of item compress method.
	 * 
	 * @return string title of item compress method
	 */
	public String getMethod();

	/**
	 * TODO: the perpose of position isn't clear
	 * 
	 * @return
	 */
	public int getPosition();

	/**
	 * Return host OS of item. TODO: The meening of the hostOS property is
	 * unknown
	 * 
	 * @return host OS of item
	 */
	public String getHostOS();

	/**
	 * Return parent user of the item.
	 * 
	 * @return parent user of the item
	 */
	public String getUser();

	/**
	 * Return parent group of the item.
	 * 
	 * @return parent group of the item
	 */
	public String getGroup();

	/**
	 * Return comments for the item.
	 * 
	 * @return comments for the item
	 */
	public String getComment();
}
