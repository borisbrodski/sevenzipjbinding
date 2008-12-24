package net.sf.sevenzip.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzip.ArchiveFormat;
import net.sf.sevenzip.ExtractAskMode;
import net.sf.sevenzip.ExtractOperationResult;
import net.sf.sevenzip.IArchiveExtractCallback;
import net.sf.sevenzip.IArchiveOpenCallback;
import net.sf.sevenzip.ICryptoGetTextPassword;
import net.sf.sevenzip.ISequentialOutStream;
import net.sf.sevenzip.ISevenZipInArchive;
import net.sf.sevenzip.PropID;
import net.sf.sevenzip.PropertyInfo;
import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.impl.RandomAccessFileInStream;

public class Test {
	// private static String testFilenames[] = { "TestArchives//TestContent.7z",
	// "TestArchives//TestContent.zip", "TestArchives//TestContent.rar", };

	private static String cryptedTestFilenames[] = {
			"TestArchives//TestContent-crypted.7z",
			"TestArchives//TestContent-files-with-pass.7z" };

	public static void main1(String[] args) {

		try {
			ISevenZipInArchive archive = SevenZip.openInArchive(
					ArchiveFormat.SEVEN_ZIP, new RandomAccessFileInStream(
							new RandomAccessFile(new File(
									cryptedTestFilenames[0]), "r")),
					new TestArchiveOpenCallback());

			System.out.println("Items: " + archive.getNumberOfItems());

			System.out.println("NumberOfArchiveProperties: "
					+ archive.getNumberOfArchiveProperties());
			System.out.println("NumberOfProperties: "
					+ archive.getNumberOfProperties());

			for (int i = 0; i < archive.getNumberOfArchiveProperties(); i++) {
				PropertyInfo propertyInfo = archive
						.getArchivePropertyInfo(PropID.getPropIDByIndex(i));
				System.out.println("ArchivePropertyInfo: " + propertyInfo);
				Object object = archive.getArchiveProperty(propertyInfo.propID);
				System.out.println("" + object);
			}

			for (int i = 0; i < archive.getNumberOfProperties(); i++) {
				PropertyInfo propertyInfo = archive.getPropertyInfo(PropID
						.getPropIDByIndex(i));
				System.out.println("PropertyInfo: " + propertyInfo);
				PropID propID = propertyInfo.propID;
				Object object = archive.getProperty(4, propID);
				System.out.println(propID.toString() + ": " + object);
			}
			System.out.println("Extracting ...");
			archive.extract(new int[] { 4, 3 }, false,
					new TestArchiveExtractCallback());

			// archive.openArchive(f, inStream)
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SevenZipException e) {
			e.printStackTrace();
		}

	}

	private static class ArchiveOpenCallback implements IArchiveOpenCallback,
			ICryptoGetTextPassword {
		@Override
		public void setCompleted(Long files, Long bytes) {
		}

		@Override
		public void setTotal(Long files, Long bytes) {
		}

		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return "haskel"; // Password for archive (7-Zip can crypt
			// filenames with it)
		}
	}

	private static class MyExtractCallback implements IArchiveExtractCallback,
			ICryptoGetTextPassword {

		@Override
		public ISequentialOutStream getStream(int index,
				ExtractAskMode extractAskMode) {
			return new ISequentialOutStream() {
				@Override
				public int write(byte[] data) {
					System.out.println(new String(data));
					return data.length;
				}
			};
		}

		@Override
		public boolean prepareOperation(ExtractAskMode extractAskMode) {
			return true;
		}

		@Override
		public void setOperationResult(
				ExtractOperationResult extractOperationResult) {
		}

		@Override
		public void setCompleted(long completeValue) {
		}

		@Override
		public void setTotal(long total) {
		}

		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return "haskel"; // Password for the current file
		}
	}

	public static void main(String[] args) {
		System.out.println("Started.");
		try {
			System.in.read();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Running");
		try {
			ISevenZipInArchive inArchive = SevenZip.openInArchive(
					ArchiveFormat.SEVEN_ZIP, new RandomAccessFileInStream(
							new RandomAccessFile(new File("D:/tmp/haskell.7z"),
									"r")), new ArchiveOpenCallback());

			int numberOfItems = inArchive.getNumberOfItems();
			System.out.println("Count of items: " + numberOfItems);
			for (int index = 0; index < numberOfItems; index++) {
				System.out.println("Name: "
						+ inArchive.getStringProperty(index, PropID.PATH));
				System.out.println("Content: ");
				inArchive.extract(new int[] { index }, false,
						new MyExtractCallback());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SevenZipException e) {
			e.printStackTrace();
		}

		System.out.println("Finish!");
	}
}
