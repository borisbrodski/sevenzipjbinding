package net.sf.sevenzip;

/**
 * Interface to create archive with specified format.
 * 
 * TODO
 * 
 * @author Boris Brodski
 * @since 0.3
 */
public interface IOutArchive {
	/**
	 * TODO
	 * 
	 * @param outStream
	 * @param numItems
	 * @param updateCallback
	 * @throws SevenZipException
	 */
	public void updateItems(ISequentialOutStream outStream, int numItems,
			IArchiveUpdateCallback updateCallback) throws SevenZipException;

	/**
	 * TODO
	 * 
	 * @return
	 */
	public int getFileTimeType();

}
