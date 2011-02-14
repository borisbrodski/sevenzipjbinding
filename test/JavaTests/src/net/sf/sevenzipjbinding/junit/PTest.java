package net.sf.sevenzipjbinding.junit;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(MyRunner.class)
@Multithreaded
public class PTest {
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
    public void test1() throws Exception {
        System.out.println("Testing 1: " + a + ", " + b);
    }

    @Test
    public void test2() throws Exception {
        System.out.println("Testing 2: " + a + ", " + b);
    }
}
