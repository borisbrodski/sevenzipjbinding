package net.sf.sevenzipjbinding;

/**
 * This interface provides progress information of a process.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public interface IProgress {
	/**
	 * Set total amount of work
	 * 
	 * @param total
	 *            amount of work
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the
	 *             current operation will be reported to 7-Zip as failed. There
	 *             are no guarantee, that there are no further call back methods
	 *             will be called. The first thrown exception will be saved and
	 *             thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or
	 *             <code>SevenZip.openInArchive()</code>.
	 */
	public void setTotal(long total) throws SevenZipException;

	/**
	 * Set current amount of completed work
	 * 
	 * @param completeValue
	 *            amount of completed work
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the
	 *             current operation will be reported to 7-Zip as failed. There
	 *             are no guarantee, that there are no further call back methods
	 *             will be called. The first thrown exception will be saved and
	 *             thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or
	 *             <code>SevenZip.openInArchive()</code>.
	 */
	public void setCompleted(long completeValue) throws SevenZipException;
}
