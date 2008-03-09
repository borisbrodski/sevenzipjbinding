package net.sf.sevenzip;

public interface IArchiveExtractCallback extends IProgress {
	/**
	 * Return sequential output stream for the file with index <code>index</code>.
	 * @param index
	 * @param askExtractMode
	 * @return
	 */
	public SequentialOutStream getStream(int index, int askExtractMode);
	
	/**
	 * Decide to proccess with extraction of the file with index <code>index</code>
	 * from last call of {@link #getStream(int, int)}.
	 * 
	 * @return <code>true</code> proceed with extraction,
	 * <code>false</code> skip this file.
	 */
	public boolean prepareOperation(int askExtractMode);
	
	/**
	 * Set result of extraction operation of the file with index <code>index</code>
	 * from last call of {@link #getStream(int, int)}.
	 * 
	 * @param resultEOperationResult result of operation
	 */
	public void setOperationResult(int resultEOperationResult);
}
