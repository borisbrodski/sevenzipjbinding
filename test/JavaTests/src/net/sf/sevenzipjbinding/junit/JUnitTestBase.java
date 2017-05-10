package net.sf.sevenzipjbinding.junit;

import java.util.Random;

/**
 * Base class for all JUnit tests.
 *
 * TODO REMOVE ME (replaced with {@link TestBase})
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class JUnitTestBase {
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final int RANDOM_GLOBAL_SEED = 6;

    protected final ThreadLocal<Random> random = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random(RANDOM_GLOBAL_SEED);
        };
    };

    public static final int WEEK = 1000 * 60 * 60 * 24 * 7; // Milliseconds in a week
    protected static final Random RANDOM = new Random(RANDOM_GLOBAL_SEED);


}
