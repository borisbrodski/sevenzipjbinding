package net.sf.sevenzip;

/**
 * Enumeration for possible archive and archive item properties
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public enum PropID {
	NO_PROPERTY1, //
	NO_PROPERTY2, //

	HANDLER_ITEM_INDEX, // = 2
	PATH, //
	NAME, //
	EXTENSION, //
	IS_FOLDER, //
	SIZE, //
	PACKED_SIZE, //
	ATTRIBUTES, //
	CREATION_TIME, //
	LAST_ACCESS_TIME, //
	LAST_WRITE_TIME, //
	SOLID, //
	COMMENTED, // 
	ENCRYPTED, //
	SPLIT_BEFORE, //
	SPLIT_AFTER, //
	DICTIONARY_SIZE, // 
	CRC, //
	TYPE, //
	IS_ANTI, //
	METHOD, //
	HOST_OS, //
	FILE_SYSTEM, //
	USER, //
	GROUP, //
	BLOCK, //
	COMMENT, //
	POSITION, //
	PREFIX, //

	TOTAL_SIZE(0x1100), //
	FREE_SPACE(0x1101), //
	CLUSTER_SIZE(0x1102), //
	VOLUME_NAME(0x1103), //

	LOCAL_NAME(0x1200), //
	PROVIDER(0x1201), //

	USER_DEFINED(0x10000), //

	UNKNOWN(-1);

	private final int propIDIndex;

	private PropID(int propIDIndex) {
		this.propIDIndex = propIDIndex;
	}

	private PropID() {
		this.propIDIndex = ordinal();
	}

	/**
	 * Native index of the property
	 * 
	 * @return Native index of the property
	 */
	public int getPropIDIndex() {
		return propIDIndex;
	}

	/**
	 * Get enumeration element by native property index
	 * 
	 * @param propIDIndex
	 *            native property index
	 * @return enumeration element
	 */
	public static PropID getPropIDByIndex(int propIDIndex) {
		if (propIDIndex >= 0 && propIDIndex < values().length
				&& values()[propIDIndex].getPropIDIndex() == propIDIndex) {
			return values()[propIDIndex];
		}

		for (int i = values().length - 1; i != -1; i--) {
			if (values()[i].getPropIDIndex() == propIDIndex) {
				return values()[i];
			}
		}

		return UNKNOWN;
	}
}
