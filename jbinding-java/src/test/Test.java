package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.PropertyInfo;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class Test {
	// private static String testFilenames[] = { "TestArchives//TestContent.7z",
	// "TestArchives//TestContent.zip", "TestArchives//TestContent.rar", };

	private static String cryptedTestFilenames[] = { "/home/boris/Tmp/TestContent1.zip",
			"/home/boris/Tmp/TestContent2.zip" };

	private static int written = 0;

	public static void main(String[] args) {
		try {
			String inputFile = "../testdata/doc.zip";
			ISevenZipInArchive archive = SevenZip.openInArchive(ArchiveFormat.ZIP, new RandomAccessFileInStream(
					new RandomAccessFile(new File(inputFile), "r")), new TestArchiveOpenCallback());

			System.out.println("Items in archive: " + archive.getNumberOfItems());
			archive.extract(new int[] { 0 }, false, new IArchiveExtractCallback() {

				@Override
				public void setTotal(long total) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setCompleted(long completeValue) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setOperationResult(ExtractOperationResult extractOperationResult) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean prepareOperation(ExtractAskMode extractAskMode) {
					// TODO Auto-generated method stub
					return true;
				}

				@Override
				public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
					return new ISequentialOutStream() {

						@Override
						public int write(byte[] data) {
							written += data.length;
							System.out.println(written + " (" + Runtime.getRuntime().freeMemory() + ")");
							return data.length;
						}
					};
				}

			});
			archive.close();
			System.out.println("Written: " + written);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main1(String[] args) {

		try {
			ISevenZipInArchive archive = SevenZip.openInArchive(ArchiveFormat.ZIP, new RandomAccessFileInStream(
					new RandomAccessFile(new File(cryptedTestFilenames[0]), "r")), new TestArchiveOpenCallback());

			System.out.println("Items: " + archive.getNumberOfItems());

			System.out.println("NumberOfArchiveProperties: " + archive.getNumberOfArchiveProperties());
			System.out.println("NumberOfProperties: " + archive.getNumberOfProperties());

			for (int i = 0; i < archive.getNumberOfArchiveProperties(); i++) {
				PropertyInfo propertyInfo = archive.getArchivePropertyInfo(PropID.getPropIDByIndex(i));
				System.out.println("ArchivePropertyInfo: " + propertyInfo);
				Object object = archive.getArchiveProperty(propertyInfo.propID);
				System.out.println("" + object);
			}

			for (int i = 0; i < archive.getNumberOfProperties(); i++) {
				PropertyInfo propertyInfo = archive.getPropertyInfo(PropID.getPropIDByIndex(i));
				System.out.println("PropertyInfo: " + propertyInfo);
				PropID propID = propertyInfo.propID;
				Object object = archive.getProperty(0, propID);
				System.out.println(propID.toString() + ": " + object);
			}
			System.out.println("Extracting ...");
			archive.extract(new int[] { 0, 1 }, false, new TestArchiveExtractCallback());

			// archive.openArchive(f, inStream)
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SevenZipException e) {
			e.printStackTrace();
		}

	}

	private static class ArchiveOpenCallback implements IArchiveOpenCallback, ICryptoGetTextPassword {
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

	private static class MyExtractCallback implements IArchiveExtractCallback, ICryptoGetTextPassword {

		@Override
		public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
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
		public void setOperationResult(ExtractOperationResult extractOperationResult) {
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

	public static void main2(String[] args) {
		try {
			ISevenZipInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP,
					new RandomAccessFileInStream(new RandomAccessFile(new File("D:/tmp/haskell.7z"), "r")),
					new ArchiveOpenCallback());

			int numberOfItems = inArchive.getNumberOfItems();
			System.out.println("Count of items: " + numberOfItems);
			for (int index = 0; index < numberOfItems; index++) {
				System.out.println("Name: " + inArchive.getStringProperty(index, PropID.PATH));
				System.out.println("Content: ");
				inArchive.extract(new int[] { index }, false, new MyExtractCallback());
			}

			inArchive.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SevenZipException e) {
			e.printStackTrace();
		}

		System.out.println("Finish!");
	}
}
