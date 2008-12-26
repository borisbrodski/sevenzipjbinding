package net.sf.sevenzip.test.junit;

import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

import net.sf.sevenzip.ArchiveFormat;
import net.sf.sevenzip.IArchiveOpenCallback;
import net.sf.sevenzip.ICryptoGetTextPassword;
import net.sf.sevenzip.ISevenZipInArchive;
import net.sf.sevenzip.PropID;
import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.impl.RandomAccessFileInStream;

import org.junit.Assert;
import org.junit.Test;

public class CryptedFilenamesTests extends TestBase {
	String[] EXPECTED_FILENAMES = { "smallfile1.bin", "smallfile2.bin",
			"smallfile3.bin", "smallfile4.bin", "smallfile5.bin",
			"smallfile6.bin", "smallfile7.bin" };

	private static final class OpenCryptoCallback implements
			IArchiveOpenCallback, ICryptoGetTextPassword {

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

	@Test
	public void cryptedFilenamesTest_StandardOpen() throws Exception {
		Set<String> filenames = new HashSet<String>();
		for (String filename : EXPECTED_FILENAMES) {
			filenames.add(filename);
		}
		ISevenZipInArchive inArchive = SevenZip.openInArchive(
				ArchiveFormat.SEVEN_ZIP, new RandomAccessFileInStream(
						new RandomAccessFile(
								"miscarchives/crypted-filenames.7z", "r")),
				new OpenCryptoCallback());
		Assert.assertEquals(Integer.valueOf(EXPECTED_FILENAMES.length),
				inArchive.getNumberOfItems());

		for (int index = 0; index < EXPECTED_FILENAMES.length; index++) {
			String filename = (String) inArchive
					.getProperty(index, PropID.PATH);
			Assert.assertTrue(filenames.contains(filename));
			filenames.remove(filename);
		}

		inArchive.close();
	}

	@Test
	public void cryptedFilenamesTest_SpecialOpen() throws Exception {
		Set<String> filenames = new HashSet<String>();
		for (String filename : EXPECTED_FILENAMES) {
			filenames.add(filename);
		}
		ISevenZipInArchive inArchive = SevenZip.openInArchive(
				ArchiveFormat.SEVEN_ZIP, new RandomAccessFileInStream(
						new RandomAccessFile(
								"miscarchives/crypted-filenames.7z", "r")),
				"password");
		Assert.assertEquals(Integer.valueOf(EXPECTED_FILENAMES.length),
				inArchive.getNumberOfItems());

		for (int index = 0; index < EXPECTED_FILENAMES.length; index++) {
			String filename = (String) inArchive
					.getProperty(index, PropID.PATH);
			Assert.assertTrue(filenames.contains(filename));
			filenames.remove(filename);
		}

		inArchive.close();
	}

	// @Test
	// public void test() throws Exception {
	// ISevenZipInArchive archive = SevenZip.openInArchive(
	// ArchiveFormat.SEVEN_ZIP, new RandomAccessFileInStream(
	// new RandomAccessFile(new File(
	// "TestArchives/TestArchive1.7z"), "r")));
	//
	// for (int index = 0; index < archive.getNumberOfItems(); index++) {
	// System.out.println("Filename: '"
	// + archive.getProperty(index, PropID.PATH)
	// + "', packed size: "
	// + archive.getProperty(index, PropID.PACKED_SIZE));
	// }
	//
	// // ISimpleInArchive simpleInArchive = archive.getSimpleInterface();
	// //
	// // ISimpleInArchiveItem[] archiveItems =
	// // simpleInArchive.getArchiveItems();
	// // for (ISimpleInArchiveItem inItem : archiveItems) {
	// // System.out.println("Filename: '" + inItem.getPath()
	// // + "', packed size: " + inItem.getPackedSize());
	// // }
	// }
}
