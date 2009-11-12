package net.sf.sevenzipjbinding;

/**
 * Enumeration of all supported archive types. <blockquote>
 * 
 * <table border="1">
 * <tr>
 * <td><b>Format</b></td>
 * <td><b>Implementation</b></td>
 * <td><b>Test</b></td>
 * <td><b>Enum value</b></td>
 * </tr>
 * <tr align="center">
 * <td>7z</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #SEVEN_ZIP}</td>
 * </tr>
 * <tr align="center">
 * <td>Arj</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #ARJ}</td>
 * </tr>
 * <tr align="center">
 * <td>BZip2</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #BZIP2}</td>
 * </tr>
 * <tr align="center">
 * <td>Chm</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #CHM}</td>
 * </tr>
 * <tr align="center">
 * <td>Compound</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Cpio</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #CPIO}</td>
 * </tr>
 * <tr align="center">
 * <td>Deb</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #DEB}</td>
 * </tr>
 * <tr align="center">
 * <td>Dmg</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Elf</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>GZip</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #GZIP}</td>
 * </tr>
 * <tr align="center">
 * <td>Hfs</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Iso</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #ISO}</td>
 * </tr>
 * <tr align="center">
 * <td>Lzh</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #LZH}</td>
 * </tr>
 * <tr align="center">
 * <td>Lzma</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #LZMA}</td>
 * </tr>
 * <tr align="center">
 * <td>Macho</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Mub</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Nsis</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #NSIS}</td>
 * </tr>
 * <tr align="center">
 * <td>Pa</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Rar</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #RAR}</td>
 * </tr>
 * <tr align="center">
 * <td>Rpm</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #RAR}</td>
 * </tr>
 * <tr align="center">
 * <td>Split</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Tar</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #TAR}</td>
 * </tr>
 * <tr align="center">
 * <td>Udf</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Wim</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Xar</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #XAR}</td>
 * </tr>
 * <tr align="center">
 * <td>Z</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #Z}</td>
 * </tr>
 * <tr align="center">
 * <td>Zip</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #ZIP}</td>
 * </tr>
 * </table>
 * <blockquote> <br>
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

	/**
	 * Nsis
	 */
	NSIS("Nsis"),

	/**
	 * Deb
	 */
	DEB("Deb"),

	/**
	 * Rpm
	 */
	RPM("Rpm"),

	/**
	 * Udf
	 */
	UDF("Udf"),

	/**
	 * Xar
	 */
	XAR("Xar");

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