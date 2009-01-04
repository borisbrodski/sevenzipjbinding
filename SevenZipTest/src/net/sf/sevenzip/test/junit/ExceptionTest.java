package net.sf.sevenzip.test.junit;

import java.io.RandomAccessFile;
import java.util.Random;

import net.sf.sevenzip.ArchiveFormat;
import net.sf.sevenzip.IArchiveOpenCallback;
import net.sf.sevenzip.ICryptoGetTextPassword;
import net.sf.sevenzip.IInStream;
import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.impl.RandomAccessFileInStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests exception handling of 7-Zip-JBinding
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public class ExceptionTest extends TestBase {
	private class TestException extends RuntimeException {

		private static final long serialVersionUID = 42L;

		public TestException(String message) {
			super(message);
		}
	}

	private class TestInStream implements IInStream {
		private RandomAccessFileInStream randomAccessFileInStream;
		private final boolean throwInCurrentThread;

		public TestInStream(RandomAccessFile randomAccessFile,
				boolean throwInCurrentThread) {
			this.throwInCurrentThread = throwInCurrentThread;
			randomAccessFileInStream = new RandomAccessFileInStream(
					randomAccessFile);
		}

		@Override
		public int seek(long offset, int seekOrigin,
				long[] newPositionOneElementArray) {
			throwException();
			return randomAccessFileInStream.seek(offset, seekOrigin,
					newPositionOneElementArray);
		}

		@Override
		public int read(byte[] data, int[] processedSizeOneElementArray) {
			throwException();
			return randomAccessFileInStream.read(data,
					processedSizeOneElementArray);
		}

		private void throwException() {
			if (testThread != Thread.currentThread() ^ throwInCurrentThread) {
				exceptionMessage = "Test exception " + new Random().nextLong();
				throw new TestException(exceptionMessage);
			}
		}
	}

	private class TestArchiveOpenCallback implements IArchiveOpenCallback,
			ICryptoGetTextPassword {
		@Override
		public void setCompleted(Long files, Long bytes) {
		}

		@Override
		public void setTotal(Long files, Long bytes) {
		}

		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return "password";
		}
	}

	private Thread testThread;
	private String exceptionMessage = null;

	@Test
	public void testThrowOutOfTheSameThread() {
		testThread = Thread.currentThread();
		try {
			SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new TestInStream(
					new RandomAccessFile("miscarchives/simplearchive.7z", "r"),
					true));
		} catch (SevenZipException e) {
			Assert.assertNotNull(e.getCause());
			Assert.assertTrue(e.getCause() instanceof TestException);
			Assert.assertEquals(exceptionMessage, e.getCause().getMessage());
			return;

		} catch (Throwable e) {
			Assert.assertTrue(e instanceof TestException);
			Assert.assertEquals(exceptionMessage, e.getMessage());
			return;
		}

		if (exceptionMessage == null) {
			Assert.fail("Exception wasn't thrown");
		} else {
			Assert.fail("No exception arrives");
		}
	}

	@Test
	public void testThrowOutOfOtherThread() {
		testThread = Thread.currentThread();
		try {
			SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new TestInStream(
					new RandomAccessFile("miscarchives/crypted-filenames.7z",
							"r"), false), new TestArchiveOpenCallback());
		} catch (SevenZipException e) {
			Assert.assertNotNull(e.getCause());
			Assert.assertTrue(e.getCause() instanceof TestException);
			Assert.assertEquals(exceptionMessage, e.getCause().getMessage());
			return;

		} catch (Throwable e) {
			Assert.assertTrue(e instanceof TestException);
			Assert.assertEquals(exceptionMessage, e.getMessage());
			return;
		}

		if (exceptionMessage == null) {
			Assert.fail("Exception wasn't thrown");
		} else {
			Assert.fail("No exception arrives");
		}
	}
}
