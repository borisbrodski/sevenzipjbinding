package net.sf.sevenzipjbinding;

/**
 * Enumeration of all supported archive types.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public enum ArchiveFormat {
	/**
	 * Zip format.
	 */
	ZIP("Zip"),

	/**
	 * Tar format.
	 */
	TAR("Tar"),

	/**
	 * Split format.
	 */
	SPLIT("Split"),

	/**
	 * Rar format.
	 */
	RAR("Rar"), //

	/**
	 * Lzma format.
	 */
	LZMA("Lzma"),

	/**
	 * Iso format.
	 */
	ISO("Iso"),

	/**
	 * Hfs format
	 */
	HFS("HFS"),

	/**
	 * Gzip format
	 */
	GZIP("GZip"),

	/**
	 * Cpio format.
	 */
	CPIO("Cpio"),

	// CAB("Cab"), // TODO Problems with CAB: It always require ArchiveOpenVolumeCallback!

	/**
	 * BZip2 format.
	 */
	BZIP2("BZIP2"),

	/**
	 * 7z format.
	 */
	SEVEN_ZIP("7z"),

	/**
	 * Z format.
	 */
	Z("Z"),

	/**
	 * Arj format
	 */
	ARJ("Arj"), //
	;
	// TODO Not supported in cross platform version archive formats:
	//	CHM, CDEB, LZH, NSIS, RPM

	private String methodName;

	ArchiveFormat(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * Return name of the archive method
	 * 
	 * @return name of the archive method
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return methodName;
	}
}