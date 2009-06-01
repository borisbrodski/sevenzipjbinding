package test;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

public class TestArchiveExtractCallback implements IArchiveExtractCallback,
		ICryptoGetTextPassword {

	public ISequentialOutStream getStream(int index,
			ExtractAskMode extractAskMode) {
		System.out.println("getStream(" + index + ", " + extractAskMode + ")");
		if (extractAskMode != ExtractAskMode.EXTRACT) {
			return null;
		}
		return new TestOutputStream();
	}

	public boolean prepareOperation(ExtractAskMode extractAskMode) {
		System.out.println("prepareOperation(" + extractAskMode + ")");
		return true;
	}

	public void setOperationResult(ExtractOperationResult extractOperationResult) {
		System.out
				.println("setOperationResult(" + extractOperationResult + ")");

	}

	public void setCompleted(long completeValue) {
		System.out.println("setCompleted(" + completeValue + ")");
	}

	public void setTotal(long total) {
		System.out.println("setTotal(" + total + ")");
	}

	public String cryptoGetTextPassword() throws SevenZipException {
		System.out
				.println("TestArchiveExtractCallback.cryptoGetTextPassword()");
		return "123";
	}

}
