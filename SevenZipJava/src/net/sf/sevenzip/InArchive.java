package net.sf.sevenzip;

public class InArchive implements IInArchive {

	static {
		SevenZip.init();
	}
	
	InArchive(int sevenZipImplementation) {
		
	}
	
	private void checkResult(int errCode) throws SevenZipException {
		if (errCode != 0) {
			throw new SevenZipException(errCode);
		}
	}

	public void close() throws SevenZipException {
		checkResult(nativeClose());
	}

	private native int nativeClose();

	public void extract(int[] indices, int testMode,
			IArchiveExtractCallback extractCallback) throws SevenZipException {
		checkResult(nativeExtract(indices, testMode, extractCallback));
	}

	private native int nativeExtract(int[] indices, int testMode,
			IArchiveExtractCallback extractCallback);

	public PropVariant getArchiveProperty(long propID) throws SevenZipException {
		PropVariant propVariant = new PropVariant();
		checkResult(nativeGetArchiveProperty(propID, propVariant));
		return propVariant;
	}

	private native int nativeGetArchiveProperty(long propID, PropVariant value);

	public PropertyInfo getArchivePropertyInfo(int index)
			throws SevenZipException {
		PropertyInfo propertyInfo = new PropertyInfo();
		checkResult(nativeGetArchivePropertyInfo(index, propertyInfo));
		return propertyInfo;
	}

	private native int nativeGetArchivePropertyInfo(int index,
			PropertyInfo propertyInfo);

	public int getNumberOfArchiveProperties() throws SevenZipException {
		int[] result = new int[1];
		checkResult(nativeGetNumberOfArchiveProperties(result));
		return result[0];
	}

	private native int nativeGetNumberOfArchiveProperties(
			int[] numPropertiesOneElementArray);

	public int getNumberOfItems() throws SevenZipException {
		int[] result = new int[1];

		checkResult(nativeGetNumberOfItems(result));
		return result[0];
	}

	private native int nativeGetNumberOfItems(int[] numItemsOneElementArray);

	public int getNumberOfProperties() throws SevenZipException {
		int [] result = new int[1];
		checkResult(nativeGetNumberOfProperties(result));
		return result[0];
	}
	
	private native int nativeGetNumberOfProperties(int[] numPropertiesOneElementArray);

	public PropVariant getProperty(int itemIndex, long propID) throws SevenZipException {
		PropVariant propVariant = new PropVariant();
		checkResult(nativeGetProperty(itemIndex, propID, propVariant));
		return propVariant;
	}
	
	private native int nativeGetProperty(int itemIndex, long propID, PropVariant value);

	public PropertyInfo getPropertyInfo(int index) throws SevenZipException {
		PropertyInfo propertyInfo = new PropertyInfo();
		checkResult(nativeGetPropertyInfo(index, propertyInfo));
		return propertyInfo;
	}
	
	private native int nativeGetPropertyInfo(int index, PropertyInfo propertyInfo);

	
	/**
	 * 
	 * @param inStream
	 * @param archiveOpenCallback
	 * @return maxCheckStartPosition
	 * @throws SevenZipException 
	 */
	public long open(IInStream inStream, IArchiveOpenCallback archiveOpenCallback) throws SevenZipException {
		long[] maxCheckStartPosition = new long[1];
		checkResult(nativeOpen(inStream, maxCheckStartPosition, archiveOpenCallback));
		return maxCheckStartPosition[0];
	}
	
	private native int nativeOpen(IInStream stream,
			long[] maxCheckStartPositionOneElementArray,
			IArchiveOpenCallback openArchiveCallback);
}
