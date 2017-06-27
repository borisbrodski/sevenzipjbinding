package net.sf.sevenzipjbinding.junit.junittools.rules;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import net.sf.sevenzipjbinding.junit.TestBase;
import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.TestLogger;
import net.sf.sevenzipjbinding.junit.junittools.RuntimeInfoAnnotation;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

/**
 * Run tests multithreaded, annotated with {@link Multithreaded}.
 *
 * @author Boris Brodski
 * @since 15.09-2.01
 */
public class MultithreadedRule implements TestRule {
    private Description description;

    private class MultithreadedStatement extends Statement {
        private Statement base;
        private Description description;

        MultithreadedStatement(Statement base, Description description) {
            this.base = base;
            this.description = description;
        }

        @Override
        public void evaluate() throws Throwable {
            RuntimeInfoAnnotation info = description.getAnnotation(RuntimeInfoAnnotation.class);
            int threadCount;
            int repeatCount;
            if (info != null && info.isDoRunMultithreaded()) {
                threadCount = TestConfiguration.getCurrent().getMultiThreadedThreads();
                repeatCount = TestConfiguration.getCurrent().getRepeatMultiThreadedTest();
            } else {
                threadCount = 1;
                repeatCount = TestConfiguration.getCurrent().getRepeatSingleThreadedTest();
            }
            Repeat repeatAnnotation = description.getAnnotation(Repeat.class);
            if (repeatAnnotation == null) {
                repeatCount = 1;
            } else {
                repeatCount *= repeatAnnotation.multiplyBy();
            }

            Test test = description.getAnnotation(Test.class);
            runMultithreaded(base, getExpectedException(test), threadCount, repeatCount, getTimeout());
        }

        private long getTimeout() {
            Test annotation = description.getAnnotation(Test.class);
            long timeout = 0;
            if (annotation != null) {
                timeout = annotation.timeout();
            }

            if (timeout > 0 && timeout < 1000) {
                // Timeout less then a second is interpreted as a multiple of the default timeout.
                timeout *= 1000 * TestConfiguration.getCurrent().getSingleTestTimeout();
            }
            if (timeout == 0) {
                timeout = 1000 * TestConfiguration.getCurrent().getSingleTestTimeout();
            }
            return timeout;
        }

        private Class<? extends Throwable> getExpectedException(Test annotation) {
            if (annotation == null || annotation.expected() == None.class) {
                return null;
            } else {
                return annotation.expected();
            }
        }
    }

    private static class TestExecutorCallable implements Callable<Throwable> {
        private Statement base;
        private Class<? extends Throwable> expectedException;
        private int repeatCount;

        TestExecutorCallable(Statement base, Class<? extends Throwable> expectedException, int repeatCount) {
            this.base = base;
            this.expectedException = expectedException;
            this.repeatCount = repeatCount;
        }

        public Throwable call() throws Exception {
            Throwable firstException = null;
            for (int i = 1; i <= repeatCount; i++) {
                if (i > 1) {
                    if (i <= 5 || i == repeatCount || i % 10 == 0) {
                        TestLogger.log("--- Repeating test: " + i + "/" + repeatCount);
                    }
                }
                Throwable throwable = executeExpectingException();
                if (throwable != null && firstException == null) {
                    if (i > 1) {
                        TestLogger.log("The following exception is first occured at " + i + "-th execution!");
                    }
                    firstException = throwable;
                }
            }
            return firstException;
        }

        private Throwable executeExpectingException() {
            Throwable result = null;
            try {
                base.evaluate();
                if (expectedException != null) {
                    result = new AssertionError(
                            "FAILURE: Expected exception wasn't thrown: " + expectedException.getSimpleName());
                }
            } catch (Throwable e) {
                if (expectedException == null) {
                    result = e;
                } else if (!expectedException.isAssignableFrom(e.getClass())) {
                    result = new AssertionError(
                            "FAILURE: Unexpected exception. Expected: " + expectedException.getName() //
                                    + ", got: " + e.getMessage());
                    result.setStackTrace(e.getStackTrace());
                    TestLogger.log(result.getMessage(), e);
                    return result;
                }
            }
            if (result != null) {
                TestLogger.log(result);
            }
            return result;
        }
    }

    public Statement apply(Statement base, Description description) {
        this.description = description;
        return new MultithreadedStatement(base, description);
    }

    private void verifyNoAttibutesDefined(Class<?> clazz) {
        Class<?> superClazz = clazz;
        List<String> invalidFields = new ArrayList<String>();
        //        List<Class<?>> invalidSubclasses = new ArrayList<Class<?>>();
        while (superClazz != null && superClazz != TestBase.class && superClazz != Object.class) {
            for (Field field : superClazz.getDeclaredFields()) {
                if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    invalidFields.add(superClazz.getSimpleName() + "." + field.getName());
                }
            }
            //            for (Class<?> subclass : superClazz.getDeclaredClasses()) {
            //                if (!Modifier.isStatic(subclass.getModifiers())) {
            //                    invalidSubclasses.add(subclass);
            //                }
            //            }
            superClazz = superClazz.getSuperclass();
        }
        if (invalidFields.size() > 0) {
            throwNoFieldsInMultithreadedTestAreAllowed(superClazz, invalidFields);
        }
        //        if (invalidSubclasses.size() > 0) {
        //            throwNoSubclassesInMultithreadedTestAreAllowed(superClazz, invalidSubclasses);
        //        }
    }

    //    private void throwNoSubclassesInMultithreadedTestAreAllowed(Class<?> superClazz, List<Class<?>> invalidSubclasses) {
    //        String msg = "Only static subclasses are allowed due to the presents of the @Multithreaded. Use TestContext instead. Prohibited subclasses: "
    //                + invalidSubclasses;
    //        RuntimeException exception = new RuntimeException(msg);
    //        TestLogger.logWithoutPrefix(msg, exception);
    //        throw exception;
    //    }

    private void throwNoFieldsInMultithreadedTestAreAllowed(Class<?> superClazz, List<String> invalidFields) {
        String msg = "Only static or final fields are allowed due to the presents of the @Multithreaded. Use TestContext instead. Prohibited fields: "
                + invalidFields;
        RuntimeException exception = new RuntimeException(msg);
        TestLogger.logWithoutPrefix(msg, exception);
        throw exception;
    }

    public void runMultithreaded(final Statement base, final Class<? extends Throwable> expectedException,
            int threadCount, int repeatCount, long timeoutExecution)
            throws Throwable {
        long timeout = repeatCount * timeoutExecution;
        if (threadCount == 0) {
            return;
        }
        if (threadCount > 1) {
            TestLogger.logWithoutPrefix(
                    "[Running test in " + threadCount + " threadeds, repeating " + repeatCount + " times]");
            verifyNoAttibutesDefined(description.getTestClass());
        }
        List<Future<Throwable>> futureList = new ArrayList<Future<Throwable>>();

        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        boolean createThreadsOk = false;

        try {
            for (int i = 0; i < threadCount; i++) {
                futureList.add(service.submit(new TestExecutorCallable(base, expectedException, repeatCount)));
            }
            createThreadsOk = true;
        } finally {
            try {
                service.shutdown();
            } catch (Throwable t) {
                if (createThreadsOk) {
                    throw t;
                } else {
                    t.printStackTrace(); // Don't use TestLogger because of possible exceptions
                }
            }
        }

        String stackTraces = null;
        boolean terminated = service.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        if (!terminated) {
            stackTraces = getStackTraces();
            List<Runnable> list = service.shutdownNow();
            if (list.size() > 0) {
                TestLogger.log("##################################################");
                TestLogger.log(list.size() + " CALLABLES WAS NOT EXECUTED");
                TestLogger.log("##################################################");
            }
        }

        Throwable firstFailureFound = null;
        int countFailures = 0;
        int countTimeouts = 0;
        for (Future<Throwable> future : futureList) {
            if (future.isDone()) {
                Throwable throwable = future.get();
                if (throwable != null) {
                    countFailures++;
                    if (firstFailureFound == null) {
                        firstFailureFound = throwable;
                    }
                }
            } else {
                countTimeouts++;
            }
        }
        if (countTimeouts > 0) {
            if (threadCount > 1) {
                TestLogger.logWithoutPrefix("[" + countTimeouts + " of " + threadCount + //
                        " threads timed out after %d milliseconds for " + repeatCount + " consequent executions]",
                        timeout);
            } else {
                TestLogger.logWithoutPrefix(
                        "[Timeout after %d milliseconds for " + repeatCount + " consequent executions]",
                        timeout);
            }
            TestLogger.logWithoutPrefix(stackTraces);
        }
        if (countFailures > 0) {
            TestLogger.logWithoutPrefix("[" + countFailures + " of " + threadCount + " threads failed]");
        }

        if (firstFailureFound != null) {
            throw firstFailureFound;
        }
        if (!terminated) {
            if (threadCount > 1) {
                Assert.fail(countTimeouts + " of " + threadCount + " threads timed out after " + timeout
                        + " milliseconds for " + repeatCount + " consequent executions");
            } else {
                Assert.fail("Timeout after " + timeout + " milliseconds for " + repeatCount + " consequent executions");
            }
        }
    }

    private String getStackTraces() {
        StringBuilder stringBuilder = new StringBuilder();
        Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
        for (Thread thread : stacks.keySet()) {
            stringBuilder.append(thread.toString()).append('\n');
            for (StackTraceElement stackTraceElement : thread.getStackTrace()) {
                stringBuilder.append("\tat ").append(stackTraceElement.toString()).append('\n');
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
