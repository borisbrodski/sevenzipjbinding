package net.sf.sevenzip.test.junit.blocktests;

public class CryptedTestContent5 extends StandardTest {

	@Override
	protected int getTestId() {
		return 5;
	}

	@Override
	protected boolean usingPassword() {
		return true;
	}

}
