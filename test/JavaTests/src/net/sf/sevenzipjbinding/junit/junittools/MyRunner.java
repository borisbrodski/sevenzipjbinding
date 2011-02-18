package net.sf.sevenzipjbinding.junit.junittools;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.sevenzipjbinding.junit.TestConfiguration;

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
        protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
            long timeout = 0;
            Test annotation = method.getAnnotation(Test.class);
            if (annotation != null) {
                timeout = annotation.timeout();
            }

            if (timeout == 0) {
                timeout = TestConfiguration.getTimeout();
            }
            return timeout > 0 ? new FailAndStackDumpOnTimeout(next, timeout) : next;
        }
    }

    private final ArrayList<Runner> runners = new ArrayList<Runner>();

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
        FrameworkMethod parametersMethod = getParametersMethod(klass);
        if (parametersMethod == null) {
            ArrayList<Object[]> arrayList = new ArrayList<Object[]>();
            arrayList.add(new Object[0]);
            return arrayList;
        }
        return (List<Object[]>) parametersMethod.invokeExplosively(null);
    }

    private FrameworkMethod getParametersMethod(TestClass testClass) throws Exception {
        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);
        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }
        }

        return null;
    }

}
