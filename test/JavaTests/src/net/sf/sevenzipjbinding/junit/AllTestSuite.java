package net.sf.sevenzipjbinding.junit;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite builder for all JUnit tests.
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public class AllTestSuite extends TestSuite {

	private static boolean processRecursive = true;

	public static Test suite() throws Exception {
		String singleDir = System.getProperty("SINGLEDIR");
		String srcDir = System.getProperty("SRCDIR");
		if (singleDir != null) {
			processRecursive = false;
			File singleDirFile = new File(singleDir);
			String singleDirAbsolutePath = singleDirFile.getAbsolutePath();
			System.out.println("Processing single directory: " + singleDirAbsolutePath);
			if (!singleDirFile.exists() || !singleDirFile.isDirectory()) {
				throw new Exception("Directory '" + singleDirAbsolutePath + "' doesn't exists");
			}
			if (srcDir == null) {
				throw new Exception("Source directory not provided. Use java SRCDIR property.");
			}
			File srcDirFile = new File(srcDir);
			String srcDirAbsolutePath = srcDirFile.getAbsolutePath();
			//			System.out.println(singleDirAbsolutePath);
			//			System.out.println(srcDirAbsolutePath);
			if (!singleDirAbsolutePath.startsWith(srcDirAbsolutePath)
					|| singleDirAbsolutePath.length() == srcDirAbsolutePath.length()) {
				throw new Exception("Source directory SRCDIR is not a parent of test package directory SINGLEDIR.");
			}
			return process(singleDirFile, singleDirAbsolutePath.substring(srcDirAbsolutePath.length() + 1).replace('/',
					'.'));
		}
		System.out.println("Processing directory (recursive): " + new File(".").getAbsolutePath());
		File file = new File("src");
		if (!file.exists() || !file.isDirectory()) {
			throw new Exception("Directory '" + file.getAbsolutePath() + "' doesn't exists");
		}

		return process(file, null);
	}

	private static TestSuite process(File dir, String classname) throws Exception {
		TestSuite testSuite = new TestSuite(classname == null ? "All tests" : classname);
		File[] listFiles = dir.listFiles();
		for (File item : listFiles) {
			if (item.getName().startsWith(".svn")) {
				continue;
			}
			if (item.isDirectory()) {
				if (processRecursive) {
					testSuite.addTest(process(item, (classname == null ? "" : classname + ".") + item.getName()));
				}
			} else {
				if (classname == null) {
					continue;
				}
				if (item.getName().endsWith(".java")) {
					Class<?> clazz = Class.forName(classname + "."
							+ item.getName().substring(0, item.getName().length() - 5));
					processClass(testSuite, clazz);
				}
			}
		}
		return testSuite;

	}

	private static void processClass(TestSuite testSuite, Class<?> clazz) {
		boolean found = false;
		if (!Modifier.isAbstract(clazz.getModifiers())) {
			for (Method method : clazz.getMethods()) {
				if (method.getAnnotation(org.junit.Test.class) != null) {
					found = true;
					break;
				}
			}
		}
		if (found) {
			System.out.println("Adding class: " + clazz.getCanonicalName());
			testSuite.addTest(new JUnit4TestAdapter(clazz));
		}
		for (Class<?> subclazz : clazz.getDeclaredClasses()) {
			if (Modifier.isStatic(subclazz.getModifiers())) {
				processClass(testSuite, subclazz);
			}
		}
	}
}
