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

    private static native String singleCallSessionWithCallback1(Callback1Impl callback1Impl, long number);

    static String simpleCallbackMethod(int i) {
        if (i < 0) {
            System.out.println("Throwing exception");
            throw new RuntimeException("i < 0");
        }
        return "Java: i = " + i;
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
