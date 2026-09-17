package net.sf.sevenzipjbinding;

/**
 * Main callback interface for extraction operations.
 * <p>
 * To provide a password for extracting encrypted files, additionally implement {@link ICryptoGetTextPassword} in your
 * {@code IArchiveExtractCallback} implementation.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public interface IArchiveExtractCallback extends IProgress {
    /**
     * Return sequential output stream for the file with index <code>index</code>.
     *
     * @param index
     *            index of the item to extract
     *
     * @param extractAskMode
     *            the mode in which 7-Zip is processing this item; see {@link ExtractAskMode}
     * @return an instance of {@link ISequentialOutStream} sequential out stream or <code>null</code> to skip the
     *         extraction of the current item (with index <code>index</code>) and proceed with the next one
     *
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There is no guarantee that no further callback methods will be called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>IInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     */
    public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException;

    /**
     * Prepare operation. The index of the current archive item can be taken from the last call of
     * {@link #getStream(int, ExtractAskMode)}.
     *
     * @param extractAskMode
     *            the mode in which 7-Zip is processing this item; see {@link ExtractAskMode}
     *
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There is no guarantee that no further callback methods will be called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>IInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
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
     *             as failed. There is no guarantee that no further callback methods will be called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>IInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     */
    public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException;

    /**
     * Report an extraction result. Newer 7-Zip engine versions call this method to report extraction errors as they
     * occur.
     * <p>
     * This callback corresponds to the {@code IArchiveExtractCallbackMessage2} interface introduced in the 7-Zip engine
     * v23. It allows the native 7-Zip engine to report extraction errors immediately when they occur, rather than
     * waiting until {@link #setOperationResult(ExtractOperationResult)} is called. The method has a default no-op
     * implementation, so existing callbacks written for older engine versions continue to work unchanged.
     *
     * @param indexType
     *            type of index being reported ({@link ReportExtractResultIndexType})
     * @param index
     *            the archive item index or block index, depending on indexType
     * @param extractOperationResult
     *            the extraction operation result (e.g., OK, DATAERROR, CRCERROR)
     *
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed.
     *
     * @since 23.01-2.2
     */
    public default void reportExtractResult(ReportExtractResultIndexType indexType, int index, ExtractOperationResult extractOperationResult) throws SevenZipException {
        // Default no-op implementation for backward compatibility
        // Existing implementations won't break if they don't override this method
    }

}
