package net.sf.sevenzip;

/**
 * Simplified interface for C++ <code>IInArchive</code>. For binding of
 * original 7-Zip C++ interface see {@link ISevenZipInArchive}.
 * 
 * 
 * @author Boris Brodski
 * @since 0.3
 */
public interface IInArchive {
	/**
	 * Close archive. No more archive operations are possible.<br>
	 * <b>Note</b>: This method should be always called to free system
	 * resources.
	 * 
	 * @throws SevenZipException
	 *             intern error occurs. See
	 *             {@link SevenZipException#getMessage()} for details.
	 */
	public void close() throws SevenZipException;

	/**
	 * Return count of items in archive.
	 * 
	 * @return count of item in archive.
	 * 
	 * @throws SevenZipException
	 *             intern error occurs. See
	 *             {@link SevenZipException#getMessage()} for details.
	 */
	public int getNumberOfItems() throws SevenZipException;

}
