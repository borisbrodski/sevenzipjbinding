package net.sf.sevenzip;

import net.sf.sevenzip.simple.ISimpleInArchive;

public interface ISevenZipInArchive {
	public void close() throws SevenZipException;

	public int getNumberOfItems() throws SevenZipException;

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

	public Object getArchiveProperty(PropID propID) throws SevenZipException;

	/**
	 * Return property content in human readable form.
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

	public int getNumberOfProperties() throws SevenZipException;

	public PropertyInfo getPropertyInfo(int index) throws SevenZipException;

	public int getNumberOfArchiveProperties() throws SevenZipException;

	public PropertyInfo getArchivePropertyInfo(int index)
			throws SevenZipException;

	public ISimpleInArchive getSimpleInterface();
}
