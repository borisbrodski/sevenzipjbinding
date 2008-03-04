package net.sf.sevenzip.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.impl.InStreamImpl;

public class Test {
	private static String testFilenames[] = { "TestArchives//TestContent.7z",
			"TestArchives//TestContent.zip", "TestArchives//TestContent.rar", };

	public static void main(String[] args) {

		try {
			SevenZip archive = SevenZip.openArchive(SevenZip.Format.SEVEN_ZIP, new InStreamImpl(
					new RandomAccessFile(new File(testFilenames[0]), "r")));
			
			// archive.openArchive(f, inStream)
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SevenZipException e) {
			e.printStackTrace();
		}

	}
}
