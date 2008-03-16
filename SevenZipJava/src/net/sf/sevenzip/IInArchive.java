package net.sf.sevenzip;

public interface IInArchive {
	public void close() throws SevenZipException;

	public int getNumberOfItems() throws SevenZipException;

	public Object getProperty(int index, PropID propID)
			throws SevenZipException;

	/**
	 * indices must be sorted numItems = 0xFFFFFFFF means all files testMode !=
	 * 0 means "test files operation"
	 */
	public void extract(int[] indices, boolean testMode,
			IArchiveExtractCallback extractCallback) throws SevenZipException;

	public Object getArchiveProperty(PropID propID) throws SevenZipException;

	public int getNumberOfProperties() throws SevenZipException;

	public PropertyInfo getPropertyInfo(int index) throws SevenZipException;

	public int getNumberOfArchiveProperties() throws SevenZipException;

	public PropertyInfo getArchivePropertyInfo(int index)
			throws SevenZipException;

}
