package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class MyRunner extends Suite {
    private static class MultithreadedFrameworkMethod extends FrameworkMethod {
        public MultithreadedFrameworkMethod(Method method) {
            super(method);
        }

        @Override
        public String getName() {
            return super.getName() + " <Multithreaded>";
        }
    }

    public static class MultithreadedAndExpectException extends Statement {
        private Statement next;
        private final Class<? extends Throwable> expected;

        public MultithreadedAndExpectException(FrameworkMethod method, Statement next,
                Class<? extends Throwable> expected) {
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
                                boolean wasExceptionExpected = expected != null
                                        && expected.isAssignableFrom(e.getClass());
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

    private class TestClassRunner extends BlockJUnit4ClassRunner {
        private final int parameterSetNumber;

        private final Object[] parameterSet;

        TestClassRunner(Class<?> type, Object[] parameterSet, int parameterSetNumber) throws InitializationError {
            super(type);
            this.parameterSet = parameterSet;
            this.parameterSetNumber = parameterSetNumber;
        }

        @Override
        protected List<FrameworkMethod> getChildren() {
            //            List<FrameworkMethod> computeTestMethods = super.getChildren();
            List<FrameworkMethod> computeTestMethods = new ArrayList<FrameworkMethod>();
            TestClass targetClass = getTestClass();
            boolean multithreadedOnClass = targetClass.getJavaClass().getAnnotation(Multithreaded.class) != null;
            for (FrameworkMethod frameworkMethod : targetClass.getAnnotatedMethods(Test.class)) {
                computeTestMethods.add(frameworkMethod);
                Method method = frameworkMethod.getMethod();
                if (multithreadedOnClass || method.getAnnotation(Multithreaded.class) != null) {
                    computeTestMethods.add(new MultithreadedFrameworkMethod(method));
                }
            }
            return computeTestMethods;
        }

        @Override
        protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test, Statement next) {
            if (method.getClass() == MultithreadedFrameworkMethod.class) {
                Test annotation = method.getAnnotation(Test.class);
                return new MultithreadedAndExpectException(method, next, getExpectedException(annotation));
            }
            return super.possiblyExpectingExceptions(method, test, next);
        }

        private Class<? extends Throwable> getExpectedException(Test annotation) {
            if (annotation == null || annotation.expected() == None.class) {
                return null;
            } else {
                return annotation.expected();
            }
        }

        @Override
        public Object createTest() throws Exception {
            return getTestClass().getOnlyConstructor().newInstance(parameterSet);
        }

        @Override
        protected String getName() {
            return "Set " + parameterSetNumber + ": " + Arrays.toString(parameterSet);
        }

        @Override
        protected String testName(final FrameworkMethod method) {
            return method.getName() + " - " + getName();
        }

        @Override
        protected void validateZeroArgConstructor(List<Throwable> errors) {
        }

        @Override
        protected Statement classBlock(RunNotifier notifier) {
            return childrenInvoker(notifier);
        }

        @Override
        protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
            //            if (multithreaded) {
            //                return new MultithreadedTestRunner(super.withBefores(method, target, statement), method, target);
            //            }
            return super.withBefores(method, target, statement);
        }

        @Override
        protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
            long timeout = 0;
            Test annotation = method.getAnnotation(Test.class);
            if (annotation != null) {
                timeout = annotation.timeout();
            }

            if (timeout == 0) {
                timeout = JUnitNativeTestBase.SINGLE_TEST_TIMEOUT;
            }
            return timeout > 0 ? new FailAndStackDumpOnTimeout(next, timeout) : next;
        }
    }

    private final ArrayList<Runner> runners = new ArrayList<Runner>();

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public MyRunner(Class<?> klass) throws Throwable {
        super(klass, Collections.<Runner> emptyList());
        List<Object[]> parametersList = getParametersList(getTestClass());
        for (int i = 0; i < parametersList.size(); i++) {
            runners.add(new TestClassRunner(getTestClass().getJavaClass(), parametersList.get(i), i));
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getParametersList(TestClass klass) throws Throwable {
        return (List<Object[]>) getParametersMethod(klass).invokeExplosively(null);
    }

    private FrameworkMethod getParametersMethod(TestClass testClass) throws Exception {
        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);
        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }
        }

        throw new Exception("No public static parameters method on class " + testClass.getName());
    }

}
