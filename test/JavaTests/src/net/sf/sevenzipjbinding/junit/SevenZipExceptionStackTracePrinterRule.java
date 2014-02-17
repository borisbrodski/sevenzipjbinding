package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SevenZipExceptionStackTracePrinterRule implements TestRule {
    private static class SevenZipExceptionStackTracePrinterStatement extends Statement {
        private Statement base;

        SevenZipExceptionStackTracePrinterStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                base.evaluate();
            } catch (SevenZipException sevenZipException) {
                sevenZipException.printStackTraceExtended();
                throw sevenZipException;
            }
        }
    }

    public Statement apply(Statement base, Description description) {
        return new SevenZipExceptionStackTracePrinterStatement(base);
    }
}
