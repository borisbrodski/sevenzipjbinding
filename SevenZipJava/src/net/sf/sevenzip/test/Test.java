package net.sf.sevenzip.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import net.sf.sevenzip.ArchiveFormat;
import net.sf.sevenzip.IInArchive;
import net.sf.sevenzip.PropID;
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
					ArchiveFormat.SEVEN_ZIP, new InStreamImpl(
							new RandomAccessFile(new File(testFilenames[0]),
									"r")));

//			System.out.println("Items: " + archive.getNumberOfItems());
//			
//			System.out.println("NumberOfArchiveProperties: "
//					+ archive.getNumberOfArchiveProperties());
//			System.out.println("NumberOfProperties: "
//					+ archive.getNumberOfProperties());
//
//			for (int i = 0; i < archive.getNumberOfArchiveProperties(); i++) {
//				PropertyInfo propertyInfo = archive.getArchivePropertyInfo(i);
//				System.out.println("ArchivePropertyInfo: " + propertyInfo);
//				Object object = archive.getArchiveProperty(propertyInfo.propID);
//				System.out.println("" + object);
//			}
//			
//			for (int i = 0; i < archive.getNumberOfProperties(); i++) {
//				PropertyInfo propertyInfo = archive.getPropertyInfo(i);
//				System.out.println("PropertyInfo: " + propertyInfo);
//				PropID propID = propertyInfo.propID;
//				Object object = archive.getProperty(4, propID);
//				System.out.println(propID.toString() + ": " + object);	
//			}
			System.out.println("Extracting ...");
			archive.extract(new int[] {4, 3}, false, new TestArchiveExtractCallback());

			// archive.openArchive(f, inStream)
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SevenZipException e) {
			e.printStackTrace();
		}

	}
}
