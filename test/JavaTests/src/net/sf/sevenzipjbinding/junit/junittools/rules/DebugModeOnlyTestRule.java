package net.sf.sevenzipjbinding.junit.junittools.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import net.sf.sevenzipjbinding.junit.TestLogger;
import net.sf.sevenzipjbinding.junit.junittools.annotations.DebugModeOnly;

/**
 * This rule skips execution of tests annotated with {@link DebugModeOnly}, if <code>skip-debug-mode-tests</code> java
 * property set to <code>true</code>.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class DebugModeOnlyTestRule implements TestRule {
    public static final String SKIP_DEBUG_MODE_TESTS_PROPERTS = "skip-debug-mode-tests";

    private static class SkipDebugModeOnlyTestStatement extends Statement {
        private Statement base;
        private Description description;

        SkipDebugModeOnlyTestStatement(Statement base, Description description) {
            this.base = base;
            this.description = description;
        }

        @Override
        public void evaluate() throws Throwable {
            if (description.getAnnotation(DebugModeOnly.class) != null) {
                String value = System.getProperties().getProperty(SKIP_DEBUG_MODE_TESTS_PROPERTS);
                if (value != null && (value.trim().toLowerCase().matches("1|true"))) {
                    // Skip test execution
                    TestLogger.log(description.getClassName() + "." + description.getMethodName()
                            + "Test execution skiped due to the " + SKIP_DEBUG_MODE_TESTS_PROPERTS
                            + " property");
                    return;
                }
            }
            base.evaluate();
        }
    }

    public Statement apply(Statement base, Description description) {
        return new SkipDebugModeOnlyTestStatement(base, description);
    }
}
