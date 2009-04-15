package net.sf.sevenzip;

import net.sf.sevenzip.simple.ISimpleInArchive;

/**
 * One to one binding of original 7-Zip dll. Standard way to get implementation
 * is to use {@link SevenZip}.<br>
 * <br>
 * The interface provides functionity to query archive and archive item
 * parameters, but also to extract of item content. <br>
 * <br>
 * The last call should be a call of the methode
 * {@link ISevenZipInArchive#close()}. After this call no more Methods should
 * be called.
 * 
 * @author Boris Brodski
 * 
 */
public interface ISevenZipInArchive {
	/**
	 * Close archive. This method should be always called after all operations
	 * with this archive was preformed. After this call no more Methods should
	 * be called.
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public void close() throws SevenZipException;

	/**
	 * Returns count of items in archive. Depending on archive type directories
	 * are considered to be items or not.
	 * 
	 * @return count of items in archive
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public int getNumberOfItems() throws SevenZipException;

	/**
	 * Get value of property <code>propID</code> of item with index
	 * <code>index</code>.
	 * 
	 * @param index
	 *            index of item to get property value
	 * @param propID
	 *            property to get value of
	 * @return value of property <code>propID</code> of item with index
	 *         <code>index</code>
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public Object getProperty(int index, PropID propID)
			throws SevenZipException;

	/**
	 * Return property content in human readable form. Example for
	 * {@link PropID#ATTRIBUTES}: <code>D</code> for a directory.
	 * 
	 * @param index
	 *            index of item in archive to get property of
	 * @param propID
	 *            property to return
	 * @return property <code>propID</code> of item with id <code>index</code>
	 *         in human readable form.
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public String getStringProperty(int index, PropID propID)
			throws SevenZipException;

	/**
	 * indices must be sorted numItems = 0xFFFFFFFF means all files testMode !=
	 * 0 means "test files operation"
	 * 
	 * @param indices
	 *            array of indices of archive items to extract
	 * @param testMode
	 *            <code>true</code> - test achived items only
	 * @param extractCallback
	 *            extraction callback object. Optional implementation of
	 *            {@link ICryptoGetTextPassword} is possible.
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public void extract(int[] indices, boolean testMode,
			IArchiveExtractCallback extractCallback) throws SevenZipException;

	/**
	 * Extract one item from archive. Multiple calls of this operations very
	 * slow on some archive types.
	 * 
	 * @param index
	 *            index of the item to extract
	 * @param outStream
	 *            sequential output stream to get inhalt of the item
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public void extractSlow(int index, ISequentialOutStream outStream)
			throws SevenZipException;

	/**
	 * Extract one item from archive. Multiple calls of this operations very
	 * slow on some archive types.
	 * 
	 * @param index
	 *            index of the item to extract
	 * @param outStream
	 *            sequential output stream to get inhalt of the item
	 * @param password
	 *            password to use
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public void extractSlow(int index, ISequentialOutStream outStream,
			String password) throws SevenZipException;

	/**
	 * Get value of archive property <code>propID</code>.
	 * 
	 * @param propID
	 *            property to get value of
	 * @return value of archive property <code>propID</code>
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
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
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public String getStringArchiveProperty(PropID propID)
			throws SevenZipException;

	/**
	 * Return count of properties of each archive item
	 * 
	 * @return count of properties of each archive item
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public int getNumberOfProperties() throws SevenZipException;

	/**
	 * Get information about archive item property <code>propID</code>
	 * 
	 * @param propID
	 *            item property
	 * @return information about property of archive item
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public PropertyInfo getPropertyInfo(PropID propID) throws SevenZipException;

	/**
	 * Get count of properties of archive
	 * 
	 * @return count of properties of archive
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public int getNumberOfArchiveProperties() throws SevenZipException;

	/**
	 * Get information about archive property <code>propID</code>.<br>
	 * <br>
	 * Use {@link PropID#getPropIDByIndex(int)} to get <code>PropID</code>
	 * enumeration element with specified index.
	 * 
	 * @param propID
	 *            archive property
	 * @return information about archive property
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public PropertyInfo getArchivePropertyInfo(PropID propID)
			throws SevenZipException;

	/**
	 * Return simple 7-Zip interface for the archive
	 * 
	 * @return simple 7-Zip interface for the archive
	 */
	public ISimpleInArchive getSimpleInterface();

}
