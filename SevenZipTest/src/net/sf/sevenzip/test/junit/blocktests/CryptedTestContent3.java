package net.sf.sevenzip.test.junit.blocktests;

public class CryptedTestContent3 extends StandardTest {

	@Override
	protected int getTestId() {
		return 3;
	}

	@Override
	protected boolean usingPassword() {
		return true;
	}

}
