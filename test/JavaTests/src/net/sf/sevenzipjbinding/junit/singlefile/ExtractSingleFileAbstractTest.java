package net.sf.sevenzipjbinding.junit.singlefile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.ExtractFileAbstractTest;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

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
 * @since 1.0
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

    @Test
    @Multithreaded
    @Repeat
	public void test4Compression1() throws Exception {
		testArchiveExtraction(4, compression1, false, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test4Compression1FormatAutodetect() throws Exception {
		testArchiveExtraction(4, compression1, true, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test4Compression2() throws Exception {
		testArchiveExtraction(4, compression2, false, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test4Compression2FormatAutodetect() throws Exception {
		testArchiveExtraction(4, compression2, true, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test4Compression3() throws Exception {
		testArchiveExtraction(4, compression3, false, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test4Compression3FormatAutodetect() throws Exception {
		testArchiveExtraction(4, compression3, true, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test5Compression1() throws Exception {
		testArchiveExtraction(5, compression1, false, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test5Compression1FormatAutodetect() throws Exception {
		testArchiveExtraction(5, compression1, true, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test5Compression2() throws Exception {
		testArchiveExtraction(5, compression2, false, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test5Compression2FormatAutodetect() throws Exception {
		testArchiveExtraction(5, compression2, true, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test5Compression3() throws Exception {
		testArchiveExtraction(5, compression3, false, false);
	}

    @Test
    @Multithreaded
    @Repeat
	public void test5Compression3FormatAutodetect() throws Exception {
		testArchiveExtraction(5, compression3, true, false);
	}

	@Override
	protected void doTestArchiveExtraction(int fileIndex, int compressionIndex, boolean autodetectFormat)
            throws Exception {
        String uncompressedFilename = getUncompressedFilename(fileIndex);
        String expectedFilename = SINGLE_FILE_TEST_DATA_PATH + File.separatorChar + uncompressedFilename;

        ExtractionInArchiveTestHelper extractionInArchiveTestHelper = new ExtractionInArchiveTestHelper();
        closeLater(extractionInArchiveTestHelper);

        IInArchive inArchive = extractionInArchiveTestHelper.openArchiveFileWithSevenZip(fileIndex, compressionIndex,
                autodetectFormat, "simple", "dat");
        closeLater(inArchive);

        int index = archiveFormat == ArchiveFormat.ISO ? 1 : 0;
        long sizes[] = null;

        if (archiveFormat != ArchiveFormat.Z && archiveFormat != ArchiveFormat.BZIP2) {
            sizes = new long[inArchive.getNumberOfItems()];
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = -1;
            }
            sizes[index] = (Long) inArchive.getProperty(index, PropID.SIZE);
        }

        if (archiveFormat == ArchiveFormat.CHM || archiveFormat == ArchiveFormat.NTFS) {
            index = calcSampleFileIndexInArchive(inArchive);
        }

        SingleFileSequentialOutStreamComparator outputStream = new SingleFileSequentialOutStreamComparator(inArchive,
                sizes, expectedFilename);
        closeLater(outputStream);

        assertTrue(inArchive.getNumberOfItems() > 0);
        checkPropertyPath(inArchive, index, uncompressedFilename);
        checkDataSize(inArchive, index, expectedFilename);
        checkPropertyIsFolder(inArchive, index);
        ExtractOperationResult operationResult;
        if (context().usingPassword) {
            if (context().usingPasswordCallback) {
                PasswordArchiveExtractCallback extractCallback = new PasswordArchiveExtractCallback(outputStream);
                inArchive.extract(new int[] { index }, false, extractCallback);
                operationResult = extractCallback.getExtractOperationResult();
            } else {
                operationResult = inArchive.extractSlow(index, outputStream, context().passwordToUse);
            }
        } else {
            operationResult = inArchive.extractSlow(index, outputStream);
        }
        if (ExtractOperationResult.OK != operationResult) {
            throw new ExtractOperationResultException(operationResult);
        }
        checkArchiveGeneric(inArchive);
        outputStream.check();

        if (archiveFormat != ArchiveFormat.CAB && archiveFormat != ArchiveFormat.CHM
                    && archiveFormat != ArchiveFormat.UDF) {
            checkPropertyPackedSize(inArchive, index, expectedFilename);
        }
        checkPropertyIsEncrypted(inArchive, index, expectedFilename);
	}

    protected String getUncompressedFilename(int fileIndex) {
		return "simple" + fileIndex + ".dat";
	}

	protected boolean skipSizeCheck() {
		return false;
	}

    private int calcSampleFileIndexInArchive(IInArchive inArchive) throws SevenZipException {
		int count = inArchive.getNumberOfItems();
		for (int i = 0; i < count; i++) {
			String name = (String) inArchive.getProperty(i, PropID.PATH);
			if (name.startsWith("simple")) {
				return i;
			}
		}
        fail("Can't find sample file in archive");
		return -1;
	}

	@Override
	protected String getTestDataPath() {
		return SINGLE_FILE_TEST_DATA_PATH;
	}

	private void checkPropertyIsEncrypted(IInArchive inArchive, int index, String uncommpressedFilename)
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
        if (context().usingPassword || context().usingHeaderPassword) {
			assertTrue("File reported not to be crypted (PropID.ENCRYPTED)", isEncrypted1);
		} else {
			assertFalse("File reported to be crypted (PropID.ENCRYPTED)", isEncrypted1);
		}
		assertEquals("Simple interface problem: ENCRYPTED", isEncrypted1, isEncrypted2);
	}

	private void checkPropertyIsFolder(IInArchive inArchive, int index) throws SevenZipException {
		Boolean isFolder1 = (Boolean) inArchive.getProperty(index, PropID.IS_FOLDER);
		Boolean isFolder2 = inArchive.getSimpleInterface().getArchiveItem(index).isFolder();

		assertNotNull(isFolder1);
		assertNotNull(isFolder2);
		assertFalse("File reported to be a directory (PropID.IS_FOLDER)", isFolder1);
		assertEquals("Simple interface problem: IS_FOLDER", isFolder1, isFolder2);
	}

	private void checkDataSize(IInArchive inArchive, int index, String uncommpressedFilename)
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
		if (!skipSizeCheck()) {
			assertEquals("Wrong size of the file (PropID.SIZE)", actual, size1);
			assertEquals("Simple interface problem: wrong size of the file", actual, size2);
		}
	}

	private void checkPropertyPackedSize(IInArchive inArchive, int index, String uncommpressedFilename)
			throws SevenZipException {
		Long size1 = (Long) inArchive.getProperty(index, PropID.PACKED_SIZE);
		Long size2 = inArchive.getSimpleInterface().getArchiveItem(index).getPackedSize();

		long unpackedSize = Long.valueOf(new File(uncommpressedFilename).length());
		long expectedPackedSize;
		if (unpackedSize < 1024) {
            if (inArchive.getArchiveFormat() == ArchiveFormat.FAT) {
                expectedPackedSize = 2048;
            } else {
                expectedPackedSize = 1024;
            }
		} else {
			expectedPackedSize = unpackedSize * 2;
		}
		assertNotNull(size1);
		assertNotNull(size2);
		assertTrue("Packed size == 0 (PropID.PACKED_SIZE)", unpackedSize == 0 || size1 != 0);
        assertTrue("Wrong size of the file (PropID.PACKED_SIZE): expected >= " + expectedPackedSize + ", actual="
                + size1,
				expectedPackedSize >= size1);
		assertEquals("Simple interface problem: wrong size of the file", size1, size2);
	}

	private void checkPropertyPath(IInArchive inArchive, int index, String uncommpressedFilename)
			throws SevenZipException {
		if (archiveFormat != ArchiveFormat.BZIP2 && archiveFormat != ArchiveFormat.GZIP
				&& archiveFormat != ArchiveFormat.LZMA && archiveFormat != ArchiveFormat.RPM
				&& archiveFormat != ArchiveFormat.Z) {
			// Skip name test for Bzip2 and GZip.
			// File name are not supported by this stream compression methods
			Object nameInArchive = inArchive.getProperty(index, PropID.PATH);
			String nameInArchiveUsingStringProperty = inArchive.getStringProperty(index, PropID.PATH);
            if (archiveFormat == ArchiveFormat.WIM) {
                // Remove '1\' or '1/' at the beginning of the file name
                nameInArchive = ((String) nameInArchive).replaceAll("\\A1[\\\\/]", "");
                nameInArchiveUsingStringProperty = nameInArchiveUsingStringProperty.replaceAll("\\A1[\\\\/]", "");
            }
			assertEquals("Wrong name of the file in archive", uncommpressedFilename, nameInArchive);
			assertEquals("Wrong name of the file in archive (using getStringProperty() method)", uncommpressedFilename,
					nameInArchiveUsingStringProperty);
		}
	}

    private static class SingleFileSequentialOutStreamComparator implements ISequentialOutStream, Closeable {
		private InputStream fileInputStream;
		private long processed = 0;
		private Random random = new Random();
		private Throwable firstException;
		private final IInArchive inArchive;
		private final long[] sizes;

		public SingleFileSequentialOutStreamComparator(IInArchive inArchive, long[] sizes,
				String expectedFilename) throws FileNotFoundException {
			this.inArchive = inArchive;
			this.sizes = sizes;
			File file = new File(expectedFilename);
			assertTrue("Expect-File " + expectedFilename + " doesn't exists", file.exists());

			fileInputStream = new FileInputStream(file);
		}

        public void close() throws IOException {
            fileInputStream.close();
		}

        void check() {
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
			}
		}

		public int write(byte[] data) {
			try {
				int numberOfItems = inArchive.getNumberOfItems();
				if (sizes != null) {
					assertEquals(sizes.length, numberOfItems);
				}
				for (int i = 0; i < numberOfItems; i++) {
					Long size = (Long) inArchive.getProperty(i, PropID.SIZE);
					inArchive.getProperty(i, PropID.PACKED_SIZE);
					inArchive.getProperty(i, PropID.PATH);
					if (sizes != null && sizes[i] >= 0) {
						assertEquals(Long.valueOf(sizes[i]), size);
					}
				}
			} catch (SevenZipException e1) {
				throw new Error("Error accessing 7-Zip archive out of callback method", e1);
			}

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
