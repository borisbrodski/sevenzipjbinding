package net.sf.sevenzipjbinding.junit.junittools;

import static org.junit.Assert.fail;
import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class MultithreadedAndExpectException extends Statement {
    private Statement next;
    private final Class<? extends Throwable> expected;

    public MultithreadedAndExpectException(FrameworkMethod method, Statement next, Class<? extends Throwable> expected) {
        this.next = next;
        this.expected = expected;
    }

    @Override
    public void evaluate() throws Exception {

        final int threadCount = 3;
        final int[] threadsFinished = new int[] { threadCount };
        final Throwable[] firstThrowableArray = new Throwable[] { null };
        final Throwable[] firstExpectedThrowableArray = new Throwable[] { null };
        final int threadTimeout = 100000;

        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        next.evaluate();
                        if (expected != null) {
                            throw new Exception("Expected exception wasn't thrown: " + expected.getCanonicalName());
                        }
                    } catch (Throwable e) {
                        synchronized (firstThrowableArray) {
                            boolean wasExceptionExpected = expected != null && expected.isAssignableFrom(e.getClass());
                            if (firstThrowableArray[0] == null //
                                    && !wasExceptionExpected) {
                                firstThrowableArray[0] = e;
                            }
                            if (wasExceptionExpected) {
                                firstExpectedThrowableArray[0] = e;
                            }
                        }
                    } finally {
                        synchronized (MultithreadedAndExpectException.this) {
                            try {
                                threadsFinished[0]--;
                                MultithreadedAndExpectException.this.notify();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
        long start = System.currentTimeMillis();
        synchronized (this) {
            while (true) {
                try {
                    if (threadsFinished[0] == 0) {
                        break;
                    }
                    wait(threadCount * threadTimeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (System.currentTimeMillis() - start > threadTimeout) {
                    fail("Time out");
                }
            }
        }
        Throwable firstThrowable = firstThrowableArray[0];
        if (firstThrowable != null) {
            if (firstThrowable instanceof SevenZipException) {
                throw (SevenZipException) firstThrowable;
            }
            System.out.println(firstThrowable);
            throw new RuntimeException("Exception in underlying thread", firstThrowable);
        }
        Throwable firstExpectedThrowable = firstExpectedThrowableArray[0];
        if (firstExpectedThrowable != null) {
            if (firstExpectedThrowable instanceof Exception) {
                throw (Exception) firstExpectedThrowable;
            }
            throw (Error) firstExpectedThrowable;
        }
    }
}