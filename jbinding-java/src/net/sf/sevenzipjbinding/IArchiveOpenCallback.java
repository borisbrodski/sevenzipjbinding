package net.sf.sevenzipjbinding;

/**
 * Interface to recieve information about open archive progress
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public interface IArchiveOpenCallback {

	/**
	 * Set total amount of work to be done. Both parameter are optional.
	 * 
	 * @param files
	 *            count of files to be processed (optional)
	 * @param bytes
	 *            count of bytes to be processed (optional)
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
	 *             as failed. There are no guarantee, that there are no further call back methods will be called. The
	 *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
	 */
	public void setTotal(Long files, Long bytes) throws SevenZipException;

	/**
	 * Set amount of competed work. Both parameter are optional.
	 * 
	 * @param files
	 *            count of processed files (optional)
	 * @param bytes
	 *            count of processed bytes (optional)
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
	 *             as failed. There are no guarantee, that there are no further call back methods will be called. The
	 *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
	 */
	public void setCompleted(Long files, Long bytes) throws SevenZipException;
}
