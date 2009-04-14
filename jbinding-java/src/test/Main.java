package test;

public class Main {

	private static native void nativeTest();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary("test");
		nativeTest();
	}

}
