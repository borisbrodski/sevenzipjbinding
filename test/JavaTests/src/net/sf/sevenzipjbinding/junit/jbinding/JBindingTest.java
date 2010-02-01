package net.sf.sevenzipjbinding.junit.jbinding;

import static org.junit.Assert.assertEquals;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

public class JBindingTest extends JUnitNativeTestBase {
    private static native String singleCallSession1(int param);

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

}
