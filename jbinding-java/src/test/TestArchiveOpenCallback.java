package test;

import net.sf.sevenzip.IArchiveOpenCallback;
import net.sf.sevenzip.ICryptoGetTextPassword;
import net.sf.sevenzip.SevenZipException;

public class TestArchiveOpenCallback implements IArchiveOpenCallback,
		ICryptoGetTextPassword {

	public String cryptoGetTextPassword() throws SevenZipException {
		System.out.println("cryptoGetTextPassword()");
		return "123";
	}

	public void setCompleted(Long files, Long bytes) {
		System.out.println("setCompleted(" + files + ", " + bytes + ")");
	}

	public void setTotal(Long files, Long bytes) {
		System.out.println("setTotal(" + files + ", " + bytes + ")");
	}

}
