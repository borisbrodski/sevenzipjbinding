package net.sf.sevenzipjbinding.junit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import org.junit.Test;

/**
 * This test tests extractiotestSingleFileArchiveExtractionn of a archive with a single file. Test data:
 * <code>testdata/simple</code>.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public class ExtractSingleFileArchiveTest {
	private static final String SINGLE_FILE_ARCHIVE_PATH = "testdata/simple";

	@Test
	public void testZip1Compression0() {
		testSingleFileArchiveExtraction(ArchiveFormat.ZIP, "zip", 1, 0);
	}

	private void testSingleFileArchiveExtraction(ArchiveFormat archiveFormat, String subdir, int fileIndex,
			int compressionIndex) {
		String filename = SINGLE_FILE_ARCHIVE_PATH + File.separatorChar + subdir + File.separatorChar + //
				"simple" + fileIndex + ".dat." + compressionIndex + "." + archiveFormat.toString().toLowerCase();
		try {
			SevenZip.openInArchive(archiveFormat, new RandomAccessFileInStream(new RandomAccessFile(filename, "r")));
		} catch (SevenZipException exception) {
			throw new RuntimeException(exception);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
}
