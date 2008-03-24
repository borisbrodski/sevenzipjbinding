package net.sf.sevenzip.test.junit;

import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.zip.ZipFile;

import net.sf.sevenzip.ArchiveFormat;
import net.sf.sevenzip.IInArchive;
import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.impl.InStreamImpl;
import net.sf.sevenzip.test.ZipContentComparator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTest {
	private static void reloadLibPath() throws Exception {
		// Reset the "sys_paths" field of the ClassLoader to null.
		Class<?> clazz = ClassLoader.class;
		Field field = clazz.getDeclaredField("sys_paths");
		boolean accessible = field.isAccessible();
		if (!accessible)
		field.setAccessible(true);
		// Object original = field.get(clazz);
		// Reset it to null so that whenever "System.loadLibrary" is called, it will be reconstructed with the changed value.
		field.set(clazz, null);
	}
	
	@BeforeClass
	public static void init() throws Exception {
		System.setProperty("java.library.path", "..\\SevenZipCPP\\Debug\\");
		reloadLibPath();
	}
	
	@Test
	public void simpleTest() throws Exception {
		ZipFile zipFile = new ZipFile("ArchiveContents/ArchiveContent4.zip");
		IInArchive sevenZipArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new InStreamImpl(
				new RandomAccessFile("TestArchives/TestArchive4.7z", "r")));
		
		ZipContentComparator contentComparator = new ZipContentComparator(sevenZipArchive, zipFile);
		Assert.assertTrue(contentComparator.getErrorMessage(), contentComparator.isEqual());
	}
}
