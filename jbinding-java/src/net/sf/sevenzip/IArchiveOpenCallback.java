package net.sf.sevenzip;

/**
 * Interface to recieve information about open archive progress
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public interface IArchiveOpenCallback {

	/**
	 * Set total amount of work to be done. Both parameter are optional.
	 * 
	 * @param files
	 *            count of files to be processed (optional)
	 * @param bytes
	 *            count of bytes to be processed (optional)
	 */
	public void setTotal(Long files, Long bytes);

	/**
	 * Set amount of competed work. Both parameter are optional.
	 * 
	 * @param files
	 *            count of processed files (optional)
	 * @param bytes
	 *            count of processed bytes (optional)
	 */
	public void setCompleted(Long files, Long bytes);
}
