package net.sf.sevenzip;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;

import net.sf.sevenzip.test.SequentialInStreamImpl;


public class SevenZip {
	private int test = 123;
	
	public static native SevenZip openArchiveTest(SequentialInStream sequentialInStream);
	
	private static native String initSevenZipLibrary();

	public SevenZip() {
		
	}
	
	static void init() {
		System.loadLibrary("libSevenZip");
		String errorMessage = initSevenZipLibrary();
		if (errorMessage != null) {
			throw new RuntimeException("Error initializing 7-Zip-JBinding: " + errorMessage);
		}
	
		try {
			SevenZip openArchiveTest = openArchiveTest(new SequentialInStreamImpl(new FileInputStream("D:\\Download\\Torrent\\Transformers.HDDVDRip.720p.x264.HANSMER.mkv")));//"C:\\setup.log")));
			System.out.println("Object created! Test="+openArchiveTest.test);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
