package net.sf.sevenzipjbinding.junit;

import java.io.Closeable;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Rule;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.junit.junittools.rules.CloseableRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.SevenZipExceptionStackTracePrinterRule;

/**
 * This is the base class for all JUnit test classes, that needs native library to be loaded. This class provides:<br>
 * - Initialization of the native library
 *
 * @author Boris Brodski
 * @since 4.65-1
 *
 */
public class JUnitNativeTestBase2 extends TestBase {
    protected interface RunnableThrowsException {
        public void run() throws Exception;
    }

    private static boolean initializeNativeLibrary = true;


    @Rule
    public CloseableRule closeableRule = new CloseableRule();

    @Rule
    public SevenZipExceptionStackTracePrinterRule stackTracePrinterRule = new SevenZipExceptionStackTracePrinterRule();

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

    /**
     * Add closeable to be closed automatically at the end of the test.
     *
     * @param closeable
     *            closeable
     */
    @Override
    public void addCloseable(Closeable closeable) {
        closeableRule.addCloseable(closeable);
    }

    /**
     * Remove closeable from the list.
     *
     * @param closeable
     *            closeable to remove
     */
    public void removeCloseable(Closeable closeable) {
        closeableRule.removeCloseable(closeable);
    }

    /**
     * Add closeable to be closed automatically at the end of the test.
     *
     * @param <T>
     *            Concrete type of closeable
     * @param closeable
     *            closeable
     * @return <code>closeable</code> parameter allowing call chains
     */
    public <T extends Closeable> T closeLater(T closeable) {
        closeableRule.addCloseable(closeable);
        return closeable;
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

    protected static ThreadLocal<Long> newThreadLocalLongInit0() {
        return new ThreadLocal<Long>() {
            @Override
            protected Long initialValue() {
                return Long.valueOf(0);
            }
        };
    }
}
