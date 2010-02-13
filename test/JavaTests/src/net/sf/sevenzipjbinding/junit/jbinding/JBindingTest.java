package net.sf.sevenzipjbinding.junit.jbinding;

import static org.junit.Assert.assertEquals;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

public class JBindingTest extends JUnitNativeTestBase {
    private static class Callback1Impl implements Callback1 {
        public String test(int i) {
            return simpleCallbackMethod(i);
        }
    }

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
        assertEquals("OK", checkAddingRemovingObjects(1));
    }

    @Test
    public void testSingleCallSession2() {
        assertEquals("OK", checkAddingRemovingObjects(2));
    }

    @Test
    public void testSingleCallSession3() {
        assertEquals("OK", checkAddingRemovingObjects(3));
    }

    @Test
    public void testSingleCallSession10() {
        assertEquals("OK", checkAddingRemovingObjects(10));
    }

    @Test
    public void testCallSimpleCallbackMethod() {
        assertEquals("Java: i = 2", callSimpleCallbackMethod(2));
    }

    @Test
    public void testCallSimpleCallbackMethodWithException() {
        assertEquals("Exception", callSimpleCallbackMethod(-2));
    }

    @Test
    public void testCallRecursiveCallbackMethod() {
        assertEquals("i=-1", callRecursiveCallbackMethod(-1, false));
    }

    @Test
    public void testCallRecursiveCallbackMethodWithException() {
        assertEquals("Exception", callRecursiveCallbackMethod(-1, true));
    }

    @Test
    public void testCallRecursiveCallbackMethod0() {
        assertEquals("i=-1, i=0", callRecursiveCallbackMethod(0, false));
    }

    @Test
    public void testCallRecursiveCallbackMethod0WithException() {
        assertEquals("Exception, i=0", callRecursiveCallbackMethod(0, true));
    }

    @Test
    public void testCallRecursiveCallbackMethod1() {
        assertEquals("i=-1, i=0, i=1", callRecursiveCallbackMethod(1, false));
    }

    @Test
    public void testCallRecursiveCallbackMethod1WithException() {
        assertEquals("Exception, i=0, i=1", callRecursiveCallbackMethod(1, true));
    }

    @Test
    public void testCallRecursiveCallbackMethod2() {
        assertEquals("i=-1, i=0, i=1, i=2", callRecursiveCallbackMethod(2, false));
    }

    @Test
    public void testCallRecursiveCallbackMethod2WithException() {
        assertEquals("Exception, i=0, i=1, i=2", callRecursiveCallbackMethod(2, true));
    }

    @Test
    public void testCallRecursiveCallbackMethod3() {
        assertEquals("i=-1, i=0, i=1, i=2, i=3", callRecursiveCallbackMethod(3, false));
    }

    @Test
    public void testCallRecursiveCallbackMethod3WithException() {
        assertEquals("Exception, i=0, i=1, i=2, i=3", callRecursiveCallbackMethod(3, true));
    }

    @Test
    public void testCallRecursiveCallbackMethod4() {
        assertEquals("i=-1, i=0, i=1, i=2, i=3, i=4", callRecursiveCallbackMethod(4, false));
    }

    @Test
    public void testCallRecursiveCallbackMethod4WithException() {
        assertEquals("Exception, i=0, i=1, i=2, i=3, i=4", callRecursiveCallbackMethod(4, true));
    }

    @Test
    public void testSingleCallSessionWithCallback1() {
        assertEquals("Java: i = 1", singleCallSessionWithCallback1(new Callback1Impl(), 1));
    }

    @Test
    public void testSingleCallSessionWithCallback1Exception() {
        assertEquals("EXCEPTION", singleCallSessionWithCallback1(new Callback1Impl(), -1));
    }
}

interface Callback1 {
    public String test(int i);
}
