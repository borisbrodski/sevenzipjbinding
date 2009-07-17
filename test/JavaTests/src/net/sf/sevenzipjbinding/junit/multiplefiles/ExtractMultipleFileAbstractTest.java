package net.sf.sevenzipjbinding.junit.multiplefiles;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.ExtractFileAbstractTest;
import net.sf.sevenzipjbinding.junit.tools.ZipContentComparator;

/**
 * This test tests extraction of archives of multiple files. Test data: <code>testdata/multiple-files</code>.<br>
 * <br>
 * Following properties will be verified:
 * <ul>
 * </ul>
 * 
 * Following properties will be NOT verified:
 * <ul>
 * <li>PATH
 * <li>SIZE
 * <li>PACKED_SIZE
 * <li>IS_FOLDER
 * <li>ENCRYPTED
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
public abstract class ExtractMultipleFileAbstractTest extends ExtractFileAbstractTest {
	private static final String MULTIPLE_FILES_TEST_DATA_PATH = "testdata/multiple-files";

	public ExtractMultipleFileAbstractTest(ArchiveFormat archiveFormat, int compression1, int compression2,
			int compression3) {
		super(archiveFormat, compression1, compression2, compression3);
	}

	public ExtractMultipleFileAbstractTest(ArchiveFormat archiveFormat, String extention, int compression1,
			int compression2, int compression3) {
		super(archiveFormat, extention, compression1, compression2, compression3);
	}

	@Override
	protected String getTestDataPath() {
		return MULTIPLE_FILES_TEST_DATA_PATH;
	}

	@Override
	protected void doTestArchiveExtraction(int fileIndex, int compressionIndex, boolean autodetectFormat)
			throws SevenZipException {
		String sollArchiveFilename = "archive" + fileIndex + ".zip";
		String sollFullFilename = MULTIPLE_FILES_TEST_DATA_PATH + File.separatorChar + sollArchiveFilename;

		ExtractionInArchiveTestHelper extractionInArchiveTestHelper = new ExtractionInArchiveTestHelper();
		ISevenZipInArchive inArchive = extractionInArchiveTestHelper.openArchiveFileWithSevenZip(fileIndex,
				compressionIndex, autodetectFormat, "archive", "zip");

		ZipFile zipFile = null;
		try {

			System.out.println("Opening: " + sollFullFilename);
			zipFile = new ZipFile(new File(sollFullFilename));
			assertTrue(inArchive.getNumberOfItems() > 0);

			ZipContentComparator zipContentComparator1 = new ZipContentComparator(archiveFormat, inArchive, zipFile,
					false, usingPassword ? passwordToUse : null);
			assertTrue(zipContentComparator1.getErrorMessage(), zipContentComparator1.isEqual());

			ZipContentComparator zipContentComparator2 = new ZipContentComparator(archiveFormat, inArchive, zipFile,
					true, usingPassword ? passwordToUse : null);

			assertTrue(zipContentComparator2.getErrorMessage(), zipContentComparator2.isEqual());
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		} finally {
			inArchive.close();
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			extractionInArchiveTestHelper.closeAllStreams();
		}
	}
	//	private void checkPropertyIsEncrypted(ISevenZipInArchive inArchive, int index, String uncommpressedFilename)
	//			throws SevenZipException {
	//		Boolean isEncrypted1 = (Boolean) inArchive.getProperty(index, PropID.ENCRYPTED);
	//		Boolean isEncrypted2 = inArchive.getSimpleInterface().getArchiveItem(index).isEncrypted();
	//
	//		long unpackedSize = Long.valueOf(new File(uncommpressedFilename).length());
	//		if (unpackedSize == 0) {
	//			// ENCRYPTED flag doesn't really meaningful for zero length files
	//			return;
	//		}
	//		assertNotNull(isEncrypted1);
	//		assertNotNull(isEncrypted1);
	//		if (usingPassword || usingHeaderPassword) {
	//			assertTrue("File reported not to be crypted (PropID.ENCRYPTED)", isEncrypted1);
	//		} else {
	//			assertFalse("File reported to be crypted (PropID.ENCRYPTED)", isEncrypted1);
	//		}
	//		assertEquals("Simple interface problem: ENCRYPTED", isEncrypted1, isEncrypted2);
	//	}
	//
	//	private void checkPropertyIsFolder(ISevenZipInArchive inArchive, int index) throws SevenZipException {
	//		Boolean isFolder1 = (Boolean) inArchive.getProperty(index, PropID.IS_FOLDER);
	//		Boolean isFolder2 = inArchive.getSimpleInterface().getArchiveItem(index).isFolder();
	//
	//		assertNotNull(isFolder1);
	//		assertNotNull(isFolder2);
	//		assertFalse("File reported to be a directory (PropID.IS_FOLDER)", isFolder1);
	//		assertEquals("Simple interface problem: IS_FOLDER", isFolder1, isFolder2);
	//	}
	//
	//	private void checkPropertySize(ISevenZipInArchive inArchive, int index, String uncommpressedFilename)
	//			throws SevenZipException {
	//		if (archiveFormat == ArchiveFormat.BZIP2 || archiveFormat == ArchiveFormat.Z) {
	//			// It looks that Bzip2 doesn't support SIZE property
	//			return;
	//		}
	//		Long size1 = (Long) inArchive.getProperty(index, PropID.SIZE);
	//		Long size2 = inArchive.getSimpleInterface().getArchiveItem(index).getSize();
	//
	//		Long actual = Long.valueOf(new File(uncommpressedFilename).length());
	//		assertNotNull(size1);
	//		assertNotNull(size2);
	//		assertEquals("Wrong size of the file (PropID.SIZE)", actual, size1);
	//		assertEquals("Simple interface problem: wrong size of the file", actual, size2);
	//	}
	//
	//	private void checkPropertyPackedSize(ISevenZipInArchive inArchive, int index, String uncommpressedFilename)
	//			throws SevenZipException {
	//		Long size1 = (Long) inArchive.getProperty(index, PropID.PACKED_SIZE);
	//		Long size2 = inArchive.getSimpleInterface().getArchiveItem(index).getPackedSize();
	//
	//		long unpackedSize = Long.valueOf(new File(uncommpressedFilename).length());
	//		long expectedPackedSize;
	//		if (unpackedSize < 1024) {
	//			expectedPackedSize = 1024;
	//		} else {
	//			expectedPackedSize = unpackedSize * 2;
	//		}
	//		assertNotNull(size1);
	//		assertNotNull(size2);
	//		assertTrue("Packed size == 0 (PropID.PACKED_SIZE)", unpackedSize == 0 || size1 != 0);
	//		assertTrue("Wrong size of the file (PropID.PACKED_SIZE): expected=" + expectedPackedSize + ", actual=" + size1,
	//				expectedPackedSize >= size1);
	//		assertEquals("Simple interface problem: wrong size of the file", size1, size2);
	//	}
	//
	//	private void checkPropertyPath(ISevenZipInArchive inArchive, int index, String uncommpressedFilename)
	//			throws SevenZipException {
	//		if (archiveFormat != ArchiveFormat.BZIP2 && archiveFormat != ArchiveFormat.GZIP
	//				&& archiveFormat != ArchiveFormat.LZMA && archiveFormat != ArchiveFormat.Z) {
	//			// Skip name test for Bzip2 and GZip.
	//			// File name are not supported by this stream compression methods
	//			Object nameInArchive = inArchive.getProperty(index, PropID.PATH);
	//			String nameInArchiveUsingStringProperty = inArchive.getStringProperty(index, PropID.PATH);
	//			assertEquals("Wrong name of the file in archive", uncommpressedFilename, nameInArchive);
	//			assertEquals("Wrong name of the file in archive (using getStringProperty() method)", uncommpressedFilename,
	//					nameInArchiveUsingStringProperty);
	//		}
	//	}

}
