package net.sf.sevenzipjbinding;

/**
 *
 * Enumeration of possible results of extraction operations.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public enum ExtractOperationResult {
    /**
     * Extraction was a success
     */
    OK,

    /**
     * Extraction failed: unknown compression method
     */
    UNSUPPORTEDMETHOD,

    /**
     * Extraction failed: data error.<br>
     * <br>
     * <i>NOTE:</i> for a password-protected <b>7z</b> archive created <b>without</b> header encryption, a wrong
     * password is reported here (as <code>DATAERROR</code>/{@link #CRCERROR}), not as {@link #WRONG_PASSWORD} -
     * see {@link #WRONG_PASSWORD}.
     */
    DATAERROR,

    /**
     * Extraction failed: CRC-check failed
     */
    CRCERROR,

    /**
     * Archive data is unavailable, e.g. the data can't be read from the archive.
     */
    UNAVAILABLE,

    /**
     * Archive ends unexpectedly. The archive may be truncated or the next volume is missing.
     */
    UNEXPECTED_END,

    /**
     * There is extra data after the end of the payload data.
     */
    DATA_AFTER_END,

    /**
     * The data is not a (supported) archive.
     */
    IS_NOT_ARC,

    /**
     * The archive headers are corrupted.
     */
    HEADERS_ERROR,

    /**
     * Wrong password.<br>
     * <br>
     * <i>NOTE:</i> this result is reliable for formats that carry a password verifier (e.g. Zip). A password-protected
     * <b>7z</b> archive created <b>without</b> header encryption has no such verifier, so a wrong password cannot be
     * detected as such and surfaces as {@link #DATAERROR} or {@link #CRCERROR} instead. (With 7z header encryption the
     * archive already fails to open.) This is a limitation of the archive format, not of 7-Zip-JBinding.
     */
    WRONG_PASSWORD,

    /**
     * Unknown extract operation result
     */
    UNKNOWN_OPERATION_RESULT;

    /**
     * Return extract operations enumeration item by index
     *
     * @param index
     *            index of enumeration item
     * @return extract operations enumeration item by index
     */
    public static ExtractOperationResult getOperationResult(int index) {
        if (index >= 0 && index < values().length) {
            return values()[index];
        }

        return UNKNOWN_OPERATION_RESULT;
    }
}
