package net.sf.sevenzipjbinding.junit;

import java.io.Closeable;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

import net.sf.sevenzipjbinding.junit.junittools.MyRunner;
import net.sf.sevenzipjbinding.junit.junittools.rules.AllCPPObjectsWasFreedRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.CloseableRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.DebugModeOnlyTestRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.LogTestInfoRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.MultithreadedRule;

@RunWith(MyRunner.class)
public class TestBase {
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final int RANDOM_GLOBAL_SEED = 6;
    public static final int WEEK = 1000 * 60 * 60 * 24 * 7; // Milliseconds in a week


    @Rule
    public MultithreadedRule multithreadedRule = new MultithreadedRule();

    @Rule
    public CloseableRule closeableRule = new CloseableRule();

    @Rule
    public DebugModeOnlyTestRule debugModeOnlyTestRule = new DebugModeOnlyTestRule();

    @Rule
    public AllCPPObjectsWasFreedRule allCPPObjectsWasFreedRule = new AllCPPObjectsWasFreedRule();

    @Rule
    public LogTestInfoRule logTestInfoRule = new LogTestInfoRule();

    private static final ThreadLocal<Random> random = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random(RANDOM_GLOBAL_SEED);
        };
    };

    @BeforeClass
    public static void printTestConfiguration() throws Exception {
        TestConfiguration.init();
    }

    public void log() {
        log("");
    }
    public void log(String msg, Object... args) {
        TestLogger.log(msg, args);
    }

    public void log(Throwable throwable, String msg, Object... args) {
        TestLogger.log(throwable, msg, args);
    }

    public static Random getRandom() {
        return random.get();
    }

    /**
     * Add closeable to be closed automatically at the end of the test.
     *
     * @param closeable
     *            closeable
     */
    public void addCloseable(Closeable closeable) {
        closeableRule.addCloseable(closeable);
    }

    /**
     * Remove closeable from the list.
     *
     * @param closeable
     *            closeable to remove
     */
    public void removeCloseable(Closeable closeable) {
        closeableRule.removeCloseable(closeable);
    }

    /**
     * Add closeable to be closed automatically at the end of the test.
     *
     * @param <T>
     *            Concrete type of closeable
     * @param closeable
     *            closeable
     * @return <code>closeable</code> parameter allowing call chains
     */
    public <T extends Closeable> T closeLater(T closeable) {
        closeableRule.addCloseable(closeable);
        return closeable;
    }
}
