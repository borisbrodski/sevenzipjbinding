package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.junit.junittools.MyRunner;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(MyRunner.class)
public class TestBase {
    @BeforeClass
    public static void printTestConfiguration() throws Exception {
        TestConfiguration.printParameter();
    }
}
