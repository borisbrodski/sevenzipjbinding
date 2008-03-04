package net.sf.sevenzip;

public class SevenZip {
	public enum Format {
		ARJ, SEVEN_ZIP, BZIP_2, CAB, CHM, CPIO, CDEB, GZIP, //
		ISO, LZH, NSIS, RAR, RPM, SPLIT, TAR, Z, ZIP
	};

	private static native SevenZip nativeOpenArchive(int format,
			IInStream inStream) throws SevenZipException;

	private static native String initSevenZipLibrary();

	/**
	 * Hide default constructor
	 */
	private SevenZip() {

	}

	static {
		init();
	}

	static void init() {
		System.loadLibrary("libSevenZip");
		String errorMessage = initSevenZipLibrary();
		if (errorMessage != null) {
			throw new RuntimeException("Error initializing 7-Zip-JBinding: "
					+ errorMessage);
		}
	}

	public static SevenZip openArchive(Format f, IInStream inStream)
			throws SevenZipException {
		return nativeOpenArchive(f.ordinal(), inStream);
	}
}
