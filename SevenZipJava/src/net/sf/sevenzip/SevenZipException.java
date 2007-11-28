package net.sf.sevenzip;

public class SevenZipException extends Exception {

	private static final long serialVersionUID = 42L;
	
	private final int errCode;

	public SevenZipException(int errCode) {
		this.errCode = errCode;
	}
	
	public int getErrCode() {
		return errCode;
	}
}
