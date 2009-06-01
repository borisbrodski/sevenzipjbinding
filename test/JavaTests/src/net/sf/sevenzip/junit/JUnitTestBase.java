package net.sf.sevenzip.junit;

import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.SevenZipNativeInitializationException;

import org.junit.BeforeClass;

/**
 * This is the base class for all JUnit test classes. This class provides:<br>
 * - Initialization of the native library
 * 
 * @author boris
 * 
 */
public class JUnitTestBase {
	/**
	 * Initialize native SevenZipJBinding library for all JUnit tests
	 */
	@BeforeClass
	public static void initializeSevenZipJBinding() throws SevenZipNativeInitializationException {
		SevenZip.initSevenZipFromPlatformJAR();
	}
}
