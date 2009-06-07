package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

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
	 * 
	 * @throws SevenZipNativeInitializationException
	 *             in case initialization of SevenZipJBinding fails
	 */
	@BeforeClass
	public static void initializeSevenZipJBinding() throws SevenZipNativeInitializationException {
		SevenZip.initSevenZipFromPlatformJAR();
		System.out.println("!!!");
	}
}
