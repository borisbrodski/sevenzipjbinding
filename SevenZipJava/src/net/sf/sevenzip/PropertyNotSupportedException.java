package net.sf.sevenzip;

public class PropertyNotSupportedException extends Exception {

	private static final long serialVersionUID = 42L;

	public PropertyNotSupportedException() {
		super();
	}

	public PropertyNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertyNotSupportedException(String message) {
		super(message);
	}

	public PropertyNotSupportedException(Throwable cause) {
		super(cause);
	}

}
