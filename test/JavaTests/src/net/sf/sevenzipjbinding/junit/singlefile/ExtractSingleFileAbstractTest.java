package net.sf.sevenzipjbinding.junit.singlefile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.ExtractFileAbstractTest;

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
public abstract class ExtractSingleFileAbstractTest extends ExtractFileAbstractTest {
	private static final String SINGLE_FILE_TEST_DATA_PATH = "testdata/simple";

	public ExtractSingleFileAbstractTest(ArchiveFormat archiveFormat, int compression1, int compression2,
			int compression3) {
		super(archiveFormat, compression1, compression2, compression3);
	}

	public ExtractSingleFileAbstractTest(ArchiveFormat archiveFormat, String extention, int compression1,
			int compression2, int compression3) {
		super(archiveFormat, extention, compression1, compression2, compression3);
	}

	@Override
	protected void doTestSingleFileArchiveExtraction(int fileIndex, int compressionIndex, boolean autodetectFormat)
			throws SevenZipException {
		String uncommpressedFilename = "simple" + fileIndex + ".dat";
		String expectedFilename = SINGLE_FILE_TEST_DATA_PATH + File.separatorChar + uncommpressedFilename;

		ExtractionInArchiveTestHelper extractionInArchiveTestHelper = new ExtractionInArchiveTestHelper();
		ISevenZipInArchive inArchive = extractionInArchiveTestHelper.openArchiveFileWithSevenZip(fileIndex,
				compressionIndex, autodetectFormat);

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
					PasswordArchiveExtractCallback extractCallback = new PasswordArchiveExtractCallback(outputStream);
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
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		} finally {
			inArchive.close();
			if (outputStream != null) {
				outputStream.closeInputFile();
			}

			extractionInArchiveTestHelper.closeAllStreams();
		}
	}

	@Override
	protected String getTestDataPath() {
		return SINGLE_FILE_TEST_DATA_PATH;
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
}
