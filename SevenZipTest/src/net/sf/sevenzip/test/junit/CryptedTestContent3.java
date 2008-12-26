package net.sf.sevenzip.test.junit;

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
