package net.sf.sevenzip;

/**
 * If you want to provide a password to extract files, you should also implement
 * {@link ICryptoGetTextPassword} within your
 * IArchiveExtractCallback-implementation.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public interface IArchiveExtractCallback extends IProgress {
	/**
	 * Return sequential output stream for the file with index
	 * <code>index</code>.
	 */
	public ISequentialOutStream getStream(int index,
			ExtractAskMode extractAskMode);

	/**
	 * Decide to proccess with extraction of the file with index
	 * <code>index</code> from last call of {@link #getStream(int, int)}.
	 * 
	 * @return <code>true</code> proceed with extraction, <code>false</code>
	 *         skip this file.
	 */
	public boolean prepareOperation(ExtractAskMode extractAskMode);

	/**
	 * Set result of extraction operation of the file with index
	 * <code>index</code> from last call of {@link #getStream(int, int)}.
	 * 
	 * @param extractOperationResult
	 *            result of operation
	 */
	public void setOperationResult(ExtractOperationResult extractOperationResult);

}
