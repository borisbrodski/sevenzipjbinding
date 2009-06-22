package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.Test;

public abstract class ExtractSingleFileAbstractPassTest extends ExtractSingleFileAbstractTest {

	public ExtractSingleFileAbstractPassTest(ArchiveFormat archiveFormat, int compression1, int compression2,
			int compression3) {
		super(archiveFormat, compression1, compression2, compression3);
		init();
	}

	private void init() {
		usingPassword();
		setCryptedArchivePrefix("pass-");
	}

	public ExtractSingleFileAbstractPassTest(ArchiveFormat archiveFormat, String extention, int compression1,
			int compression2, int compression3) {
		super(archiveFormat, extention, compression1, compression2, compression3);
		init();
	}

	@Test(expected = SevenZipException.class)
	public void test1Compression1WithoutPassword() throws SevenZipException {
		usingPassword(false);
		test1Compression1();
	}

	@Test(expected = ExtractOperationResultException.class)
	public void test1Compression1WithWrongPassword() throws SevenZipException {
		setPasswordToUse("12345");
		test1Compression1();
	}
}
