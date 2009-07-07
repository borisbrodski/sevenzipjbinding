package net.sf.sevenzipjbinding.junit;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTestSuite extends TestSuite {

	public static Test suite() throws Exception {
		File file = new File("src");
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
				testSuite.addTest(process(item, (classname == null ? "" : classname + ".") + item.getName()));
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
			testSuite.addTest(new JUnit4TestAdapter(clazz));
		}
		for (Class<?> subclazz : clazz.getDeclaredClasses()) {
			if (Modifier.isStatic(subclazz.getModifiers())) {
				processClass(testSuite, subclazz);
			}
		}
	}
}
