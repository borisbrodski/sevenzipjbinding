package net.sf.sevenzipjbinding.junit;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

import net.sf.sevenzipjbinding.junit.junittools.MyRunner;
import net.sf.sevenzipjbinding.junit.junittools.rules.DebugModeOnlyTestRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.LogTestInfoRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.MultithreadedRule;

@RunWith(MyRunner.class)
public class TestBase {
    @Rule
    public MultithreadedRule multithreadedRule = new MultithreadedRule();

    @Rule
    public DebugModeOnlyTestRule debugModeOnlyTestRule = new DebugModeOnlyTestRule();

    @Rule
    public LogTestInfoRule logTestInfoRule = new LogTestInfoRule();

    @BeforeClass
    public static void printTestConfiguration() throws Exception {
        TestConfiguration.init();
    }

    public void log(String msg, Object... args) {
        TestLogger.log(msg, args);
    }
}
