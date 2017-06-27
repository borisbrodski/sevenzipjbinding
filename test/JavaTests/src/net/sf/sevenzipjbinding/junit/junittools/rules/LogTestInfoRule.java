package net.sf.sevenzipjbinding.junit.junittools.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.TestLogger;
import net.sf.sevenzipjbinding.junit.junittools.RuntimeInfoAnnotation;

/**
 * Log general information about current test: name, thread, run time, ...
 *
 * @author Boris Brodski
 * @since 15.09-2.01
 */
public class LogTestInfoRule implements TestRule {
    private static class LogTestInfoStatement extends Statement {
        private static int dotCounter;

        private final Statement base;
        private final Description description;

        LogTestInfoStatement(Statement base, Description description) {
            this.base = base;
            this.description = description;
        }

        @Override
        public void evaluate() throws Throwable {
            if (!TestConfiguration.getCurrent().isTrace()) {
                printDotForTest();
            }
            Throwable exception = null;
            long start = System.currentTimeMillis();
            String addons = "";
            RuntimeInfoAnnotation annotation = description.getAnnotation(RuntimeInfoAnnotation.class);
            if (annotation != null) {
                if (annotation.isDoRunMultithreaded()) {
                    addons += "(Multithreaded)";
                }
            }
            String startMsg = "--- " + description.getDisplayName() + " " + addons + " ---------------------------";
            printInDashes(startMsg, '-');
            try {
                base.evaluate();
            } catch (Throwable t) {
                exception = t;
            }

            try {
                String status;
                if (exception == null) {
                    status = "OK";
                } else {
                    status = "FAILED";
                    TestLogger.logWithoutPrefix(exception);
                }
                String time = String.format("%1.2f", (System.currentTimeMillis() - start) / 1000.0) + " s";
                TestLogger.logWithoutPrefix(
                        "====> " + status + ", " + time + " <================ " + description.getDisplayName() + "\n");
            } catch (Throwable t) {
                if (exception == null) {
                    throw t;
                }
                t.printStackTrace();
            }

            if (exception != null) {
                throw exception;
            }
        }

        private void printDotForTest() {
            synchronized (LogTestInfoStatement.class) {
                dotCounter++;
                if (dotCounter % 80 == 0) {
                    System.out.println();
                }
            }
            System.out.print(".");
            System.out.flush();
        }

        private void printInDashes(String message, char dashChar) {
            String dashes = new String(new char[message.length()]).replace('\0', dashChar);
            TestLogger.logWithoutPrefix(dashes + "\n" + message + "\n" + dashes);
        }
    }

    public Statement apply(Statement base, Description description) {
        return new LogTestInfoStatement(base, description);
    }
}
