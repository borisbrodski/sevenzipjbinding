package net.sf.sevenzipjbinding.simple;

import java.util.Date;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * This Java interface represents an archive item. There is no corresponding interface in the original SevenZip API.<br>
 * <br>
 *
 * This interface is a part of the simplified 7-Zip-JBinding interface.
 *
 * <br>
 * <br>
 * <i>NOTE:</i> Some properties may only be available after the extraction operation completes.<br>
 * Example: PACKED_SIZE of the LZMA archives.
 * 
 * @see ISimpleInArchive
 * @author Boris Brodski
 * @since 4.65-1
 */
public interface ISimpleInArchiveItem {

    /**
     * Full path, name and extension of the file inside the archive. Example: <code>'dir/file.ext'</code>. Please note
     * that stream archive formats such as gzip do not support this property, since they always compress a single file
     * (or stream).<br>
     * 
     * @return full path, name and extension of the item in archive.<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public String getPath() throws SevenZipException;

    /**
     * Original (uncompressed) size of the item in bytes.
     *
     * @return original size of the item in bytes<br>
     *         <code>null</code> will be returned, if the current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Long getSize() throws SevenZipException;

    /**
     * Size of the packed item in archive. Sometimes <code>0</code> will be returned. It means either <i>unknown</i> or
     * the item shares compressed data with other items and so take no additional space in archive.
     * 
     * @return packed size of item in archive<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Long getPackedSize() throws SevenZipException;

    /**
     * Returns a flag indicating whether the item represents a folder or not. Please note that some archive formats do
     * not define special items for folders. In this case you may get an item with a path <code>'dir/file'</code>
     * without having an item for <code>'dir'</code> at all. <br>
     * <br>
     * 
     * @return <code>true</code> if item is a folder, otherwise <code>false</code>. <code>false</code> is returned, if
     *         archive format doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public boolean isFolder() throws SevenZipException;

    /**
     * Return item attributes.<br>
     * <br>
     * Supported by the following formats:
     * <ul>
     * <li> <code>Zip</code>
     * <li> <code>7Z</code>
     * <li> <code>Arj</code>
     * <li> <code>Cab</code>
     * <li> <code>Rar</code>
     * </ul>
     * 
     * @return attributes of item<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Integer getAttributes() throws SevenZipException;

    /**
     * Return creation date and time of the item.<br>
     * <br>
     * Supported by the following formats:
     * <ul>
     * <li> <code>7Z</code>
     * <li> <code>Rar</code>
     * </ul>
     * 
     * @return creation date and time of the item<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Date getCreationTime() throws SevenZipException;

    /**
     * Return last access date and time of the item.<br>
     * <br>
     * Supported by the following formats:
     * <ul>
     * <li> <code>7Z</code>
     * <li> <code>Rar</code>
     * </ul>
     * 
     * @return last access date and time of the item.<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Date getLastAccessTime() throws SevenZipException;

    /**
     * Return last write date and time of the item.<br>
     * <br>
     * Supported by all formats <b>except</b> the following:
     * <ul>
     * <li> <code>BZ2</code>
     * <li> <code>Chm</code>
     * <li> <code>RPM</code>
     * <li> <code>Split</code>
     * <li> <code>Z</code>
     * </ul>
     * 
     * @return last write date and time of the item<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Date getLastWriteTime() throws SevenZipException;

    /**
     * Flag indicating whether the item is encrypted or not.
     * 
     * @return <code>true</code> if item is encrypted, otherwise <code>false</code>. <code>false</code> is returned, if
     *         archive format doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public boolean isEncrypted() throws SevenZipException;

    /**
     * Return <code>true</code> if item was commented, otherwise <code>false</code>.
     * 
     * @return <code>true</code> if item was commented, otherwise <code>false</code>.<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Boolean isCommented() throws SevenZipException;

    /**
     * Return CRC checksum of the item content.
     * 
     * @return CRC checksum of the item content<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Integer getCRC() throws SevenZipException;

    /**
     * Return string title of item compress method.
     * 
     * @return string title of item compress method<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public String getMethod() throws SevenZipException;

    /**
     * Return the value of the item's <code>POSITION</code> property. This is a rarely used, format-specific property; for
     * most archive formats it is not defined.
     *
     * @return value of the <code>POSITION</code> property<br>
     *         <code>null</code> will be returned, if the current archive type doesn't support this property.
     *
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public Integer getPosition() throws SevenZipException;

    /**
     * Return the host operating system recorded for the item, i.e. the operating system on which the item was added to
     * the archive. This is mainly relevant for formats such as Zip and RAR.
     *
     * @return host OS of the item.<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public String getHostOS() throws SevenZipException;

    /**
     * Return parent user of the item.
     * 
     * @return parent user of the item.<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public String getUser() throws SevenZipException;

    /**
     * Return parent group of the item.
     * 
     * @return parent group of the item.<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public String getGroup() throws SevenZipException;

    /**
     * Return comments for the item.
     * 
     * @return comments for the item.<br>
     *         <code>null</code> will be returned, if current archive type doesn't support this property.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public String getComment() throws SevenZipException;

    /**
     * Extract one archive item. Use <code>sequentialOutStream</code> to output data.<br>
     * <br>
     * <b>WARNING:</b> this is a very slow operation when called repeatedly.
     * 
     * @param sequentialOutStream
     *            output stream to use
     * 
     * @return result of operation
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public ExtractOperationResult extractSlow(ISequentialOutStream sequentialOutStream) throws SevenZipException;

    /**
     * Extract one archive item. Use <code>sequentialOutStream</code> to output data.<br>
     * <br>
     * <b>WARNING:</b> this is a very slow operation when called repeatedly.
     * 
     * @param sequentialOutStream
     *            output stream to use
     * @param password
     *            password to use
     * @return result of operation
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public ExtractOperationResult extractSlow(ISequentialOutStream sequentialOutStream, String password)
            throws SevenZipException;

    /**
     * Returns the archive item index
     * 
     * @return the archive item index
     */
    public int getItemIndex();
}
