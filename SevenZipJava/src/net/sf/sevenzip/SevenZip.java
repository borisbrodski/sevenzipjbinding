package net.sf.sevenzip;

public class SevenZip {

	private static boolean initialized = false;

	/**
	 * Hide default constructor
	 */
	private SevenZip() {

	}

	static {
		initSevenZipNativeLibrary();
	}

	private static native String nativeInitSevenZipLibrary();

	public static void initSevenZipNativeLibrary() {
		if (initialized) {
			return;
		}
		initialized = true;

		System.loadLibrary("7-Zip-JBinding");
		String errorMessage = nativeInitSevenZipLibrary();
		if (errorMessage != null) {
			throw new RuntimeException("Error initializing 7-Zip-JBinding: "
					+ errorMessage);
		}
	}

	private static native ISevenZipInArchive nativeOpenArchive(int format,
			IInStream inStream, IArchiveOpenCallback archiveOpenCallback)
			throws SevenZipException;

	public static ISevenZipInArchive openInArchive(ArchiveFormat f, IInStream inStream,
			IArchiveOpenCallback archiveOpenCallback) throws SevenZipException {
		return nativeOpenArchive(f.ordinal(), inStream, archiveOpenCallback);
	}

	public static ISevenZipInArchive openInArchive(ArchiveFormat f, IInStream inStream)
			throws SevenZipException {
		return nativeOpenArchive(f.ordinal(), inStream, null);
	}
}
