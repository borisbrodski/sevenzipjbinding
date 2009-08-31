package net.sf.sevenzipjbinding;

/**
 * If you want to provide a password to extract files, you should also implement {@link ICryptoGetTextPassword} within
 * your IArchiveExtractCallback-implementation.
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public interface IArchiveExtractCallback extends IProgress {
	/**
	 * Return sequential output stream for the file with index <code>index</code>.
	 * 
	 * @param index
	 *            index of the item to extract
	 * 
	 * @param extractAskMode
	 *            extract ask mode
	 * @return an instance of {@link ISequentialOutStream} sequential out stream or <code>null</code> to skip the
	 *         extraction of the current item (with index <code>index</code>) and proceed with the next one
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
	 *             as failed. There are no guarantee, that there are no further call back methods will be called. The
	 *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
	 */
	public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException;

	/**
	 * Prepare operation. The index of the current archive item can be taken from the last call of
	 * {@link #getStream(int, ExtractAskMode)}.
	 * 
	 * @param extractAskMode
	 *            extract ask mode
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
	 *             as failed. There are no guarantee, that there are no further call back methods will be called. The
	 *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
	 */
	public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException;

	/**
	 * Set result of extraction operation of the file with index <code>index</code> from last call of
	 * {@link #getStream(int, ExtractAskMode)}.
	 * 
	 * @param extractOperationResult
	 *            result of operation
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
	 *             as failed. There are no guarantee, that there are no further call back methods will be called. The
	 *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
	 */
	public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException;

}
