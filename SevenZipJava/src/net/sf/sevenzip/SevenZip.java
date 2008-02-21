package net.sf.sevenzip;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import net.sf.sevenzip.tools.InStreamImpl;

public class SevenZip {
	private int test = 123;

	public static native SevenZip openArchiveTest(IInStream inStream);

	private static native String initSevenZipLibrary();

	public SevenZip() {

	}

	static void init() {
		System.loadLibrary("libSevenZip");
		String errorMessage = initSevenZipLibrary();
		if (errorMessage != null) {
			throw new RuntimeException("Error initializing 7-Zip-JBinding: "
					+ errorMessage);
		}

		try {
			SevenZip openArchiveTest = openArchiveTest(new InStreamImpl(
					new RandomAccessFile("D:\\Private\\workspace\\SevenZipCPP\\test.7z", "r")));
			System.out.println("Object created! Test=" + openArchiveTest.test);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
