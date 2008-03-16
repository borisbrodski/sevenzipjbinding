package net.sf.sevenzip;

/**
 * Enumeration of 'Ask Modes' for extract operations
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public enum ExtractAskMode {
	EXTRACT, TEST, SKIP, UNKNOWN_ASK_MODE;

	public static ExtractAskMode getExtractAskModeByIndex(int index) {
		if (index >= 0 && index < values().length) {
			return values()[index];
		}

		return UNKNOWN_ASK_MODE;
	}
}
