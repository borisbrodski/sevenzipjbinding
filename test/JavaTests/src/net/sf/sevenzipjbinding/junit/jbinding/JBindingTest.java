package net.sf.sevenzipjbinding.junit.jbinding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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

    private static native String callRecursiveCallbackMethod(int parameter, boolean useException);

    private static native String singleCallSessionWithCallback1(Callback1Impl callback1Impl, long number);

    static String simpleCallbackMethod(int i) {
        if (i < 0) {
            throw new RuntimeException("i < 0");
        }
        return "Java: i = " + i;
    }

    static String recursiveCallbackMethod(int i, boolean useException) {
        if (i < 0) {
            if (useException) {
                throw new RuntimeException("EXCEPTION: i=" + i);
            }
            return "i=" + i;
        }
        return callRecursiveCallbackMethod(i - 1, useException) + ", i=" + i;
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
    public void testCallRecursiveCallbackMethod() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1", callRecursiveCallbackMethod(-1, false));
        }
    }

    @Test
    public void testCallRecursiveCallbackMethodMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethodWithException() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(-1, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                assertEquals("EXCEPTION: i=-1", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveCallbackMethodWithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethodWithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod0() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1, i=0", callRecursiveCallbackMethod(0, false));
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod0Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod0();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod0WithException() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(0, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                assertEquals("EXCEPTION: i=-1", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod0WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod0WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod1() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1, i=0, i=1", callRecursiveCallbackMethod(1, false));
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod1Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod1();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod1WithException() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(1, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                assertEquals("EXCEPTION: i=-1", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod1WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod1WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod2() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1, i=0, i=1, i=2", callRecursiveCallbackMethod(2, false));
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod2Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod2();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod2WithException() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(2, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                assertEquals("EXCEPTION: i=-1", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod2WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod2WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod3() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1, i=0, i=1, i=2, i=3", callRecursiveCallbackMethod(3, false));
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod3Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod3();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod3WithException() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(3, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                assertEquals("EXCEPTION: i=-1", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod3WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod3WithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod4() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("i=-1, i=0, i=1, i=2, i=3, i=4", callRecursiveCallbackMethod(4, false));
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod4Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod4();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveCallbackMethod4WithException() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callRecursiveCallbackMethod(4, true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                assertEquals("EXCEPTION: i=-1", runtimeException.getMessage());
            }
        }
    }

    @Test
    public void testCallRecursiveCallbackMethod4WithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveCallbackMethod4WithException();
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
}

interface Callback1 {
    public String test(int i);
}
