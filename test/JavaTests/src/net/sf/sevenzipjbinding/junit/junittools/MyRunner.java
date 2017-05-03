package net.sf.sevenzipjbinding.junit.junittools;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;

public class MyRunner extends Suite {
    public static class MultithreadedFrameworkMethod extends FrameworkMethodWithRuntimeInfo {
        public static final RuntimeInfoAnnotation RUN_MULTITHREADED_ANNOTATION = new RuntimeInfoAnnotation(true);

        public MultithreadedFrameworkMethod(Method method) {
            super(method);
        }

        @Override
        public String getName() {
            return super.getName() + " <Multithreaded>";
        }

        @Override
        protected RuntimeInfoAnnotation getRuntimeInfoAnnotation() {
            return RUN_MULTITHREADED_ANNOTATION;
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
            boolean multithreadedEnabled = TestConfiguration.getCurrent().isMultithreadedEnabled();
            for (FrameworkMethod frameworkMethod : targetClass.getAnnotatedMethods(Test.class)) {
                computeTestMethods.add(frameworkMethod);
                Method method = frameworkMethod.getMethod();
                if (multithreadedEnabled//
                        && (multithreadedOnClass || method.getAnnotation(Multithreaded.class) != null)) {
                    computeTestMethods.add(new MultithreadedFrameworkMethod(method));
                }
            }
            return computeTestMethods;
        }

        @Override
        protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test, Statement next) {
            // MultithreadedRule takes care of the expected exceptions
            return next;
        }

        @Override
        public Object createTest() throws Exception {
            return getTestClass().getOnlyConstructor().newInstance(parameterSet);
        }

        @Override
        protected String getName() {
            if (parameterSet == NO_PARAMETERS_ARRAY) {
                return "All test";
            }
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
    }

    private final ArrayList<Runner> runners = new ArrayList<Runner>();
    private final Object[] NO_PARAMETERS_ARRAY = new Object[0];

    public MyRunner(Class<?> klass) throws Throwable {
        super(klass, Collections.<Runner> emptyList());
        TestConfiguration.init();
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
        if (parametersMethod != null) {
            Object param = parametersMethod.invokeExplosively(null);
            if (param != null) {
                assertTrue("Return type for the parameter method should be a Collection", param instanceof Collection);
                Collection<?> paramCollection = (Collection<?>) param;
                if (paramCollection.size() > 0) {
                    Object element = paramCollection.iterator().next();
                    if (element instanceof Object[]) {
                        return (List<Object[]>) param;
                    }
                    List<Object[]> result = new ArrayList<Object[]>();
                    for (Object object : paramCollection) {
                        result.add(new Object[] { object });
                    }
                    return result;
                }
            }
        }

        ArrayList<Object[]> arrayList = new ArrayList<Object[]>();
        arrayList.add(NO_PARAMETERS_ARRAY);
        return arrayList;
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
