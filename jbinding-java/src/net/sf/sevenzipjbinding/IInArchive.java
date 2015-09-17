package net.sf.sevenzipjbinding;

import java.io.Closeable;
import java.util.Arrays;

import net.sf.sevenzipjbinding.simple.ISimpleInArchive;

/**
 * The interface provides functionality to query archive and archive item parameters, but also to extract item content. <br>
 * <br>
 * Standard way to get implementation is to use {@link SevenZip}.<br>
 * <br>
 * The last call should be a call to the method {@link IInArchive#close()}. After this call no more methods should be
 * called.
 * 
 * @author Boris Brodski
 * @version 4.65-1
 * 
 */
public interface IInArchive extends Closeable {
    /**
     * Close archive. This method should be the last one called on the implementation of this interface. After this call
     * no more methods should be called.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void close() throws SevenZipException;

    /**
     * Returns count of items in archive. Depending on archive type directories are considered to be items or not.
     * 
     * @return count of items in archive
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public int getNumberOfItems() throws SevenZipException;

    /**
     * Get value of property <code>propID</code> of the item with the index <code>index</code>.
     * 
     * @param index
     *            index of item to get property value. 0 - first archive item.
     * @param propID
     *            property to get value of
     * @return value of property <code>propID</code> of item with index <code>index</code>
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public Object getProperty(int index, PropID propID) throws SevenZipException;

    /**
     * Return property content in human readable form. Example for {@link PropID#ATTRIBUTES}: <code>D</code> for a
     * directory.
     * 
     * @param index
     *            index of item in archive to get property of. 0 - first archive item.
     * @param propID
     *            property to return
     * @return property <code>propID</code> of item with id <code>index</code> in human readable form.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public String getStringProperty(int index, PropID propID) throws SevenZipException;

    /**
     * Extract archive items with indices <code>indices</code>. <br>
     * Note: passing sorted <code>indices</code> array is more efficient. But it isn't suggested to manually sort
     * indices with something like {@link Arrays#sort(int[])}. Sort indices only, if you can do it quicker, that generic
     * sort algorithms: <code>O(n*log(n))</code>.
     * 
     * @param indices
     *            (optional) array of indices of archive items to extract.<br>
     *            <code>null</code> - all archive items.
     * 
     * @param testMode
     *            <code>true</code> - test archive items only<br>
     *            <code>false</code> - extract archive items
     * 
     * @param extractCallback
     *            extraction callback object. Optional implementation of {@link ICryptoGetTextPassword}.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void extract(int[] indices, boolean testMode, IArchiveExtractCallback extractCallback)
            throws SevenZipException;

    /**
     * Extract one item from archive. Multiple calls of this method are inefficient for some archive types.
     * 
     * @param index
     *            index of the item to extract. 0 - first archive item.
     * @param outStream
     *            sequential output stream to get content of the item
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     * @return result status of the extraction
     */
    public ExtractOperationResult extractSlow(int index, ISequentialOutStream outStream) throws SevenZipException;

    /**
     * Extract one item from archive. Multiple calls of this method are inefficient for some archive types.
     * 
     * @param index
     *            index of the item to extract. 0 - first archive item.
     * @param outStream
     *            sequential output stream to get content of the item
     * @param password
     *            password to use
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     * @return result status of the extraction
     */
    public ExtractOperationResult extractSlow(int index, ISequentialOutStream outStream, String password)
            throws SevenZipException;

    /**
     * Get value of archive property <code>propID</code>.
     * 
     * @param propID
     *            property to get value of
     * @return value of archive property <code>propID</code>
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public Object getArchiveProperty(PropID propID) throws SevenZipException;

    /**
     * Return property value in human readable form.
     * 
     * @param propID
     *            property to return
     * @return property <code>propID</code> of archive in human readable form.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public String getStringArchiveProperty(PropID propID) throws SevenZipException;

    /**
     * Return count of properties of each archive item
     * 
     * @return count of properties of each archive item
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public int getNumberOfProperties() throws SevenZipException;

    /**
     * Get information about archive item property with index <code>index</code>.
     * 
     * @see #getNumberOfProperties()
     * @param index
     *            property index
     * @return information about property of archive item
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public PropertyInfo getPropertyInfo(int index) throws SevenZipException;

    /**
     * Get count of properties of archive
     * 
     * @return count of properties of archive
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public int getNumberOfArchiveProperties() throws SevenZipException;

    /**
     * Get information about archive property with index <code>index</code>.<br>
     * 
     * @see #getNumberOfArchiveProperties()
     * 
     * @param index
     *            property index
     * @return information about archive property
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public PropertyInfo getArchivePropertyInfo(int index) throws SevenZipException;

    /**
     * Return simple 7-Zip interface for the archive
     * 
     * @return simple 7-Zip interface for the archive
     */
    public ISimpleInArchive getSimpleInterface();

    /**
     * Return archive format of the opened archive.
     * 
     * @return archive format of the opened archive
     */
    public ArchiveFormat getArchiveFormat();

    /**
     * Get an instance of {@link IOutUpdateArchive} connected to the current archive. This is a part of the archive
     * format non-specific API. The new instance allows modification of the currently opened archive. Multiple call of
     * this methods return the same instance. Closing the returned instance of {@link IOutUpdateArchive} isn't
     * necessary, since it will be closed automatically. This happens when the parent instance of the {@link IInArchive}
     * get closed. Calls to the {@link IOutArchive#close()} methods of such connected instances will be ignored.
     * 
     * @return an instance of the {@link IOutUpdateArchive} interface
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public IOutUpdateArchive<IOutItemAllFormats> getConnectedOutArchive() throws SevenZipException;

    /**
     * Get an instance of {@link IOutUpdateArchive7z} connected to the current archive. This is a part of the archive
     * format specific API. The new instance only allows modification of the currently opened 7z archive. Multiple call
     * of this methods return the same instance. Closing the returned instance of {@link IOutUpdateArchive7z} isn't
     * necessary, since it will be closed automatically. This happens when the parent instance of the {@link IInArchive}
     * get closed. Calls to the {@link IOutArchive#close()} methods of such connected instances will be ignored.
     * 
     * @return an instance of the {@link IOutUpdateArchive} interface
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public IOutUpdateArchive7z getConnectedOutArchive7z() throws SevenZipException;

    /**
     * Get an instance of {@link IOutUpdateArchiveZip} connected to the current archive. This is a part of the archive
     * format specific API. The new instance only allows modification of the currently opened Zip archive. Multiple call
     * of this methods return the same instance. Closing the returned instance of {@link IOutUpdateArchiveZip} isn't
     * necessary, since it will be closed automatically. This happens when the parent instance of the {@link IInArchive}
     * get closed. Calls to the {@link IOutArchive#close()} methods of such connected instances will be ignored.
     * 
     * @return an instance of the {@link IOutUpdateArchive} interface
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public IOutUpdateArchiveZip getConnectedOutArchiveZip() throws SevenZipException;

    /**
     * Get an instance of {@link IOutUpdateArchiveTar} connected to the current archive. This is a part of the archive
     * format specific API. The new instance only allows modification of the currently opened Tar archive. Multiple call
     * of this methods return the same instance. Closing the returned instance of {@link IOutUpdateArchiveTar} isn't
     * necessary, since it will be closed automatically. This happens when the parent instance of the {@link IInArchive}
     * get closed. Calls to the {@link IOutArchive#close()} methods of such connected instances will be ignored.
     * 
     * @return an instance of the {@link IOutUpdateArchive} interface
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public IOutUpdateArchiveTar getConnectedOutArchiveTar() throws SevenZipException;

    /**
     * Get an instance of {@link IOutUpdateArchiveGZip} connected to the current archive. This is a part of the archive
     * format specific API. The new instance only allows modification of the currently opened GZip archive. Multiple
     * call of this methods return the same instance. Closing the returned instance of {@link IOutUpdateArchiveGZip} 
     * isn't necessary, since it will be closed automatically. This happens when the parent instance of the
     * {@link IInArchive} get closed. Calls to the {@link IOutArchive#close()} methods of such connected instances will
     * be ignored.
     * 
     * @return an instance of the {@link IOutUpdateArchive} interface
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public IOutUpdateArchiveGZip getConnectedOutArchiveGZip() throws SevenZipException;

    /**
     * Get an instance of {@link IOutUpdateArchiveBZip2} connected to the current archive. This is a part of the archive
     * format specific API. The new instance only allows modification of the currently opened BZip2 archive. Multiple
     * call of this methods return the same instance. Closing the returned instance of {@link IOutUpdateArchiveBZip2}
     * isn't necessary, since it will be closed automatically. This happens when the parent instance of the
     * {@link IInArchive} get closed. Calls to the {@link IOutArchive#close()} methods of such connected instances will
     * be ignored.
     * 
     * @return an instance of the {@link IOutUpdateArchive} interface
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public IOutUpdateArchiveBZip2 getConnectedOutArchiveBZip2() throws SevenZipException;
}
