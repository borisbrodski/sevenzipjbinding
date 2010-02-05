package net.sf.sevenzipjbinding.junit.jbinding;

import static org.junit.Assert.assertEquals;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

public class JBindingTest extends JUnitNativeTestBase {
    private static class Callback1Impl implements Callback1 {

        public String test(int i) {
            return "Java: i = " + i;
        }

    }

    private static native String singleCallSession1(int param);

    private static native String singleCallSessionWithCallback1(Callback1Impl callback1Impl);

    @Test
    public void testSingleCallSession() {
        assertEquals("OK", singleCallSession1(1));
    }

    @Test
    public void testSingleCallSession2() {
        assertEquals("OK", singleCallSession1(2));
    }

    @Test
    public void testSingleCallSession10() {
        assertEquals("OK", singleCallSession1(10));
    }

    @Test
    public void testSingleCallSessionWithCallback1() {
        assertEquals("OK", singleCallSessionWithCallback1(new Callback1Impl()));
    }
}

interface Callback1 {
    public String test(int i);
}
