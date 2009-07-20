package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.VolumedArchiveInStream;

import org.junit.Before;
import org.junit.Test;

public abstract class ExtractFileAbstractTest extends JUnitNativeTestBase {
	private static final int SINGLE_TEST_THREAD_COUNT = 2;
	private static final int SINGLE_TEST_REPEAT_COUNT = 2;
	private static final int SINGLE_TEST_TIMEOUT = 100000;
	private static final String DEFAULT_PASSWORD = "TestPass";

	protected final ArchiveFormat archiveFormat;
	private final int compression1;
	private final int compression2;
	private final int compression3;
	protected final String extention;
	protected String passwordToUse;
	protected boolean usingPassword = false;
	protected String cryptedArchivePrefix = "";
	protected String volumedArchivePrefix = "";
	protected String volumeArchivePostfix = "";
	protected boolean usingHeaderPassword = false;
	protected boolean usingPasswordCallback = false;
	protected boolean usingVolumes = false;
	private RandomAccessFileInStream randomAccessFileInStream;
	private VolumeArchiveOpenCallback volumeArchiveOpenCallback;

	public ExtractFileAbstractTest(ArchiveFormat archiveFormat, String extention, int compression1, int compression2,
			int compression3) {
		this.archiveFormat = archiveFormat;
		this.extention = extention;
		this.compression1 = compression1;
		this.compression2 = compression2;
		this.compression3 = compression3;
	}

	public ExtractFileAbstractTest(ArchiveFormat archiveFormat, int compression1, int compression2, int compression3) {
		this(archiveFormat, archiveFormat.toString().toLowerCase(), compression1, compression2, compression3);
	}

	protected void usingPassword(boolean using) {
		if (using) {
			usingPassword();
		} else {
			usingPassword = false;
		}
	}

	protected void setCryptedArchivePrefix(String cryptedArchivePrefix) {
		this.cryptedArchivePrefix = cryptedArchivePrefix;
	}

	protected void usingPassword() {
		usingPassword = true;
	}

	protected void usingPasswordCallback() {
		usingPasswordCallback = true;
	}

	protected void setPasswordToUse(String password) {
		passwordToUse = password;
	}

	protected void usingHeaderPassword() {
		usingHeaderPassword(true);
	}

	protected void usingHeaderPassword(boolean using) {
		usingHeaderPassword = using;
	}

	protected void usingVolumes(boolean usingVolumes) {
		this.usingVolumes = usingVolumes;
		if (usingVolumes) {
			setVolumedArchivePrefix("vol-");
		} else {
			setVolumedArchivePrefix("");
		}

	}

	public void setVolumedArchivePrefix(String volumedArchivePrefix) {
		this.volumedArchivePrefix = volumedArchivePrefix;
	}

	public void setVolumeArchivePostfix(String volumeArchivePostfix) {
		this.volumeArchivePostfix = volumeArchivePostfix;
	}

	final protected void usingVolumedSevenZip() {
		usingVolumes(true);
		setVolumeArchivePostfix(".001");
	}

	@Before
	public void initPasswordToUse() {
		passwordToUse = DEFAULT_PASSWORD;
	}

	@Test
	public void test1Compression1() throws SevenZipException {
		testArchiveExtraction(1, compression1, false, false);
	}

	@Test
	public void test1Compression1Multithreaded() throws SevenZipException {
		testArchiveExtraction(1, compression1, false, true);
	}

	@Test
	public void test1Compression1FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(1, compression1, true, false);
	}

	@Test
	public void test1Compression1FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(1, compression1, true, true);
	}

	@Test
	public void test1Compression2() throws SevenZipException {
		testArchiveExtraction(1, compression2, false, false);
	}

	@Test
	public void test1Compression2Multithreaded() throws SevenZipException {
		testArchiveExtraction(1, compression2, false, true);
	}

	@Test
	public void test1Compression2FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(1, compression2, true, false);
	}

	@Test
	public void test1Compression2FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(1, compression2, true, true);
	}

	@Test
	public void test1Compression3() throws SevenZipException {
		testArchiveExtraction(1, compression3, false, false);
	}

	@Test
	public void test1Compression3Multithreaded() throws SevenZipException {
		testArchiveExtraction(1, compression3, false, true);
	}

	@Test
	public void test1Compression3FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(1, compression3, true, false);
	}

	@Test
	public void test1Compression3FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(1, compression3, true, true);
	}

	@Test
	public void test2Compression1() throws SevenZipException {
		testArchiveExtraction(2, compression1, false, false);
	}

	@Test
	public void test2Compression1Multithreaded() throws SevenZipException {
		testArchiveExtraction(2, compression1, false, true);
	}

	@Test
	public void test2Compression1FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(2, compression1, true, false);
	}

	@Test
	public void test2Compression1FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(2, compression1, true, true);
	}

	@Test
	public void test2Compression2() throws SevenZipException {
		testArchiveExtraction(2, compression2, false, false);
	}

	@Test
	public void test2Compression2Multithreaded() throws SevenZipException {
		testArchiveExtraction(2, compression2, false, true);
	}

	@Test
	public void test2Compression2FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(2, compression2, true, false);
	}

	@Test
	public void test2Compression2FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(2, compression2, true, true);
	}

	@Test
	public void test2Compression3() throws SevenZipException {
		testArchiveExtraction(2, compression3, false, false);
	}

	@Test
	public void test2Compression3Multithreaded() throws SevenZipException {
		testArchiveExtraction(2, compression3, false, true);
	}

	@Test
	public void test2Compression3FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(2, compression3, true, false);
	}

	@Test
	public void test2Compression3FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(2, compression3, true, true);
	}

	@Test
	public void test3Compression1() throws SevenZipException {
		testArchiveExtraction(3, compression1, false, false);
	}

	@Test
	public void test3Compression1Multithreaded() throws SevenZipException {
		testArchiveExtraction(3, compression1, false, true);
	}

	@Test
	public void test3Compression1FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(3, compression1, true, false);
	}

	@Test
	public void test3Compression1FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(3, compression1, true, true);
	}

	@Test
	public void test3Compression2() throws SevenZipException {
		testArchiveExtraction(3, compression2, false, false);
	}

	@Test
	public void test3Compression2Multithreaded() throws SevenZipException {
		testArchiveExtraction(3, compression2, false, true);
	}

	@Test
	public void test3Compression2FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(3, compression2, true, false);
	}

	@Test
	public void test3Compression2FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(3, compression2, true, true);
	}

	@Test
	public void test3Compression3() throws SevenZipException {
		testArchiveExtraction(3, compression3, false, false);
	}

	@Test
	public void test3Compression3Multithreaded() throws SevenZipException {
		testArchiveExtraction(3, compression3, false, true);
	}

	@Test
	public void test3Compression3FormatAutodetect() throws SevenZipException {
		testArchiveExtraction(3, compression3, true, false);
	}

	@Test
	public void test3Compression3FormatAutodetectMultithreaded() throws SevenZipException {
		testArchiveExtraction(3, compression3, true, true);
	}

	//	@Test
	//	public void test4Compression1() throws SevenZipException {
	//		testArchiveExtraction(4, compression1, false, false);
	//	}
	//
	//	@Test
	//	public void test4Compression1Multithreaded() throws SevenZipException {
	//		testArchiveExtraction(4, compression1, false, true);
	//	}
	//
	//	@Test
	//	public void test4Compression1FormatAutodetect() throws SevenZipException {
	//		testArchiveExtraction(4, compression1, true, false);
	//	}
	//
	//	@Test
	//	public void test4Compression1FormatAutodetectMultithreaded() throws SevenZipException {
	//		testArchiveExtraction(4, compression1, true, true);
	//	}
	//
	//	@Test
	//	public void test4Compression2() throws SevenZipException {
	//		testArchiveExtraction(4, compression2, false, false);
	//	}
	//
	//	@Test
	//	public void test4Compression2Multithreaded() throws SevenZipException {
	//		testArchiveExtraction(4, compression2, false, true);
	//	}
	//
	//	@Test
	//	public void test4Compression2FormatAutodetect() throws SevenZipException {
	//		testArchiveExtraction(4, compression2, true, false);
	//	}
	//
	//	@Test
	//	public void test4Compression2FormatAutodetectMultithreaded() throws SevenZipException {
	//		testArchiveExtraction(4, compression2, true, true);
	//	}
	//
	//	@Test
	//	public void test4Compression3() throws SevenZipException {
	//		testArchiveExtraction(4, compression3, false, false);
	//	}
	//
	//	@Test
	//	public void test4Compression3Multithreaded() throws SevenZipException {
	//		testArchiveExtraction(4, compression3, false, true);
	//	}
	//
	//	@Test
	//	public void test4Compression3FormatAutodetect() throws SevenZipException {
	//		testArchiveExtraction(4, compression3, true, false);
	//	}
	//
	//	@Test
	//	public void test4Compression3FormatAutodetectMultithreaded() throws SevenZipException {
	//		testArchiveExtraction(4, compression3, true, true);
	//	}
	//
	//	@Test
	//	public void test5Compression1() throws SevenZipException {
	//		testArchiveExtraction(5, compression1, false, false);
	//	}
	//
	//	@Test
	//	public void test5Compression1Multithreaded() throws SevenZipException {
	//		testArchiveExtraction(5, compression1, false, true);
	//	}
	//
	//	@Test
	//	public void test5Compression1FormatAutodetect() throws SevenZipException {
	//		testArchiveExtraction(5, compression1, true, false);
	//	}
	//
	//	@Test
	//	public void test5Compression1FormatAutodetectMultithreaded() throws SevenZipException {
	//		testArchiveExtraction(5, compression1, true, true);
	//	}
	//
	//	@Test
	//	public void test5Compression2() throws SevenZipException {
	//		testArchiveExtraction(5, compression2, false, false);
	//	}
	//
	//	@Test
	//	public void test5Compression2Multithreaded() throws SevenZipException {
	//		testArchiveExtraction(5, compression2, false, true);
	//	}
	//
	//	@Test
	//	public void test5Compression2FormatAutodetect() throws SevenZipException {
	//		testArchiveExtraction(5, compression2, true, false);
	//	}
	//
	//	@Test
	//	public void test5Compression2FormatAutodetectMultithreaded() throws SevenZipException {
	//		testArchiveExtraction(5, compression2, true, true);
	//	}
	//
	//	@Test
	//	public void test5Compression3() throws SevenZipException {
	//		testArchiveExtraction(5, compression3, false, false);
	//	}
	//
	//	@Test
	//	public void test5Compression3Multithreaded() throws SevenZipException {
	//		testArchiveExtraction(5, compression3, false, true);
	//	}
	//
	//	@Test
	//	public void test5Compression3FormatAutodetect() throws SevenZipException {
	//		testArchiveExtraction(5, compression3, true, false);
	//	}
	//
	//	@Test
	//	public void test5Compression3FormatAutodetectMultithreaded() throws SevenZipException {
	//		testArchiveExtraction(5, compression3, true, true);
	//	}

	private void testArchiveExtraction(final int fileIndex, final int compressionIndex, final boolean autodetectFormat,
			boolean multithreaded) throws SevenZipException {
		if (multithreaded) {
			final int[] threadsFinished = new int[] { SINGLE_TEST_THREAD_COUNT };
			final Throwable[] firstThrowable = new Throwable[] { null };
			for (int i = 0; i < SINGLE_TEST_THREAD_COUNT; i++) {
				new Thread(new Runnable() {

					public void run() {
						try {
							testArchiveExtraction(fileIndex, compressionIndex, autodetectFormat, false);
						} catch (Throwable e) {
							synchronized (firstThrowable) {
								if (firstThrowable[0] == null) {
									firstThrowable[0] = e;
								}
							}
						} finally {
							synchronized (ExtractFileAbstractTest.this) {
								try {
									threadsFinished[0]--;
									ExtractFileAbstractTest.this.notify();
								} catch (Throwable throwable) {
									throwable.printStackTrace();
								}
							}
						}
					}
				}).start();
			}
			long start = System.currentTimeMillis();
			synchronized (this) {
				while (true) {
					try {
						if (threadsFinished[0] == 0) {
							break;
						}
						wait(SINGLE_TEST_TIMEOUT * SINGLE_TEST_REPEAT_COUNT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (System.currentTimeMillis() - start > SINGLE_TEST_TIMEOUT * SINGLE_TEST_REPEAT_COUNT) {
						fail("Time out");
					}
				}
			}
			if (firstThrowable[0] != null) {
				if (firstThrowable[0] instanceof SevenZipException) {
					throw (SevenZipException) firstThrowable[0];
				}
				throw new RuntimeException("Exception in underlying thread", firstThrowable[0]);
			}
		} else {
			for (int i = 0; i < SINGLE_TEST_REPEAT_COUNT; i++) {
				doTestArchiveExtraction(fileIndex, compressionIndex, autodetectFormat);
			}
		}
	}

	protected abstract void doTestArchiveExtraction(int fileIndex, int compressionIndex, boolean autodetectFormat)
			throws SevenZipException;

	protected abstract String getTestDataPath();

	protected class ExtractionInArchiveTestHelper {
		private RandomAccessFileInStream randomAccessFileInStream;
		private VolumeArchiveOpenCallback volumeArchiveOpenCallback;

		public ExtractionInArchiveTestHelper() {

		}

		public ISevenZipInArchive openArchiveFileWithSevenZip(int fileIndex, int compressionIndex,
				boolean autodetectFormat, String testFileNameWE, String testFileExt) throws SevenZipException {
			String archiveFilename = getTestDataPath() + File.separatorChar + archiveFormat.toString().toLowerCase()
					+ File.separatorChar
					+ //
					volumedArchivePrefix + cryptedArchivePrefix + testFileNameWE + fileIndex + "." + testFileExt + "."
					+ compressionIndex + "." + extention + volumeArchivePostfix;

			if (!new File(archiveFilename).exists() && extention.contains("part1.rar")) {
				archiveFilename = archiveFilename.replace("part1.rar", "rar");
			}

			randomAccessFileInStream = null;
			ISevenZipInArchive inArchive = null;
			try {
				randomAccessFileInStream = new RandomAccessFileInStream(new RandomAccessFile(archiveFilename, "r"));
				volumeArchiveOpenCallback = null;
				VolumedArchiveInStream volumedArchiveInStream;
				IInStream inStreamToUse = randomAccessFileInStream;
				IArchiveOpenCallback archiveOpenCallbackToUse = null;

				if (usingVolumes) {
					volumeArchiveOpenCallback = new VolumeArchiveOpenCallback(archiveFilename);
					if (archiveFormat == ArchiveFormat.SEVEN_ZIP) {
						volumedArchiveInStream = new VolumedArchiveInStream(archiveFilename, volumeArchiveOpenCallback);
						inStreamToUse = volumedArchiveInStream;
					} else {
						archiveOpenCallbackToUse = volumeArchiveOpenCallback;
					}
				}

				if (usingHeaderPassword) {
					if (usingPasswordCallback) {
						if (usingVolumes) {
							inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse,
									new CombinedArchiveOpenCallback(archiveOpenCallbackToUse,
											new PasswordArchiveOpenCallback(), volumeArchiveOpenCallback));
						} else {
							inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse,
									new PasswordArchiveOpenCallback());

						}
					} else {
						inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse,
								passwordToUse);
					}
				} else {
					if (archiveOpenCallbackToUse == null) {
						inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse);
					} else {
						inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse,
								archiveOpenCallbackToUse);
					}
				}
			} catch (IOException exception) {
				throw new RuntimeException(exception);
			}
			return inArchive;
		}

		public void closeAllStreams() {
			if (randomAccessFileInStream != null) {
				try {
					randomAccessFileInStream.close();
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}
			if (volumeArchiveOpenCallback != null) {
				try {
					volumeArchiveOpenCallback.close();
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}
		}
	}

	public class CombinedArchiveOpenCallback implements IArchiveOpenCallback, ICryptoGetTextPassword,
			IArchiveOpenVolumeCallback {
		private final ICryptoGetTextPassword cryptoGetTextPassword;
		private final IArchiveOpenVolumeCallback archiveOpenVolumeCallback;
		private final IArchiveOpenCallback archiveOpenCallback;

		public CombinedArchiveOpenCallback(IArchiveOpenCallback archiveOpenCallback,
				ICryptoGetTextPassword cryptoGetTextPassword, IArchiveOpenVolumeCallback archiveOpenVolumeCallback) {
			this.archiveOpenCallback = archiveOpenCallback;
			this.cryptoGetTextPassword = cryptoGetTextPassword;
			this.archiveOpenVolumeCallback = archiveOpenVolumeCallback;

		}

		public void setCompleted(Long files, Long bytes) throws SevenZipException {
			archiveOpenCallback.setCompleted(files, bytes);
		}

		public void setTotal(Long files, Long bytes) throws SevenZipException {
			archiveOpenCallback.setTotal(files, bytes);
		}

		public String cryptoGetTextPassword() throws SevenZipException {
			return cryptoGetTextPassword.cryptoGetTextPassword();
		}

		public Object getProperty(PropID propID) throws SevenZipException {
			return archiveOpenVolumeCallback.getProperty(propID);
		}

		public IInStream getStream(String filename) throws SevenZipException {
			return archiveOpenVolumeCallback.getStream(filename);
		}

	}

	public class PasswordArchiveOpenCallback implements IArchiveOpenCallback, ICryptoGetTextPassword {

		/**
		 * {@inheritDoc}
		 */

		public void setCompleted(Long files, Long bytes) {
		}

		/**
		 * {@inheritDoc}
		 */

		public void setTotal(Long files, Long bytes) {
		}

		/**
		 * {@inheritDoc}
		 */

		public String cryptoGetTextPassword() throws SevenZipException {
			return passwordToUse;
		}

	}

	public class PasswordArchiveExtractCallback implements IArchiveExtractCallback, ICryptoGetTextPassword {

		private final ISequentialOutStream outputStream;
		private ExtractOperationResult extractOperationResult;

		public PasswordArchiveExtractCallback(ISequentialOutStream outputStream) {
			this.outputStream = outputStream;
		}

		/**
		 * {@inheritDoc}
		 */

		public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
			return outputStream;
		}

		/**
		 * {@inheritDoc}
		 */

		public boolean prepareOperation(ExtractAskMode extractAskMode) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */

		public void setOperationResult(ExtractOperationResult extractOperationResult) {
			this.extractOperationResult = extractOperationResult;
		}

		/**
		 * {@inheritDoc}
		 */

		public void setCompleted(long completeValue) {
		}

		/**
		 * {@inheritDoc}
		 */

		public void setTotal(long total) {
		}

		/**
		 * {@inheritDoc}
		 */

		public String cryptoGetTextPassword() throws SevenZipException {
			return passwordToUse;
		}

		public ExtractOperationResult getExtractOperationResult() {
			return extractOperationResult;
		}
	}

	public static class ExtractOperationResultException extends SevenZipException {
		private static final long serialVersionUID = 42L;

		public ExtractOperationResultException(ExtractOperationResult extractOperationResult) {
			super("Extract operation returns error: " + extractOperationResult);
		}
	}

	public static class VolumeArchiveOpenCallback implements IArchiveOpenCallback, IArchiveOpenVolumeCallback,
			ICryptoGetTextPassword {

		private RandomAccessFile randomAccessFile;
		private String currentFilename;
		private List<RandomAccessFile> openedRandomAccessFileList = new ArrayList<RandomAccessFile>();

		public VolumeArchiveOpenCallback(String firstFilename) {
			currentFilename = firstFilename;
		}

		/**
		 * ${@inheritDoc}
		 */
		public Object getProperty(PropID propID) {
			switch (propID) {
			case NAME:
				return currentFilename;
			}
			return null;
		}

		/**
		 * ${@inheritDoc}
		 */

		public IInStream getStream(String filename) {
			currentFilename = filename;
			try {
				RandomAccessFile newRandomAccessFile = new RandomAccessFile(filename, "r");
				if (newRandomAccessFile == null) {
					return null;
				}
				randomAccessFile = newRandomAccessFile;
				if (randomAccessFile != null) {
					openedRandomAccessFileList.add(randomAccessFile);
				}
				return new RandomAccessFileInStream(randomAccessFile);
			} catch (FileNotFoundException fileNotFoundException) {
				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public void close() throws IOException {
			for (RandomAccessFile file : openedRandomAccessFileList) {
				file.close();
			}
		}

		/**
		 * ${@inheritDoc}
		 */

		public void setCompleted(Long files, Long bytes) {
		}

		/**
		 * ${@inheritDoc}
		 */

		public void setTotal(Long files, Long bytes) {
		}

		public String cryptoGetTextPassword() throws SevenZipException {
			return "a";
		}
	}

}
