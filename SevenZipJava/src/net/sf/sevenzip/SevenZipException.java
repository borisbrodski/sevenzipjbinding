package net.sf.sevenzip;

/**
 * 
 * @author Boris Brodski
 * @since 0.1
 */
public class SevenZipException extends Exception {

	private static final long serialVersionUID = 42L;

	public SevenZipException() {
		super();
	}

	public SevenZipException(String message, Throwable cause) {
		super(message, cause);
	}

	public SevenZipException(String message) {
		super(message);
	}

	public SevenZipException(Throwable cause) {
		super(cause);
	}

}
