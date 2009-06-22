package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

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
}
