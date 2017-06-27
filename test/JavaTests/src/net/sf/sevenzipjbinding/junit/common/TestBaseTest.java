package net.sf.sevenzipjbinding.junit.common;

import static org.junit.Assert.assertEquals;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.junit.AbstractTestContext;
import net.sf.sevenzipjbinding.junit.TestBase;
import net.sf.sevenzipjbinding.junit.TestLogger;
import net.sf.sevenzipjbinding.junit.common.TestBaseTest.TestBaseTestContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.LongRunning;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.ParameterIgnores;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

public class TestBaseTest extends TestBase<TestBaseTestContext> {
    public static class TestBaseTestContext extends AbstractTestContext {
        int param1;
    }
    private static class CloseableEmpty implements Closeable {
        boolean closed = false;

        public void close() throws IOException {
            closed = true;
        }
    }

    private final String a;
    private final int b;
    private static CloseableEmpty closeableEmpty;

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

    @Test(timeout = 2000)
    @Multithreaded
    @Ignore
    public void test3() {
        log("Endless loop");
        while (true) {
        }
    }

    @Multithreaded
    @Repeat
    @Test(expected = IndexOutOfBoundsException.class)
    public void testExpectedException() {
        throw new IndexOutOfBoundsException();
    }

    @Test
    public void testClosable() {
        if (closeableEmpty != null) {
            assertEquals(closeableEmpty.closed, true);
        }
        closeableEmpty = new CloseableEmpty();
        addCloseable(closeableEmpty);

    }

    @Test
    @LongRunning
    public void testLongRunning() {
        log("LONG RUNNING TEST!!!");
    }

    @Test
    @LongRunning
    @Multithreaded
    public void testLongRunningWithMT() {
        log("LONG RUNNING TEST!!!");
    }

    @ParameterIgnores
    public static boolean isParameterIgnores(String a, int b) {
        if (a.equals("c") && b == 8) {
            TestLogger.log("IGNORING SET: [a=" + a + ", b=" + b + "]");
            return true;
        }
        return false;
    }

    @Test
    public void testContext() {
        context().param1 = 1;
        assertEquals(1, context().param1);
    }
}
