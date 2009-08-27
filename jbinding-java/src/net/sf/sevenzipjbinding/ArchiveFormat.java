package net.sf.sevenzipjbinding;

/**
 * Enumeration of all supported archive types.
 * 
 * @author Boris Brodski
 * @version 4.65-1
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

	/**
	 * Cab format.
	 */
	CAB("Cab"),

	/**
	 * Lzh
	 */
	LZH("Lzh"),

	/**
	 * Chm
	 */
	CHM("Chm"),
	// TODO Not yet supported:
	// CDEB, NSIS, RPM
	;

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