package net.sf.sevenzip;

/**
 * 
 * Enumeration of possible operation results by extract operations.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public enum ExtractOperationResult {
	OK, UNSUPPORTEDMETHOD, DATAERROR, CRCERROR, UNKNOWN_OPERATION_RESULT;

	public static ExtractOperationResult getOperationResult(int index) {
		if (index >= 0 && index < values().length) {
			return values()[index];
		}

		return UNKNOWN_OPERATION_RESULT;
	}
}
