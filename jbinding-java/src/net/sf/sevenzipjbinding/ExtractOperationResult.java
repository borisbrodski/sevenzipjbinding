package net.sf.sevenzipjbinding;

/**
 * 
 * Enumeration of possible operation results by extracting operations.
 * 
 * @author Boris Brodski
 * @version 4.65-1
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
     * Extraction failed: data error
     */
    DATAERROR,

    /**
     * Extraction failed: CRC-check failed
     */
    CRCERROR,

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
