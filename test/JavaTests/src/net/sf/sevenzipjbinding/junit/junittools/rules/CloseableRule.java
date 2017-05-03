package net.sf.sevenzipjbinding.junit.junittools.rules;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import net.sf.sevenzipjbinding.junit.TestLogger;

public class CloseableRule implements TestRule {
    private class CloseableStatement extends Statement {
        private Statement base;

        CloseableStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            boolean ok = false;
            try {
                base.evaluate();
                ok = true;
            } finally {
                try {
                    closeAll();
                } catch (Throwable t) {
                    if (ok) {
                        throw t;
                    }
                }
            }
        }
    }

    private List<Closeable> closeableToClose = Collections.synchronizedList(new ArrayList<Closeable>());

    public Statement apply(Statement base, Description description) {
        return new CloseableStatement(base);
    }

    public synchronized void addCloseable(Closeable closeable) {
        closeableToClose.add(closeable);
    }

    public synchronized void removeCloseable(Closeable closeable) {
        closeableToClose.remove(closeable);
    }

    public synchronized void closeAll() {
        Throwable firstException = null;
        for (int i = closeableToClose.size() - 1; i != -1; i--) {
            try {
                closeableToClose.get(i).close();
            } catch (Throwable throwable) {
                if (firstException == null) {
                    firstException = throwable;
                } else {
                    TestLogger.log(
                            "Ignoring consequent exceptions during closing closeables: " + closeableToClose.get(i),
                            throwable);
                }
            }
        }
        closeableToClose.clear();
        if (firstException != null) {
            if (firstException instanceof RuntimeException) {
                throw (RuntimeException) firstException;
            }
            throw new RuntimeException("Error closing closable", firstException);
        }
    }
}
