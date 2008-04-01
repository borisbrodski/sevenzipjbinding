package net.sf.sevenzip;

/**
 * Interface to create archive with specified format.
 * 
 * @author Boris Brodski
 * @since 0.3
 */
public interface IOutArchive {
	public void updateItems(ISequentialOutStream outStream, int numItems,
			IArchiveUpdateCallback updateCallback) throws SevenZipException;

	public int getFileTimeType();

}
