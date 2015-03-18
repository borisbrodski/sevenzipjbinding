package net.sf.sevenzipjbinding.junit;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class CloseableRule implements TestRule {
    private List<Closeable> closeableToClose = Collections.synchronizedList(new ArrayList<Closeable>());
    private AtomicReference<Throwable> firstCloseException = new AtomicReference<Throwable>();

    public Statement apply(Statement base, Description description) {
        closeableToClose.clear();
        return new CloseableStatement(base, firstCloseException);
    }

    public void addCloseable(Closeable closeable) {
        closeableToClose.add(closeable);
    }

    public void removeCloseable(Closeable closeable) {
        closeableToClose.remove(closeable);
    }

    public void closeAll() {
        for (int i = closeableToClose.size() - 1; i != -1; i--) {
            try {
                closeableToClose.get(i).close();
            } catch (Throwable throwable) {
                if (firstCloseException.get() == null) {
                    firstCloseException.set(throwable);
                } else {
                    System.out.println("Ignoring consequent exceptions during closing closeables:");
                    throwable.printStackTrace();
                }
            }
        }

        closeableToClose.clear();
    }
}
