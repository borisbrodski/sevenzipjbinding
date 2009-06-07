package net.sf.sevenzipjbinding;

/**
 * Enumeration of all supported archive types.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public enum ArchiveFormat {
	ZIP("Zip"), //
	TAR("Tar"), //
	SPLIT("Split"), //
	RAR("Rar"), //
	LZMA("Lzma"), //
	ISO("Iso"), //
	HFS("HFS"), //
	GZIP("GZip"), //
	CPIO("Cpio"), //
	// CAB("Cab"), // TODO Problems with CAB: It always require ArchiveOpenVolumeCallback!
	BZIP2("BZIP2"), //
	SEVEN_ZIP("7z"), //
	Z("Z"), //
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