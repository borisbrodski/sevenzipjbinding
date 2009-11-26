package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestTestSuite {
	@org.junit.Test
	public void testTestSuite() throws Exception {
		TestSuite allTestSuite = (TestSuite) AllTestSuite.suite();
		Set<String> classNameSet = new HashSet<String>();

		addTestsFromTestSuite(allTestSuite, classNameSet);

		System.out.println("Tests in the suite: " + classNameSet.size());
		for (String string : classNameSet) {
			System.out.println(string);
		}

		File file = new File("src");
		assertTrue("Directory '" + file.getAbsolutePath() + "' doesn't exists", file.exists() && file.isDirectory());

		process(file, null, classNameSet);
	}

	private void addTestsFromTestSuite(TestSuite testSuite, Set<String> classNameSet) {
		for (int index = 0; index < testSuite.testCount(); index++) {
			Test test = testSuite.testAt(index);
			if (test instanceof TestSuite) {
				addTestsFromTestSuite((TestSuite) test, classNameSet);
			} else {
				String className = test.toString().replace('$', '.');
				assertFalse("Class '" + className + "' contains in the test suite more that ones", classNameSet
						.contains(className));
				classNameSet.add(className);
			}
		}
	}

	private void process(File dir, String classname, Set<String> classNameSet) throws Exception {
		File[] listFiles = dir.listFiles();
		for (File item : listFiles) {
			if (item.getName().startsWith(".svn")) {
				continue;
			}
			if (item.isDirectory()) {
				process(item, (classname == null ? "" : classname + ".") + item.getName(), classNameSet);
			} else {
				if (classname == null) {
					continue;
				}
				if (item.getName().endsWith(".java")) {
					Class<?> clazz = Class.forName(classname + "."
							+ item.getName().substring(0, item.getName().length() - 5));
					processClass(clazz, classNameSet);
				}
			}
		}
	}

	private void processClass(Class<?> clazz, Set<String> classNameSet) {
		boolean found = false;
		if (!Modifier.isAbstract(clazz.getModifiers())) {
			for (Method method : clazz.getMethods()) {
				if (method.getAnnotation(org.junit.Test.class) != null) {
					found = true;
					break;
				}
			}
		}
		if (found && !clazz.equals(this.getClass())) {
			assertTrue("Test class wasn't added to the suite. Missed class: " + clazz.getCanonicalName(), classNameSet
					.remove(clazz.getCanonicalName()));
		}
		for (Class<?> subclazz : clazz.getDeclaredClasses()) {
			if (Modifier.isStatic(subclazz.getModifiers())) {
				processClass(subclazz, classNameSet);
			}
		}
	}
}
