package net.sf.sevenzipjbinding.junit.junittools.rules;

import static org.junit.Assert.assertEquals;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import net.sf.sevenzipjbinding.junit.TestLogger;
import net.sf.sevenzipjbinding.junit.tools.SevenZipDebug;

public class AllCPPObjectsWasFreedRule implements TestRule {
    private class CloseableStatement extends Statement {
        private Statement base;

        CloseableStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            boolean ok = false;
            saveDeadCPPObjects();
            try {
                base.evaluate();
                ok = true;
            } finally {
                try {
                    checkAllCPPObjectsWasFreed();
                } catch (Throwable t) {
                    if (ok) {
                        throw t;
                    }
                }
            }
        }
    }

    private static boolean nonDebugModusDetected;
    private int deadCPPObjectCount;

    public Statement apply(Statement base, Description description) {
        return new CloseableStatement(base);
    }

    private void saveDeadCPPObjects() {
        if (nonDebugModusDetected) {
            return;
        }
        try {
            deadCPPObjectCount = SevenZipDebug.getCPPObjectCount();
        } catch (UnsatisfiedLinkError e) {
            if (!nonDebugModusDetected) {
                System.out.println(
                        "WARNING! SevenZip native libraray was build" + " without object tracing debug function.");
                nonDebugModusDetected = true;
            }
        }
    }

    private void checkAllCPPObjectsWasFreed() {
        if (nonDebugModusDetected) {
            return;
        }
        int objectCount = SevenZipDebug.getCPPObjectCount();
        int newDeadObjectCount = objectCount - deadCPPObjectCount;
        if (newDeadObjectCount != 0) {
            if (deadCPPObjectCount > 0) {
                TestLogger.log("Note: " + deadCPPObjectCount + " objects was already dead before the test");
            }
            SevenZipDebug.printCPPObjects();
        }
        assertEquals("Not all CPP Objects was freed.", 0, newDeadObjectCount);

        int threadCount = SevenZipDebug.getAttachedThreadCount();
        assertEquals("Not all attached thread was detached from VM", 0, threadCount);
    }
}
