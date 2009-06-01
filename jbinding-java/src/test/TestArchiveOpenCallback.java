package test;

import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.SevenZipException;

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
