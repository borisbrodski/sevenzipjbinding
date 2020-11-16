package net.sf.sevenzipjbinding.junit;

import java.util.Date;

import org.junit.BeforeClass;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

/**
 * This is the base class for all JUnit test classes, that needs native library to be loaded. This class provides:<br>
 * - Initialization of the native library
 *
 * @param <C>
 *            TestContext class
 *
 * @author Boris Brodski
 * @since 4.65-1
 *
 */
public class JUnitNativeTestBase<C extends AbstractTestContext> extends TestBase<C> {
    protected interface RunnableThrowsException {
        public void run() throws Exception;
    }

    private static boolean initializeNativeLibrary = true;


    /**
     * Initialize native SevenZipJBinding library for all JUnit tests
     *
     * @throws SevenZipNativeInitializationException
     *             in case initialization of SevenZipJBinding fails
     */
    @BeforeClass
    public static void initializeSevenZipJBinding() throws SevenZipNativeInitializationException {
        if (initializeNativeLibrary) {
            SevenZip.initSevenZipFromPlatformJAR();
            initializeNativeLibrary = false;
        }
    }

    public static Date getDate(int period) {
        return new Date(new Date().getTime() - getRandom().nextInt(period) - period);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Throwable> T getExceptionCauseByClass(Class<T> exceptionClass, Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            if (exceptionClass.isInstance(cause)) {
                return (T) cause;
            }
            cause = cause.getCause();
        }
        e.printStackTrace();
        throw new AssertionError("Exception " + exceptionClass.getName() + " is missing as a cause of the exception: "
                + e.getMessage());
    }

}
