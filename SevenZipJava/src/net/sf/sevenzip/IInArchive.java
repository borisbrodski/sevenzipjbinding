package net.sf.sevenzip;

public interface IInArchive {
	public void close() throws SevenZipException;

	public int getNumberOfItems() throws SevenZipException;

	public Object getProperty(int index, int propID)
			throws SevenZipException;

	/**
	 * indices must be sorted numItems = 0xFFFFFFFF means all files testMode !=
	 * 0 means "test files operation"
	 */
	public int extract(int[] indices, int testMode,
			IArchiveExtractCallback extractCallback) throws SevenZipException;

	public Object getArchiveProperty(int propID) throws SevenZipException;

	public int getNumberOfProperties() throws SevenZipException;

	public PropertyInfo getPropertyInfo(int index)
			throws SevenZipException;

	public int getNumberOfArchiveProperties() throws SevenZipException;

	public void getArchivePropertyInfo(int index, String[] nameOneElementArray,
			int[] propIDOneElementArray, VarType[] varTypeOneElementArray) throws SevenZipException;

}
