package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.assertEquals;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.junit.tools.SevenZipDebug;

import org.junit.After;
import org.junit.BeforeClass;

/**
 * This is the base class for all JUnit test classes, that needs native library to be loaded. This class provides:<br>
 * - Initialization of the native library
 * 
 * @author Boris Brodski
 * @version 1.0
 * 
 */
public class JUnitNativeTestBase {
	private static int deadCPPObjectCount = 0;

	/**
	 * Initialize native SevenZipJBinding library for all JUnit tests
	 * 
	 * @throws SevenZipNativeInitializationException
	 *             in case initialization of SevenZipJBinding fails
	 */
	@BeforeClass
	public static void initializeSevenZipJBinding() throws SevenZipNativeInitializationException {
		SevenZip.initSevenZipFromPlatformJAR();
	}

	@After
	public void afterTest() {
		try {
			int objectCount = SevenZipDebug.getCPPObjectCount();
			int newDeadObjectCount = objectCount - deadCPPObjectCount;
			if (newDeadObjectCount != 0) {
				SevenZipDebug.printCPPObjects();
				deadCPPObjectCount = objectCount;
			}
			assertEquals("Not all CPP Objects was freed", 0, newDeadObjectCount);
		} catch (UnsatisfiedLinkError e) {
			System.out.println("WARNING! SevenZip native libraray was build without object tracing debug function.");
		}
	}
}
