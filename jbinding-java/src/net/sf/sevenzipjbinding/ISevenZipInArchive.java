package net.sf.sevenzipjbinding;

import java.util.Arrays;

import net.sf.sevenzipjbinding.simple.ISimpleInArchive;

/**
 * One to one binding of original 7-Zip dll. Standard way to get implementation is to use {@link SevenZip}.<br>
 * <br>
 * The interface provides functionality to query archive and archive item parameters and to extract archive items.<br>
 * <br>
 * The last call should be a call of the method {@link ISevenZipInArchive#close()}. After this call no more methods
 * should be called.
 * 
 * @author Boris Brodski
 * @version 4.65-1
 * 
 */
public interface ISevenZipInArchive {
    /**
     * Close archive. This method should be the last one called on the implementation of this interface. After this call
     * no more methods should be called.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public void close() throws SevenZipException;

    /**
     * Returns count of items in archive. Depending on archive type directories are considered to be items or not.
     * 
     * @return count of items in archive
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public int getNumberOfItems() throws SevenZipException;

    /**
     * Get value of property <code>propID</code> of the item with the index <code>index</code>.
     * 
     * @param index
     *            index of item to get property value
     * @param propID
     *            property to get value of
     * @return value of property <code>propID</code> of item with index <code>index</code>
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public Object getProperty(int index, PropID propID) throws SevenZipException;

    /**
     * Return property content in human readable form. Example for {@link PropID#ATTRIBUTES}: <code>D</code> for a
     * directory.
     * 
     * @param index
     *            index of item in archive to get property of
     * @param propID
     *            property to return
     * @return property <code>propID</code> of item with id <code>index</code> in human readable form.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public String getStringProperty(int index, PropID propID) throws SevenZipException;

    /**
     * Extract archive items with indices <code>indices</code>.<br>
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
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public void extract(int[] indices, boolean testMode, IArchiveExtractCallback extractCallback)
			throws SevenZipException;

    /**
     * Extract one item from archive. Multiple calls of this method are inefficient for some archive types.
     * 
     * @param index
     *            index of the item to extract
     * @param outStream
     *            sequential output stream to get content of the item
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     * @return result status of the extraction
     */
	public ExtractOperationResult extractSlow(int index, ISequentialOutStream outStream) throws SevenZipException;

    /**
     * Extract one item from archive. Multiple calls of this method are inefficient for some archive types.
     * 
     * @param index
     *            index of the item to extract
     * @param outStream
     *            sequential output stream to get content of the item
     * @param password
     *            password to use
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
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
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
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
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public String getStringArchiveProperty(PropID propID) throws SevenZipException;

    /**
     * Return count of properties of each archive item
     * 
     * @return count of properties of each archive item
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public int getNumberOfProperties() throws SevenZipException;

    /**
     * Get information about archive item property <code>propID</code>
     * 
     * @param propID
     *            item property
     * @return information about property of archive item
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public PropertyInfo getPropertyInfo(PropID propID) throws SevenZipException;

    /**
     * Get count of properties of archive
     * 
     * @return count of properties of archive
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public int getNumberOfArchiveProperties() throws SevenZipException;

    /**
     * Get information about archive property <code>propID</code>.<br>
     * <br>
     * Use {@link PropID#getPropIDByIndex(int)} to get <code>PropID</code> enumeration element with specified index.
     * 
     * @param propID
     *            archive property
     * @return information about archive property
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding intern error. Check exception message for more information.
     */
	public PropertyInfo getArchivePropertyInfo(PropID propID) throws SevenZipException;

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

}
