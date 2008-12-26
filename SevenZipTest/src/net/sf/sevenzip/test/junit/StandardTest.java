package net.sf.sevenzip.test.junit;

import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.zip.ZipFile;

import net.sf.sevenzip.ArchiveFormat;
import net.sf.sevenzip.ISevenZipInArchive;
import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.impl.RandomAccessFileInStream;
import net.sf.sevenzip.test.ZipContentComparator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class StandardTest {
	protected enum Testart {
		USE_STANDARD_INTERFACE, //
		USE_SIMPLE_INTERFACE,
	}

	private static void reloadLibPath() throws Exception {
		// Reset the "sys_paths" field of the ClassLoader to null.
		Class<?> clazz = ClassLoader.class;
		Field field = clazz.getDeclaredField("sys_paths");
		boolean accessible = field.isAccessible();
		if (!accessible)
			field.setAccessible(true);
		// Object original = field.get(clazz);
		// Reset it to null so that whenever "System.loadLibrary" is called, it
		// will be reconstructed with the changed value.
		field.set(clazz, null);
	}

	@BeforeClass
	public static void init() throws Exception {
		String dllPath = "..\\SevenZipCPP\\Debug\\";
		System.setProperty("java.library.path", dllPath);
		reloadLibPath();
		System.out.println("Testing: " + dllPath + "7-Zip-JBinding.dll");
	}

	protected abstract int getTestId();

	protected abstract boolean usingPassword();

	@Test
	public void simpleTest_7z_standardInterface() throws Exception {
		test(ArchiveFormat.SEVEN_ZIP, "7z", Testart.USE_STANDARD_INTERFACE);
	}

	@Test
	public void simpleTest_7z_simpleInterface() throws Exception {
		if (getTestId() == 1) {
			// Skip test #1 for sake of performence
			return;
		}
		test(ArchiveFormat.SEVEN_ZIP, "7z", Testart.USE_SIMPLE_INTERFACE);
	}

	@Test
	public void simpleTest_zip_standardInterface() throws Exception {
		test(ArchiveFormat.ZIP, "zip", Testart.USE_STANDARD_INTERFACE);
	}

	@Test
	public void simpleTest_zip_simpleInterface() throws Exception {
		test(ArchiveFormat.ZIP, "zip", Testart.USE_SIMPLE_INTERFACE);
	}

	@Test
	public void simpleTest_tar_standardInterface() throws Exception {
		test(ArchiveFormat.TAR, "tar", Testart.USE_STANDARD_INTERFACE);
	}

	@Test
	public void simpleTest_tar_simpleInterface() throws Exception {
		test(ArchiveFormat.TAR, "tar", Testart.USE_SIMPLE_INTERFACE);
	}

	@Before
	public void print1() {
		System.out.println(">>> -----------------------------------");
		System.out.flush();
	}

	@After
	public void print2() {
		System.out.println("<<< -----------------------------------");
		System.gc();
		System.out.flush();
	}

	private void test(ArchiveFormat archiveFormat, String format,
			Testart testart) throws Exception {
		int testId = getTestId();
		ZipFile zipFile = new ZipFile("ArchiveContents/ArchiveContent" + testId
				+ ".zip");
		ISevenZipInArchive sevenZipArchive = SevenZip.openInArchive(
				archiveFormat, new RandomAccessFileInStream(
						new RandomAccessFile("TestArchives/TestArchive"
								+ testId + (usingPassword() ? "-password" : "")
								+ "." + format, "r")));

		boolean useSimpleInterface = testart == null
				|| testart.equals(Testart.USE_SIMPLE_INTERFACE);
		String password = null;
		if (usingPassword()) {
			password = "password";
		}

		ZipContentComparator contentComparator1 = new ZipContentComparator(
				sevenZipArchive, zipFile, useSimpleInterface, password);

		Assert.assertTrue(contentComparator1.getErrorMessage(),
				contentComparator1.isEqual());

		ZipContentComparator contentComparator2 = new ZipContentComparator(
				sevenZipArchive, zipFile, useSimpleInterface, password);

		Assert.assertTrue(contentComparator2.getErrorMessage(),
				contentComparator2.isEqual());

		sevenZipArchive.close();

		try {
			sevenZipArchive.getNumberOfItems();
			Assert.fail("Can call methods of closed archive!");
		} catch (SevenZipException sevenZipException) {
			Assert.assertTrue(sevenZipException.getMessage().equals(
					"Can't preform action. Archive already closed."));
		}
	}
}
