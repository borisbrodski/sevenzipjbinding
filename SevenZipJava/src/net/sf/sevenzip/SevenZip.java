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
		
		System.loadLibrary("libSevenZip");
		String errorMessage = nativeInitSevenZipLibrary();
		if (errorMessage != null) {
			throw new RuntimeException("Error initializing 7-Zip-JBinding: "
					+ errorMessage);
		}
	}
	
	private static native IInArchive nativeOpenArchive(int format,
			IInStream inStream) throws SevenZipException;

	public static IInArchive openArchive(ArchiveFormat f, IInStream inStream)
			throws SevenZipException {
		return nativeOpenArchive(f.ordinal(), inStream);
	}
}
