package net.sf.sevenzipjbinding.junit;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.runners.model.Statement;

public class CloseableStatement extends Statement {

    private Statement base;
    private AtomicReference<Throwable> firstCloseException;

    public CloseableStatement(Statement base, AtomicReference<Throwable> firstCloseException) {
        this.base = base;
        this.firstCloseException = firstCloseException;
    }

    @Override
    public void evaluate() throws Throwable {
        boolean ok = false;
        try {
            base.evaluate();
            ok = true;
        } finally {
            if (ok && firstCloseException.get() != null) {
                throw firstCloseException.get();
            }
        }
    }

}
