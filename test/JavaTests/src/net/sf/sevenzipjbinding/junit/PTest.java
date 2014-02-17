package net.sf.sevenzipjbinding.junit;

import java.util.Arrays;
import java.util.Collection;

import net.sf.sevenzipjbinding.junit.junittools.Multithreaded;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

// TODO Remove it
public class PTest extends TestBase {
    private final String a;
    private final int b;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { "a", 0 }, { "b", 1 }, { "c", 1 }, { "a", 2 }, { "b", 3 }, { "c", 5 },
                { "c", 8 } });
    }

    public PTest(String a, int b) {
        this.a = a;
        this.b = b;

    }

    @Test
    @Multithreaded
    public void test1() throws Exception {
        System.out.println("Testing 1: " + a + ", " + b);
    }

    @Test
    public void test2() throws Exception {
        System.out.println("Testing 2: " + a + ", " + b);
    }
}
