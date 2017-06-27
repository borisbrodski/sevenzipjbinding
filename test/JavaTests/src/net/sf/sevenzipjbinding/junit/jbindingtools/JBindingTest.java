package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.DebugModeOnly;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

public class JBindingTest extends JUnitNativeTestBase<VoidContext> {
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

    @Test
    @Repeat
    @Multithreaded
    @DebugModeOnly
    public void testSingleCallSession() {
        assertEquals("OK", checkAddingRemovingObjects(1));
    }

    @Test
    @Repeat
    @Multithreaded
    @DebugModeOnly
    public void testSingleCallSession2() {
        assertEquals("OK", checkAddingRemovingObjects(2));
    }

    @Test
    @Repeat
    @Multithreaded
    @DebugModeOnly
    public void testSingleCallSession3() {
        assertEquals("OK", checkAddingRemovingObjects(3));
    }

    @Test
    @Repeat
    @Multithreaded
    @DebugModeOnly
    public void testSingleCallSession10() {
        assertEquals("OK", checkAddingRemovingObjects(10));
    }

    @Test
    @Repeat
    @Multithreaded
    @DebugModeOnly
    public void testCallSimpleCallbackMethod() {
        assertEquals("Java: i = 2", callSimpleCallbackMethod(2));
    }

    @Test
    @Repeat
    @Multithreaded
    @DebugModeOnly
    public void testCallSimpleCallbackMethodWithException() {
        try {
            callSimpleCallbackMethod(-2);
            fail("No exception occurred");
        } catch (RuntimeException runtimeException) {
            ExceptionHandlingTest.checkException(runtimeException);
            assertEquals("i < 0", runtimeException.getMessage());
        }
    }

    @Test
    @Repeat
    @Multithreaded
    @DebugModeOnly
    public void testSingleCallSessionWithCallback1() {
        assertEquals("Java: i = 1", singleCallSessionWithCallback1(new Callback1Impl(), 1));
    }

    @Test
    @Repeat
    @Multithreaded
    @DebugModeOnly
    public void testSingleCallSessionWithCallback1Exception() {
        try {
            singleCallSessionWithCallback1(new Callback1Impl(), -1);
            fail("No exception occurred");
        } catch (RuntimeException runtimeException) {
            ExceptionHandlingTest.checkException(runtimeException);
            assertEquals("i < 0", runtimeException.getMessage());
        }
    }
}

interface Callback1 {
    public String test(int i);
}
