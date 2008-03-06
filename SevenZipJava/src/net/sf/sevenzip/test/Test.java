package net.sf.sevenzip.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import net.sf.sevenzip.IInArchive;
import net.sf.sevenzip.PropertyInfo;
import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.impl.InStreamImpl;

public class Test {
	private static String testFilenames[] = { "TestArchives//TestContent.7z",
			"TestArchives//TestContent.zip", "TestArchives//TestContent.rar", };

	public static void main(String[] args) {

		try {
			IInArchive archive = SevenZip.openArchive(
					SevenZip.Format.RAR, new InStreamImpl(
							new RandomAccessFile(new File(testFilenames[2]),
									"r")));

			System.out.println("Items: " + archive.getNumberOfItems());
			System.out.println("NumberOfArchiveProperties: "
					+ archive.getNumberOfArchiveProperties());
			System.out.println("NumberOfProperties: "
					+ archive.getNumberOfProperties());

			for (int i = 0; i < archive.getNumberOfProperties(); i++) {
				PropertyInfo propertyInfo = archive.getPropertyInfo(i);
				System.out.println("PropertyInfo: " + propertyInfo);
				Object object = archive.getProperty(4, propertyInfo.propID);
				System.out.println("" + object);

			}

			// archive.openArchive(f, inStream)
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SevenZipException e) {
			e.printStackTrace();
		}

	}
}
