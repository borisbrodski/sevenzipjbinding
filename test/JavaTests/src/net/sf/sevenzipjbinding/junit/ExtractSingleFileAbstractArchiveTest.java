package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Random;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
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
public abstract class ExtractSingleFileAbstractArchiveTest {
	private static final String SINGLE_FILE_ARCHIVE_PATH = "testdata/simple";
	private final ArchiveFormat archiveFormat;
	private final int compression1;
	private final int compression2;
	private final int compression3;
	private final String extention;

	public ExtractSingleFileAbstractArchiveTest(ArchiveFormat archiveFormat, int compression1, int compression2,
			int compression3) {
		this(archiveFormat, archiveFormat.toString().toLowerCase(), compression1, compression2, compression3);
	}

	public ExtractSingleFileAbstractArchiveTest(ArchiveFormat archiveFormat, String extention, int compression1,
			int compression2, int compression3) {
		this.archiveFormat = archiveFormat;
		this.extention = extention;
		this.compression1 = compression1;
		this.compression2 = compression2;
		this.compression3 = compression3;
	}

	@Test
	public void testZip1Compression1() {
		testSingleFileArchiveExtraction(1, compression1);
	}

	@Test
	public void testZip1Compression2() {
		testSingleFileArchiveExtraction(1, compression2);
	}

	@Test
	public void testZip1Compression3() {
		testSingleFileArchiveExtraction(1, compression3);
	}

	@Test
	public void testZip2Compression1() {
		testSingleFileArchiveExtraction(2, compression1);
	}

	@Test
	public void testZip2Compression2() {
		testSingleFileArchiveExtraction(2, compression2);
	}

	@Test
	public void testZip2Compression3() {
		testSingleFileArchiveExtraction(2, compression3);
	}

	@Test
	public void testZip3Compression1() {
		testSingleFileArchiveExtraction(3, compression1);
	}

	@Test
	public void testZip3Compression2() {
		testSingleFileArchiveExtraction(3, compression2);
	}

	@Test
	public void testZip3Compression3() {
		testSingleFileArchiveExtraction(3, compression3);
	}

	@Test
	public void testZip4Compression1() {
		testSingleFileArchiveExtraction(4, compression1);
	}

	@Test
	public void testZip4Compression2() {
		testSingleFileArchiveExtraction(4, compression2);
	}

	@Test
	public void testZip4Compression3() {
		testSingleFileArchiveExtraction(4, compression3);
	}

	@Test
	public void testZip5Compression1() {
		testSingleFileArchiveExtraction(5, compression1);
	}

	@Test
	public void testZip5Compression2() {
		testSingleFileArchiveExtraction(5, compression2);
	}

	@Test
	public void testZip5Compression3() {
		testSingleFileArchiveExtraction(5, compression3);
	}

	private void testSingleFileArchiveExtraction(int fileIndex, int compressionIndex) {
		String archiveFilename = SINGLE_FILE_ARCHIVE_PATH + File.separatorChar + archiveFormat.toString().toLowerCase()
				+ File.separatorChar + //
				"simple" + fileIndex + ".dat." + compressionIndex + "." + extention;
		String expectedFilename = SINGLE_FILE_ARCHIVE_PATH + File.separatorChar + "simple" + fileIndex + ".dat";
		try {
			ISevenZipInArchive inArchive = SevenZip.openInArchive(archiveFormat, new RandomAccessFileInStream(
					new RandomAccessFile(archiveFilename, "r")));
			SingleFileSequentialOutStreamComparator outputStream = new SingleFileSequentialOutStreamComparator(
					expectedFilename);
			inArchive.extractSlow(0, outputStream);
			outputStream.checkAndCloseInputFile();
			inArchive.close();
		} catch (SevenZipException exception) {
			throw new RuntimeException(exception);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private class SingleFileSequentialOutStreamComparator implements ISequentialOutStream {
		private InputStream fileInputStream;
		private Random random = new Random();

		public SingleFileSequentialOutStreamComparator(String expectedFilename) throws FileNotFoundException {
			File file = new File(expectedFilename);
			assertTrue("Expect-File " + expectedFilename + " doesn't exists", file.exists());

			fileInputStream = new FileInputStream(file);
		}

		void checkAndCloseInputFile() {
			try {
				assertEquals("Expected data larger that extracted data", -1, fileInputStream.read());
			} catch (IOException e) {
				throw new RuntimeException("Error reading 'expected' input file (testing for EOF)", e);
			}
		}

		@Override
		public int write(byte[] data) {
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

			return count;
		}
	}
}
