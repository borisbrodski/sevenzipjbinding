package net.sf.sevenzip;


public class SevenZip {
	@Deprecated
	public static native SevenZip openArchive(String filename);
	
	private static native String initSevenZipLibrary();

	static void init() {
		System.loadLibrary("libSevenZip");
		String errorMessage = initSevenZipLibrary();
		if (errorMessage != null) {
			throw new RuntimeException("Error initializing 7-Zip-JBinding: " + errorMessage);
		}
	}
}
