package net.sf.sevenzipjbinding.junit.singlefile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Before;
import org.junit.Test;

/**
 * This test tests extraction of archives with a single file. Test data: <code>testdata/simple</code>.<br>
 * <br>
 * Following properties will be verified:
 * <ul>
 * <li>PATH
 * <li>SIZE
 * <li>PACKED_SIZE
 * <li>IS_FOLDER
 * <li>ENCRYPTED
 * </ul>
 * 
 * Following properties will be NOT verified:
 * <ul>
 * <li>HANDLER_ITEM_INDEX
 * <li>NAME
 * <li>EXTENSION
 * <li>ATTRIBUTES
 * <li>CREATION_TIME
 * <li>LAST_ACCESS_TIME
 * <li>LAST_WRITE_TIME
 * <li>SOLID
 * <li>COMMENTED
 * <li>SPLIT_BEFORE
 * <li>SPLIT_AFTER
 * <li>DICTIONARY_SIZE
 * <li>CRC
 * <li>TYPE
 * <li>IS_ANTI
 * <li>METHOD
 * <li>HOST_OS
 * <li>FILE_SYSTEM
 * <li>USER
 * <li>GROUP
 * <li>BLOCK
 * <li>COMMENT
 * <li>POSITION
 * <li>PREFIX
 * <li>TOTAL_SIZE
 * <li>FREE_SPACE
 * <li>CLUSTER_SIZE
 * <li>VOLUME_NAME
 * <li>LOCAL_NAM
 * <li>PROVIDER
 * </ul>
 * 
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public abstract class ExtractSingleFileAbstractTest extends JUnitNativeTestBase {
	private static final int SINGLE_TEST_THREAD_COUNT = 5;
	private static final int SINGLE_TEST_REPEAT_COUNT = 1;
	private static final int SINGLE_TEST_TIMEOUT = 100000;
	private static final String SINGLE_FILE_ARCHIVE_PATH = "testdata/simple";
	private static final String DEFAULT_PASSWORD = "TestPass";
	private final ArchiveFormat archiveFormat;
	private final int compression1;
	private final int compression2;
	private final int compression3;
	private final String extention;
	private String passwordToUse;
	private boolean usingPassword = false;
	private String cryptedArchivePrefix = "";
	private String volumedArchivePrefix = "";
	private String volumeArchivePostfix = "";
	private boolean usingHeaderPassword = false;
	private boolean usingPasswordCallback = false;
	private boolean usingVolumes = false;

	public ExtractSingleFileAbstractTest(ArchiveFormat archiveFormat, int compression1, int compression2,
			int compression3) {
		this(archiveFormat, archiveFormat.toString().toLowerCase(), compression1, compression2, compression3);
	}

	public ExtractSingleFileAbstractTest(ArchiveFormat archiveFormat, String extention, int compression1,
			int compression2, int compression3) {
		this.archiveFormat = archiveFormat;
		this.extention = extention;
		this.compression1 = compression1;
		this.compression2 = compression2;
		this.compression3 = compression3;
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
		testSingleFileArchiveExtraction(1, compression1, false, false);
	}

	@Test
	public void test1Compression1Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression1, false, true);
	}

	@Test
	public void test1Compression1FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression1, true, false);
	}

	@Test
	public void test1Compression1FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression1, true, true);
	}

	@Test
	public void test1Compression2() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression2, false, false);
	}

	@Test
	public void test1Compression2Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression2, false, true);
	}

	@Test
	public void test1Compression2FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression2, true, false);
	}

	@Test
	public void test1Compression2FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression2, true, true);
	}

	@Test
	public void test1Compression3() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression3, false, false);
	}

	@Test
	public void test1Compression3Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression3, false, true);
	}

	@Test
	public void test1Compression3FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression3, true, false);
	}

	@Test
	public void test1Compression3FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(1, compression3, true, true);
	}

	@Test
	public void test2Compression1() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression1, false, false);
	}

	@Test
	public void test2Compression1Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression1, false, true);
	}

	@Test
	public void test2Compression1FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression1, true, false);
	}

	@Test
	public void test2Compression1FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression1, true, true);
	}

	@Test
	public void test2Compression2() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression2, false, false);
	}

	@Test
	public void test2Compression2Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression2, false, true);
	}

	@Test
	public void test2Compression2FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression2, true, false);
	}

	@Test
	public void test2Compression2FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression2, true, true);
	}

	@Test
	public void test2Compression3() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression3, false, false);
	}

	@Test
	public void test2Compression3Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression3, false, true);
	}

	@Test
	public void test2Compression3FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression3, true, false);
	}

	@Test
	public void test2Compression3FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(2, compression3, true, true);
	}

	@Test
	public void test3Compression1() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression1, false, false);
	}

	@Test
	public void test3Compression1Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression1, false, true);
	}

	@Test
	public void test3Compression1FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression1, true, false);
	}

	@Test
	public void test3Compression1FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression1, true, true);
	}

	@Test
	public void test3Compression2() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression2, false, false);
	}

	@Test
	public void test3Compression2Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression2, false, true);
	}

	@Test
	public void test3Compression2FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression2, true, false);
	}

	@Test
	public void test3Compression2FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression2, true, true);
	}

	@Test
	public void test3Compression3() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression3, false, false);
	}

	@Test
	public void test3Compression3Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression3, false, true);
	}

	@Test
	public void test3Compression3FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression3, true, false);
	}

	@Test
	public void test3Compression3FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(3, compression3, true, true);
	}

	@Test
	public void test4Compression1() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression1, false, false);
	}

	@Test
	public void test4Compression1Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression1, false, true);
	}

	@Test
	public void test4Compression1FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression1, true, false);
	}

	@Test
	public void test4Compression1FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression1, true, true);
	}

	@Test
	public void test4Compression2() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression2, false, false);
	}

	@Test
	public void test4Compression2Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression2, false, true);
	}

	@Test
	public void test4Compression2FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression2, true, false);
	}

	@Test
	public void test4Compression2FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression2, true, true);
	}

	@Test
	public void test4Compression3() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression3, false, false);
	}

	@Test
	public void test4Compression3Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression3, false, true);
	}

	@Test
	public void test4Compression3FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression3, true, false);
	}

	@Test
	public void test4Compression3FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(4, compression3, true, true);
	}

	@Test
	public void test5Compression1() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression1, false, false);
	}

	@Test
	public void test5Compression1Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression1, false, true);
	}

	@Test
	public void test5Compression1FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression1, true, false);
	}

	@Test
	public void test5Compression1FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression1, true, true);
	}

	@Test
	public void test5Compression2() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression2, false, false);
	}

	@Test
	public void test5Compression2Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression2, false, true);
	}

	@Test
	public void test5Compression2FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression2, true, false);
	}

	@Test
	public void test5Compression2FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression2, true, true);
	}

	@Test
	public void test5Compression3() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression3, false, false);
	}

	@Test
	public void test5Compression3Multithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression3, false, true);
	}

	@Test
	public void test5Compression3FormatAutodetect() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression3, true, false);
	}

	@Test
	public void test5Compression3FormatAutodetectMultithreaded() throws SevenZipException {
		testSingleFileArchiveExtraction(5, compression3, true, true);
	}

	private void testSingleFileArchiveExtraction(final int fileIndex, final int compressionIndex,
			final boolean autodetectFormat, boolean multithreaded) throws SevenZipException {
		if (multithreaded) {
			final int[] threadsFinished = new int[] { SINGLE_TEST_THREAD_COUNT };
			final Throwable[] firstThrowable = new Throwable[] { null };
			for (int i = 0; i < SINGLE_TEST_THREAD_COUNT; i++) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							testSingleFileArchiveExtraction(fileIndex, compressionIndex, autodetectFormat, false);
						} catch (Throwable e) {
							synchronized (firstThrowable) {
								if (firstThrowable[0] == null) {
									firstThrowable[0] = e;
								}
							}
						} finally {
							synchronized (ExtractSingleFileAbstractTest.this) {
								try {
									threadsFinished[0]--;
									ExtractSingleFileAbstractTest.this.notify();
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
				doTestSingleFileArchiveExtraction(fileIndex, compressionIndex, autodetectFormat);
			}
		}
	}

	private void doTestSingleFileArchiveExtraction(int fileIndex, int compressionIndex, boolean autodetectFormat)
			throws SevenZipException {
		String uncommpressedFilename = "simple" + fileIndex + ".dat";
		String expectedFilename = SINGLE_FILE_ARCHIVE_PATH + File.separatorChar + uncommpressedFilename;

		String archiveFilename = SINGLE_FILE_ARCHIVE_PATH + File.separatorChar + archiveFormat.toString().toLowerCase()
				+ File.separatorChar
				+ //
				volumedArchivePrefix + cryptedArchivePrefix + "simple" + fileIndex + ".dat." + compressionIndex + "."
				+ extention + volumeArchivePostfix;

		if (!new File(archiveFilename).exists() && extention.contains("part1.rar")) {
			archiveFilename = archiveFilename.replace("part1.rar", "rar");
		}

		//		System.out.println("Opening '" + archiveFilename + "'");
		RandomAccessFileInStream randomAccessFileInStream = null;
		try {
			randomAccessFileInStream = new RandomAccessFileInStream(new RandomAccessFile(archiveFilename, "r"));
			ISevenZipInArchive inArchive;
			VolumeArchiveOpenCallback volumeArchiveOpenCallback = null;
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

			SingleFileSequentialOutStreamComparator outputStream = null;
			try {
				//			System.out.println("Extracting...");
				outputStream = new SingleFileSequentialOutStreamComparator(expectedFilename);
				//			System.out.println(inArchive.getNumberOfItems());
				//			for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
				//				System.out.println(inArchive.getStringProperty(i, PropID.PATH));
				//			}
				assertTrue(inArchive.getNumberOfItems() > 0);
				int index = archiveFormat == ArchiveFormat.ISO ? 1 : 0;

				checkPropertyPath(inArchive, index, uncommpressedFilename);
				checkPropertySize(inArchive, index, expectedFilename);
				checkPropertyPackedSize(inArchive, index, expectedFilename);
				checkPropertyIsFolder(inArchive, index);
				ExtractOperationResult operationResult;
				if (usingPassword) {
					if (usingPasswordCallback) {
						PasswordArchiveExtractCallback extractCallback = new PasswordArchiveExtractCallback(
								outputStream);
						inArchive.extract(new int[] { index }, false, extractCallback);
						operationResult = extractCallback.getExtractOperationResult();
					} else {
						operationResult = inArchive.extractSlow(index, outputStream, passwordToUse);
					}
				} else {
					operationResult = inArchive.extractSlow(index, outputStream);
				}
				if (ExtractOperationResult.OK != operationResult) {
					throw new ExtractOperationResultException(operationResult);
				}
				outputStream.checkAndCloseInputFile();
				outputStream = null;

				checkPropertyIsEncrypted(inArchive, index, expectedFilename);

			} finally {
				inArchive.close();
				if (outputStream != null) {
					outputStream.closeInputFile();
				}
			}
			randomAccessFileInStream.close();
			if (volumeArchiveOpenCallback != null) {
				volumeArchiveOpenCallback.close();
			}
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		} finally {
			if (randomAccessFileInStream != null) {
				try {
					randomAccessFileInStream.close();
				} catch (IOException e) {
					throw new Error(e);
				}
			}
		}
	}

	private void checkPropertyIsEncrypted(ISevenZipInArchive inArchive, int index, String uncommpressedFilename)
			throws SevenZipException {
		Boolean isEncrypted1 = (Boolean) inArchive.getProperty(index, PropID.ENCRYPTED);
		Boolean isEncrypted2 = inArchive.getSimpleInterface().getArchiveItem(index).isEncrypted();

		long unpackedSize = Long.valueOf(new File(uncommpressedFilename).length());
		if (unpackedSize == 0) {
			// ENCRYPTED flag doesn't really meaningful for zero length files
			return;
		}
		assertNotNull(isEncrypted1);
		assertNotNull(isEncrypted1);
		if (usingPassword || usingHeaderPassword) {
			assertTrue("File reported not to be crypted (PropID.ENCRYPTED)", isEncrypted1);
		} else {
			assertFalse("File reported to be crypted (PropID.ENCRYPTED)", isEncrypted1);
		}
		assertEquals("Simple interface problem: ENCRYPTED", isEncrypted1, isEncrypted2);
	}

	private void checkPropertyIsFolder(ISevenZipInArchive inArchive, int index) throws SevenZipException {
		Boolean isFolder1 = (Boolean) inArchive.getProperty(index, PropID.IS_FOLDER);
		Boolean isFolder2 = inArchive.getSimpleInterface().getArchiveItem(index).isFolder();

		assertNotNull(isFolder1);
		assertNotNull(isFolder2);
		assertFalse("File reported to be a directory (PropID.IS_FOLDER)", isFolder1);
		assertEquals("Simple interface problem: IS_FOLDER", isFolder1, isFolder2);
	}

	private void checkPropertySize(ISevenZipInArchive inArchive, int index, String uncommpressedFilename)
			throws SevenZipException {
		if (archiveFormat == ArchiveFormat.BZIP2 || archiveFormat == ArchiveFormat.Z) {
			// It looks that Bzip2 doesn't support SIZE property
			return;
		}
		Long size1 = (Long) inArchive.getProperty(index, PropID.SIZE);
		Long size2 = inArchive.getSimpleInterface().getArchiveItem(index).getSize();

		Long actual = Long.valueOf(new File(uncommpressedFilename).length());
		assertNotNull(size1);
		assertNotNull(size2);
		assertEquals("Wrong size of the file (PropID.SIZE)", actual, size1);
		assertEquals("Simple interface problem: wrong size of the file", actual, size2);
	}

	private void checkPropertyPackedSize(ISevenZipInArchive inArchive, int index, String uncommpressedFilename)
			throws SevenZipException {
		Long size1 = (Long) inArchive.getProperty(index, PropID.PACKED_SIZE);
		Long size2 = inArchive.getSimpleInterface().getArchiveItem(index).getPackedSize();

		long unpackedSize = Long.valueOf(new File(uncommpressedFilename).length());
		long expectedPackedSize;
		if (unpackedSize < 1024) {
			expectedPackedSize = 1024;
		} else {
			expectedPackedSize = unpackedSize * 2;
		}
		assertNotNull(size1);
		assertNotNull(size2);
		assertTrue("Packed size == 0 (PropID.PACKED_SIZE)", unpackedSize == 0 || size1 != 0);
		assertTrue("Wrong size of the file (PropID.PACKED_SIZE): expected=" + expectedPackedSize + ", actual=" + size1,
				expectedPackedSize >= size1);
		assertEquals("Simple interface problem: wrong size of the file", size1, size2);
	}

	private void checkPropertyPath(ISevenZipInArchive inArchive, int index, String uncommpressedFilename)
			throws SevenZipException {
		if (archiveFormat != ArchiveFormat.BZIP2 && archiveFormat != ArchiveFormat.GZIP
				&& archiveFormat != ArchiveFormat.LZMA && archiveFormat != ArchiveFormat.Z) {
			// Skip name test for Bzip2 and GZip.
			// File name are not supported by this stream compression methods
			Object nameInArchive = inArchive.getProperty(index, PropID.PATH);
			String nameInArchiveUsingStringProperty = inArchive.getStringProperty(index, PropID.PATH);
			assertEquals("Wrong name of the file in archive", uncommpressedFilename, nameInArchive);
			assertEquals("Wrong name of the file in archive (using getStringProperty() method)", uncommpressedFilename,
					nameInArchiveUsingStringProperty);
		}
	}

	private class SingleFileSequentialOutStreamComparator implements ISequentialOutStream {
		private InputStream fileInputStream;
		private long processed = 0;
		private Random random = new Random();
		private Throwable firstException;

		public SingleFileSequentialOutStreamComparator(String expectedFilename) throws FileNotFoundException {
			File file = new File(expectedFilename);
			assertTrue("Expect-File " + expectedFilename + " doesn't exists", file.exists());

			fileInputStream = new FileInputStream(file);
		}

		public void closeInputFile() {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				throw new RuntimeException("Error closing 'expected' input file", e);
			}
		}

		void checkAndCloseInputFile() {
			try {

				if (firstException instanceof RuntimeException) {
					throw (RuntimeException) firstException;
				}
				if (firstException instanceof Error) {
					throw (Error) firstException;

				}
				assertNull("Exception of some wrong type was caught: " + firstException, firstException);

				assertEquals("Expected data larger that extracted data (processed: " + processed + ")", -1,
						fileInputStream.read());
			} catch (IOException e) {
				throw new RuntimeException("Error reading 'expected' input file (testing for EOF)", e);
			} finally {
				closeInputFile();
			}
		}

		@Override
		public int write(byte[] data) {
			try {
				assertTrue(data.length > 0);

				int count = random.nextInt(data.length) + 1;

				for (int i = 0; i < count; i++) {
					int n;
					try {
						n = fileInputStream.read();
					} catch (IOException e) {
						throw new RuntimeException("Error reading 'expected' input file", e);
					}
					assertTrue("Extracted data larger that expected file: Unexpected end of file in fileInputStream",
							n >= 0);
					assertEquals("Extracted data doesn't match exptected data", (byte) n, data[i]);
				}
				processed += count;
				return count;
			} catch (RuntimeException runtimeException) {
				if (firstException == null) {
					firstException = runtimeException;
				}
			} catch (Error error) {
				if (firstException == null) {
					firstException = error;
				}
			}
			return data.length;
		}
	}

	class CombinedArchiveOpenCallback implements IArchiveOpenCallback, ICryptoGetTextPassword,
			IArchiveOpenVolumeCallback {
		private final ICryptoGetTextPassword cryptoGetTextPassword;
		private final IArchiveOpenVolumeCallback archiveOpenVolumeCallback;
		private final IArchiveOpenCallback archiveOpenCallback;

		CombinedArchiveOpenCallback(IArchiveOpenCallback archiveOpenCallback,
				ICryptoGetTextPassword cryptoGetTextPassword, IArchiveOpenVolumeCallback archiveOpenVolumeCallback) {
			this.archiveOpenCallback = archiveOpenCallback;
			this.cryptoGetTextPassword = cryptoGetTextPassword;
			this.archiveOpenVolumeCallback = archiveOpenVolumeCallback;

		}

		@Override
		public void setCompleted(Long files, Long bytes) throws SevenZipException {
			archiveOpenCallback.setCompleted(files, bytes);
		}

		@Override
		public void setTotal(Long files, Long bytes) throws SevenZipException {
			archiveOpenCallback.setTotal(files, bytes);
		}

		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return cryptoGetTextPassword.cryptoGetTextPassword();
		}

		@Override
		public Object getProperty(PropID propID) throws SevenZipException {
			return archiveOpenVolumeCallback.getProperty(propID);
		}

		@Override
		public IInStream getStream(String filename) throws SevenZipException {
			return archiveOpenVolumeCallback.getStream(filename);
		}

	}

	class PasswordArchiveOpenCallback implements IArchiveOpenCallback, ICryptoGetTextPassword {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setCompleted(Long files, Long bytes) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setTotal(Long files, Long bytes) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return passwordToUse;
		}

	}

	class PasswordArchiveExtractCallback implements IArchiveExtractCallback, ICryptoGetTextPassword {

		private final ISequentialOutStream outputStream;
		private ExtractOperationResult extractOperationResult;

		public PasswordArchiveExtractCallback(ISequentialOutStream outputStream) {
			this.outputStream = outputStream;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
			return outputStream;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean prepareOperation(ExtractAskMode extractAskMode) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setOperationResult(ExtractOperationResult extractOperationResult) {
			this.extractOperationResult = extractOperationResult;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setCompleted(long completeValue) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setTotal(long total) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return passwordToUse;
		}

		ExtractOperationResult getExtractOperationResult() {
			return extractOperationResult;
		}
	}

	static class ExtractOperationResultException extends SevenZipException {
		private static final long serialVersionUID = 42L;

		ExtractOperationResultException(ExtractOperationResult extractOperationResult) {
			super("Extract operation returns error: " + extractOperationResult);
		}
	}

	static class VolumeArchiveOpenCallback implements IArchiveOpenCallback, IArchiveOpenVolumeCallback,
			ICryptoGetTextPassword {

		private RandomAccessFile randomAccessFile;
		private String currentFilename;
		private List<RandomAccessFile> openedRandomAccessFileList = new ArrayList<RandomAccessFile>();

		VolumeArchiveOpenCallback(String firstFilename) {
			currentFilename = firstFilename;
		}

		/**
		 * ${@inheritDoc}
		 */
		@Override
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
		@Override
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
		@Override
		public void setCompleted(Long files, Long bytes) {
		}

		/**
		 * ${@inheritDoc}
		 */
		@Override
		public void setTotal(Long files, Long bytes) {
		}

		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return "a";
		}
	}
}
