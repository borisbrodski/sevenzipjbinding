package net.sf.sevenzipjbinding;

/**
 * Enumeration of 'Ask Modes' for extract operations
 * 
 * @author Boris Brodski
 * @since 4.65-1
 */
public enum ExtractAskMode {

    /**
     * Extract an archive item
     */
    EXTRACT,

    /**
     * Test an archive item
     */
    TEST,

    /**
     * Skip extraction of an archive item
     */
    SKIP,

    /**
     * Unknown mode
     */
    UNKNOWN_ASK_MODE;

    /**
     * Return ask mode enumeration item by index
     * 
     * @param index
     *            index of enumeration item
     * @return ask mode enumeration item by index
     */
    public static ExtractAskMode getExtractAskModeByIndex(int index) {
        if (index >= 0 && index < values().length) {
            return values()[index];
        }

        return UNKNOWN_ASK_MODE;
    }
}
