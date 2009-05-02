package test;

import net.sf.sevenzip.SevenZip;

public class Main {

	private static native void nativeTest();
	private static native void clientTest();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary("7-Zip-JBinding");
		clientTest();
	}

}
