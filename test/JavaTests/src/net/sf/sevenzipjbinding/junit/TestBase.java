package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Closeable;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

import net.sf.sevenzipjbinding.junit.junittools.MyRunner;
import net.sf.sevenzipjbinding.junit.junittools.rules.AllCPPObjectsWasFreedRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.CloseableRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.DebugModeOnlyTestRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.LogTestInfoRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.MultithreadedRule;
import net.sf.sevenzipjbinding.junit.junittools.rules.RandomRule;
import net.sf.sevenzipjbinding.junit.tools.RandomTools;
import net.sf.sevenzipjbinding.junit.tools.TypeVariableResolver;
import net.sf.sevenzipjbinding.junit.tools.TypeVariableResolver.TypeVariableResolverException;

@RunWith(MyRunner.class)
public class TestBase<C extends AbstractTestContext> {
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final int RANDOM_GLOBAL_SEED = 6;
    public static final int WEEK = 1000 * 60 * 60 * 24 * 7; // Milliseconds in a week

    private static final ThreadLocal<Object> CONTEXT_THREAD_LOCAL = new ThreadLocal<Object>();

    @Rule
    public RandomRule randomRule = new RandomRule();

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

    @BeforeClass
    public static void printTestConfiguration() throws Exception {
        TestConfiguration.init();
    }

    @Before
    public void initContext() throws TypeVariableResolverException {
        initContextForTestThread();

    }
    @After
    public void clearContext() {
        CONTEXT_THREAD_LOCAL.set(null);
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
        return RandomTools.getRandom();
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

    /**
     * Return thread save test context
     */
    @SuppressWarnings("unchecked")
    protected C context() {
        Object object = CONTEXT_THREAD_LOCAL.get();
        assertNotNull("Can't get context from the non-test thread. Please, save and pass the context object yourself.",
                object);
        assertNotEquals("Can't get context. VoidContext is configured", VoidContext.class, object.getClass());
        return (C) object;
    }

    private void initContextForTestThread() throws TypeVariableResolverException {
        Class<?> contextClass = TypeVariableResolver.resolveTypeVariable(this.getClass(), TestBase.class, "C");
        try {
            Object object;
            if (VoidContext.class == contextClass) {
                object = VoidContext.class;
            } else {
                object = contextClass.newInstance();
            }
            CONTEXT_THREAD_LOCAL.set(object);
        } catch (Exception e) {
            throw new RuntimeException("Error creating context class for the test " + getClass().getName()
                    + ". Context class: " + contextClass.getName()
                    + ". Verify, that a public constructor without parameters are present.", e);
        }
    }
}
