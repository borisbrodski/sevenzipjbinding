package net.sf.sevenzipjbinding.junit.common;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.junit.TestBase;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

public class TestBaseTest extends TestBase {
    private final String a;
    private final int b;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { "a", 0 }, { "b", 1 }, { "c", 1 }, { "a", 2 }, { "b", 3 }, { "c", 5 },
                { "c", 8 } });
    }

    public TestBaseTest(String a, int b) {
        this.a = a;
        this.b = b;

    }

    @Test
    @Multithreaded
    @Repeat
    public void test1() throws Exception {
        log(Thread.currentThread() + ": Testing 1: " + a + "," + b);
    }

    @Test
    public void test2() throws Exception {
        log("Testing 2: " + a + ", " + b);
    }

    @Test
    @Multithreaded
    @Ignore
    public void test3() {
        log("Endless loop");
        while (true) {
        }
    }
}
