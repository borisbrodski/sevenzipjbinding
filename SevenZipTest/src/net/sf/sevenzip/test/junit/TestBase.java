package net.sf.sevenzip.test.junit;

import java.lang.reflect.Field;

import org.junit.BeforeClass;

public class TestBase {
	private static boolean initialized = false;

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
		if (initialized) {
			return;
		}
		initialized = true;

		String dllPath = "..\\SevenZipCPP\\Release\\";
		System.setProperty("java.library.path", dllPath);
		reloadLibPath();
		System.out.println("Testing: " + dllPath + "7-Zip-JBinding.dll");
	}
}
