package net.sf.sevenzip.test;

import net.sf.sevenzip.ExtractAskMode;
import net.sf.sevenzip.ExtractOperationResult;
import net.sf.sevenzip.IArchiveExtractCallback;
import net.sf.sevenzip.SequentialOutStream;

public class TestArchiveExtractCallback implements IArchiveExtractCallback {

	@Override
	public SequentialOutStream getStream(int index,
			ExtractAskMode extractAskMode) {
		System.out.println("getStream(" + index + ", " + extractAskMode+ ")");
		if (extractAskMode != ExtractAskMode.EXTRACT) {
			return null;
		}
		return new TestOutputStream();
	}

	@Override
	public boolean prepareOperation(ExtractAskMode extractAskMode) {
		System.out.println("prepareOperation(" + extractAskMode + ")");
		return true;
	}

	@Override
	public void setOperationResult(ExtractOperationResult extractOperationResult) {
		System.out.println("setOperationResult(" + extractOperationResult + ")");

	}

	@Override
	public void setCompleted(long completeValue) {
		System.out.println("setCompleted(" + completeValue + ")");
	}

	@Override
	public void setTotal(long total) {
		System.out.println("setTotal(" + total + ")");
	}

}
