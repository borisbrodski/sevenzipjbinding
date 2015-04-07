package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sf.sevenzipjbinding.junit.DebugModeOnly;
import net.sf.sevenzipjbinding.junit.DebugModeOnlyTestRule;

import org.junit.Rule;
import org.junit.Test;

public class JBindingTest extends JBindingToolsTestBase {
    private static class Callback1Impl implements Callback1 {
        public String test(int i) {
            return simpleCallbackMethod(i);
        }
    }

    private static native String checkAddingRemovingObjects(int objectCount);

    private static native String callSimpleCallbackMethod(int parameter);

    private static native String singleCallSessionWithCallback1(Callback1Impl callback1Impl, long number);

    static String simpleCallbackMethod(int i) {
        if (i < 0) {
            throw new RuntimeException("i < 0");
        }
        return "Java: i = " + i;
    }

    @Rule
    public DebugModeOnlyTestRule skipDebugModeOnlyTestRule = new DebugModeOnlyTestRule();

    @Test
    @DebugModeOnly
    public void testSingleCallSession() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("OK", checkAddingRemovingObjects(1));
        }
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSessionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSession();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSession2() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("OK", checkAddingRemovingObjects(2));
        }
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSession2Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSession2();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSession3() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("OK", checkAddingRemovingObjects(3));
        }
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSession3Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSession3();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSession10() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("OK", checkAddingRemovingObjects(10));
        }
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSession10Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSession10();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testCallSimpleCallbackMethod() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertEquals("Java: i = 2", callSimpleCallbackMethod(2));
        }
    }

    @Test
    @DebugModeOnly
    public void testCallSimpleCallbackMethodMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallSimpleCallbackMethod();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testCallSimpleCallbackMethodWithException() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                callSimpleCallbackMethod(-2);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                ExceptionHandlingTest.checkException(runtimeException);
                assertEquals("i < 0", runtimeException.getMessage());
            }
        }
    }

    @Test
    @DebugModeOnly
    public void testCallSimpleCallbackMethodWithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallSimpleCallbackMethodWithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSessionWithCallback1() {
        // TODO uncomment loop
        //        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
        assertEquals("Java: i = 1", singleCallSessionWithCallback1(new Callback1Impl(), 1));
        //        }
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSessionWithCallback1Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testSingleCallSessionWithCallback1();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testSingleCallSessionWithCallback1Exception() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            try {
                singleCallSessionWithCallback1(new Callback1Impl(), -1);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                ExceptionHandlingTest.checkException(runtimeException);
                assertEquals("i < 0", runtimeException.getMessage());
            }
        }
    }

    @Test
    @DebugModeOnly
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
