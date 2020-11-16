package net.sf.sevenzipjbinding.junit.multiplefiles;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.ExtractFileAbstractTest;
import net.sf.sevenzipjbinding.junit.tools.ZipContentComparator;

/**
 * This test tests extraction of archives of multiple files. Test data: <code>testdata/multiple-files</code>.<br>
 * <br>
 * Following properties will be verified:
 * <ul>
 * <li>PATH
 * </ul>
 *
 * Following properties will be NOT verified:
 * <ul>
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
 * @since 1.0
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
			throws Exception {
		String sollArchiveFilename = "archive" + fileIndex + ".zip";
		String sollFullFilename = MULTIPLE_FILES_TEST_DATA_PATH + File.separatorChar + sollArchiveFilename;

		ExtractionInArchiveTestHelper extractionInArchiveTestHelper = new ExtractionInArchiveTestHelper();
		closeLater(extractionInArchiveTestHelper);
		IInArchive inArchive = extractionInArchiveTestHelper.openArchiveFileWithSevenZip(fileIndex,
				compressionIndex, autodetectFormat, "archive", "zip");
        closeLater(inArchive);

        checkArchiveGeneric(inArchive);

        ZipFile zipFile = null;
        boolean ok = false;
        try {
            zipFile = new ZipFile(new File(sollFullFilename));
			assertTrue(inArchive.getNumberOfItems() > 0);

			ZipContentComparator zipContentComparator1 = new ZipContentComparator(archiveFormat, inArchive, zipFile,
                    false, context().usingPassword ? context().passwordToUse : null,
                    context().exceptionToBeExpected != null);
            zipContentComparator1.setRemoveFilenamePrefix(getRemoveFilenamePrefix());
            addFilesToIgnore(inArchive, zipContentComparator1);
			assertTrue(zipContentComparator1.getErrorMessage(), zipContentComparator1.isEqual());

			ZipContentComparator zipContentComparator2 = new ZipContentComparator(archiveFormat, inArchive, zipFile,
                    true, context().usingPassword ? context().passwordToUse : null,
                    context().exceptionToBeExpected != null);
            addFilesToIgnore(inArchive, zipContentComparator2);
            zipContentComparator2.setRemoveFilenamePrefix(getRemoveFilenamePrefix());

            if (archiveFormat == ArchiveFormat.WIM) {
                zipContentComparator2.addToIgnoreList("1.xml");
            }

            assertTrue(zipContentComparator2.getErrorMessage(), zipContentComparator2.isEqual());
            ok = true;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    if (ok) {
                        throw new RuntimeException(e);
                    }
                    log(e, "Error closing zip file");
                }
            }
        }
	}

    private void addFilesToIgnore(IInArchive inArchive, ZipContentComparator zipContentComparator1)
            throws SevenZipException {
        if (archiveFormat == ArchiveFormat.WIM) {
            zipContentComparator1.addToIgnoreList("1.xml");
        }
        if (archiveFormat == ArchiveFormat.NTFS) {
            int numberOfItems = inArchive.getNumberOfItems();
            for (int i = 0; i < numberOfItems; i++) {
                String path = (String) inArchive.getProperty(i, PropID.PATH);
                if (path.startsWith("[SYSTEM]")) {
                    zipContentComparator1.addToIgnoreList(path);
                }
            }
        }
    }

    public String getRemoveFilenamePrefix() {
        return null;
    }
}
