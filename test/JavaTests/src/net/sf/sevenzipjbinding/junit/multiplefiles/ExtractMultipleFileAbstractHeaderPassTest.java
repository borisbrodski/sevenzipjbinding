package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.Test;

public abstract class ExtractMultipleFileAbstractHeaderPassTest extends ExtractMultipleFileAbstractPassTest {

	public ExtractMultipleFileAbstractHeaderPassTest(ArchiveFormat archiveFormat, int compression1, int compression2,
			int compression3) {
		super(archiveFormat, compression1, compression2, compression3);
		init();
	}

	public ExtractMultipleFileAbstractHeaderPassTest(ArchiveFormat archiveFormat, String extention, int compression1,
			int compression2, int compression3) {
		super(archiveFormat, extention, compression1, compression2, compression3);
		init();
	}

	private void init() {
		usingHeaderPassword();
		setCryptedArchivePrefix("passh-");
	}

	@Test(expected = SevenZipException.class)
	public void test1Compression1WithoutHeaderPassword() throws SevenZipException {
		usingHeaderPassword(false);
		usingPassword();
		test1Compression1();
	}

	@Override
	@Test(expected = SevenZipException.class)
	public void test1Compression1WithWrongPassword() throws SevenZipException {
		setPasswordToUse("12345");
		test1Compression1();
	}

}
