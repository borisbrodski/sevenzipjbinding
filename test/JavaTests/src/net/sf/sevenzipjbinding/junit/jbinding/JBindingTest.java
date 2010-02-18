package net.sf.sevenzipjbinding.junit.jbinding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

public class JBindingTest extends JUnitNativeTestBase {
    private static class Callback1Impl implements Callback1 {
        public String test(int i) {
            return simpleCallbackMethod(i);
        }
    }

    private static final int TEST_REPEAT_COUNT = 100;
    private static final int THREAD_COUNT = 40;
    private static final int THREAD_TIMEOUT = 200 * TEST_REPEAT_COUNT;

    private static native String checkAddingRemovingObjects(int objectCount);

    private static native String callSimpleCallbackMethod(int parameter);

    private static native String callRecursiveCallbackMethod(int deep, int width, boolean useException)
            throws SevenZipException;

    private static native String singleCallSessionWithCallback1(Callback1Impl callback1Impl, long number);

    static String simpleCallbackMethod(int i) {
        if (i < 0) {
            throw new RuntimeException("i < 0");
        }
        return "Java: i = " + i;
    }

    static String recursiveCallbackMethod(int deep, int width, boolean useException, int widthIndex)
            throws SevenZipException {
        if (deep < 0) {
            if (useException) {
                throw new RuntimeException("EXCEPTION: i=" + deep + ":" + widthIndex);
            }
            return "i=" + deep + ":" + widthIndex;
        }
        return callRecursiveCallbackMethod(deep - 1, width, useException) + ", i=" + deep + ":" + widthIndex;
    }

    @Test
    public void testSingleCallSession() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("OK", checkAddingRemovingObjects(1));
        }
    }

    @Test
    public void testSingleCallSessionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSession();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testSingleCallSession2() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("OK", checkAddingRemovingObjects(2));
        }
    }

    @Test
    public void testSingleCallSession2Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSession2();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testSingleCallSession3() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("OK", checkAddingRemovingObjects(3));
        }
    }

    @Test
    public void testSingleCallSession3Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSession3();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testSingleCallSession10() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("OK", checkAddingRemovingObjects(10));
        }
    }

    @Test
    public void testSingleCallSession10Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSession10();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallSimpleCallbackMethod() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("Java: i = 2", callSimpleCallbackMethod(2));
        }
    }

    @Test
    public void testCallSimpleCallbackMethodMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallSimpleCallbackMethod();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallSimpleCallbackMethodWithException() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callSimpleCallbackMethod(-2);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertEquals("i < 0", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallSimpleCallbackMethodWithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallSimpleCallbackMethodWithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1:0", callRecursiveCallbackMethod(-1, 1, false));
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethodMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethodWithException() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(-1, 1, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertEquals("EXCEPTION: i=-1:0", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethodWithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethodWithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth2CallbackMethod() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("(i=-1:0,i=-1:1)", callRecursiveCallbackMethod(-1, 2, false));
        }
    }

    @Test
    public void testCallRecursiveWidth2CallbackMethodMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth2CallbackMethod();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth2CallbackMethodWithException() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(-1, 2, true);
                fail("No exception occurred");
            } catch (SevenZipException sevenZipException) {
                checkSevenZipException(sevenZipException);
                RuntimeException firstException = (RuntimeException) sevenZipException.getCause();
                RuntimeException lastException = (RuntimeException) sevenZipException.getCauseLastThrown();
                assertEquals("EXCEPTION: i=-1:0", firstException.getMessage());
                assertEquals("EXCEPTION: i=-1:1", lastException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveWidth2CallbackMethodWithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth2CallbackMethodWithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod0() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1:0, i=0:0", callRecursiveCallbackMethod(0, 1, false));
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod0Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod0();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod0WithException() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(0, 1, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertEquals("EXCEPTION: i=-1:0", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod0WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod0WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod1() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1:0, i=0:0, i=1:0", callRecursiveCallbackMethod(1, 1, false));
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod1Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod1();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod1WithException() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(1, 1, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertEquals("EXCEPTION: i=-1:0", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod1WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod1WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod2() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1:0, i=0:0, i=1:0, i=2:0", callRecursiveCallbackMethod(2, 1, false));
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod2Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod2();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod2WithException() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(2, 1, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertEquals("EXCEPTION: i=-1:0", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod2WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod2WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod3() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1:0, i=0:0, i=1:0, i=2:0, i=3:0", callRecursiveCallbackMethod(3, 1, false));
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod3Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod3();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod3WithException() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(3, 1, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertEquals("EXCEPTION: i=-1:0", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod3WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod3WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod4() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1:0, i=0:0, i=1:0, i=2:0, i=3:0, i=4:0", callRecursiveCallbackMethod(4, 1, false));
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod4Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod4();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod4WithException() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(4, 1, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertEquals("EXCEPTION: i=-1:0", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveWidth1CallbackMethod4WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWidth1CallbackMethod4WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testSingleCallSessionWithCallback1() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("Java: i = 1", singleCallSessionWithCallback1(new Callback1Impl(), 1));
        }
    }

    @Test
    public void testSingleCallSessionWithCallback1Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSessionWithCallback1();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testSingleCallSessionWithCallback1Exception() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                singleCallSessionWithCallback1(new Callback1Impl(), -1);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertEquals("i < 0", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testSingleCallSessionWithCallback1ExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSessionWithCallback1Exception();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    private void checkSevenZipException(SevenZipException sevenZipException) {
        if (sevenZipException.getCauseLastThrown() != null) {
            assertNotNull(sevenZipException.getCause());
        }
        if (sevenZipException.getCauseLastPotentialThrown() != null) {
            assertNotNull(sevenZipException.getCauseFirstPotentialThrown());
        }

        sevenZipException.getMessage();

        checkException(sevenZipException.getCause());
        checkException(sevenZipException.getCauseLastThrown());
        checkException(sevenZipException.getCauseFirstPotentialThrown());
        checkException(sevenZipException.getCauseLastPotentialThrown());
    }

    private void checkException(Throwable cause) {
        while (cause != null) {
            if (cause instanceof SevenZipException) {
                checkSevenZipException((SevenZipException) cause);
                return;
            }
            cause = cause.getCause();
        }
    }
}

interface Callback1 {
    public String test(int i);
}
