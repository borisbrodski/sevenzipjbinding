package net.sf.sevenzip;

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
	 */
	public void setTotal(long total);

	/**
	 * Set current amount of completed work
	 * 
	 * @param completeValue
	 *            amount of completed work
	 */
	public void setCompleted(long completeValue);
}
