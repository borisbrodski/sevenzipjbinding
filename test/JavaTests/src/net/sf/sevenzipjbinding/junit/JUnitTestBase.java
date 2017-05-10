package net.sf.sevenzipjbinding.junit;

import java.util.Random;

import net.sf.sevenzipjbinding.junit.tools.RandomTools;

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

    protected Random getRandom() {
        return RandomTools.getRandom();
    }
}
