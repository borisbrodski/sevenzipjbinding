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
import net.sf.sevenzipjbinding.PropID;
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
public abstract class ExtractSingleFileAbstractArchiveTest extends JUnitTestBase {
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
	public void test1Compression1() {
		testSingleFileArchiveExtraction(1, compression1, false);
	}

	@Test
	public void test1Compression1FormatAutodetect() {
		testSingleFileArchiveExtraction(1, compression1, true);
	}

	@Test
	public void test1Compression2() {
		testSingleFileArchiveExtraction(1, compression2, false);
	}

	@Test
	public void test1Compression2FormatAutodetect() {
		testSingleFileArchiveExtraction(1, compression2, true);
	}

	@Test
	public void test1Compression3() {
		testSingleFileArchiveExtraction(1, compression3, false);
	}

	@Test
	public void test1Compression3FormatAutodetect() {
		testSingleFileArchiveExtraction(1, compression3, true);
	}

	@Test
	public void test2Compression1() {
		testSingleFileArchiveExtraction(2, compression1, false);
	}

	@Test
	public void test2Compression1FormatAutodetect() {
		testSingleFileArchiveExtraction(2, compression1, true);
	}

	@Test
	public void test2Compression2() {
		testSingleFileArchiveExtraction(2, compression2, false);
	}

	@Test
	public void test2Compression2FormatAutodetect() {
		testSingleFileArchiveExtraction(2, compression2, true);
	}

	@Test
	public void test2Compression3() {
		testSingleFileArchiveExtraction(2, compression3, false);
	}

	@Test
	public void test2Compression3FormatAutodetect() {
		testSingleFileArchiveExtraction(2, compression3, true);
	}

	@Test
	public void test3Compression1() {
		testSingleFileArchiveExtraction(3, compression1, false);
	}

	@Test
	public void test3Compression1FormatAutodetect() {
		testSingleFileArchiveExtraction(3, compression1, true);
	}

	@Test
	public void test3Compression2() {
		testSingleFileArchiveExtraction(3, compression2, false);
	}

	@Test
	public void test3Compression2FormatAutodetect() {
		testSingleFileArchiveExtraction(3, compression2, true);
	}

	@Test
	public void test3Compression3() {
		testSingleFileArchiveExtraction(3, compression3, false);
	}

	@Test
	public void test3Compression3FormatAutodetect() {
		testSingleFileArchiveExtraction(3, compression3, true);
	}

	@Test
	public void test4Compression1() {
		testSingleFileArchiveExtraction(4, compression1, false);
	}

	@Test
	public void test4Compression1FormatAutodetect() {
		testSingleFileArchiveExtraction(4, compression1, true);
	}

	@Test
	public void test4Compression2() {
		testSingleFileArchiveExtraction(4, compression2, false);
	}

	@Test
	public void test4Compression2FormatAutodetect() {
		testSingleFileArchiveExtraction(4, compression2, true);
	}

	@Test
	public void test4Compression3() {
		testSingleFileArchiveExtraction(4, compression3, false);
	}

	@Test
	public void test4Compression3FormatAutodetect() {
		testSingleFileArchiveExtraction(4, compression3, true);
	}

	@Test
	public void test5Compression1() {
		testSingleFileArchiveExtraction(5, compression1, false);
	}

	@Test
	public void test5Compression1FormatAutodetect() {
		testSingleFileArchiveExtraction(5, compression1, true);
	}

	@Test
	public void test5Compression2() {
		testSingleFileArchiveExtraction(5, compression2, false);
	}

	@Test
	public void test5Compression2FormatAutodetect() {
		testSingleFileArchiveExtraction(5, compression2, true);
	}

	@Test
	public void test5Compression3() {
		testSingleFileArchiveExtraction(5, compression3, false);
	}

	@Test
	public void test5Compression3FormatAutodetect() {
		testSingleFileArchiveExtraction(5, compression3, true);
	}

	private void testSingleFileArchiveExtraction(int fileIndex, int compressionIndex, boolean autodetectFormat) {
		for (int i = 0; i < 10; i++) {
			testSingleFileArchiveExtraction2(fileIndex, compressionIndex, autodetectFormat);
		}
	}

	private void testSingleFileArchiveExtraction2(int fileIndex, int compressionIndex, boolean autodetectFormat) {
		String archiveFilename = SINGLE_FILE_ARCHIVE_PATH + File.separatorChar + archiveFormat.toString().toLowerCase()
				+ File.separatorChar + //
				"simple" + fileIndex + ".dat." + compressionIndex + "." + extention;
		String uncommpressedFilename = "simple" + fileIndex + ".dat";
		String expectedFilename = SINGLE_FILE_ARCHIVE_PATH + File.separatorChar + uncommpressedFilename;
		try {
			RandomAccessFileInStream randomAccessFileInStream = new RandomAccessFileInStream(new RandomAccessFile(
					archiveFilename, "r"));
			ISevenZipInArchive inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat,
					randomAccessFileInStream);
			SingleFileSequentialOutStreamComparator outputStream = new SingleFileSequentialOutStreamComparator(
					expectedFilename);
			//			System.out.println(inArchive.getNumberOfItems());
			//			for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
			//				System.out.println(inArchive.getStringProperty(i, PropID.PATH));
			//			}
			int index = archiveFormat == ArchiveFormat.ISO ? 1 : 0;

			if (archiveFormat != ArchiveFormat.BZIP2 && archiveFormat != ArchiveFormat.GZIP
					&& archiveFormat != ArchiveFormat.LZMA) {
				// Skip name test for Bzip2 and GZip.
				// File name are not supported by this stream compression methods
				Object nameInArchive = inArchive.getProperty(index, PropID.PATH);
				String nameInArchiveUsingStringProperty = inArchive.getStringProperty(index, PropID.PATH);
				assertEquals("Wrong name of the file in archive", uncommpressedFilename, nameInArchive);
				assertEquals("Wrong name of the file in archive (using getStringProperty() method)",
						uncommpressedFilename, nameInArchiveUsingStringProperty);
			}
			inArchive.extractSlow(index, outputStream);
			outputStream.checkAndCloseInputFile();
			inArchive.close();
			randomAccessFileInStream.close();
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

		public void closeInputFile() {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				throw new RuntimeException("Error closing 'expected' input file", e);
			}
		}

		void checkAndCloseInputFile() {
			try {
				assertEquals("Expected data larger that extracted data", -1, fileInputStream.read());
			} catch (IOException e) {
				throw new RuntimeException("Error reading 'expected' input file (testing for EOF)", e);
			}
			closeInputFile();
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
