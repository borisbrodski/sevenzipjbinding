package net.sf.sevenzipjbinding.junit.junittools;

import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
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
import net.sf.sevenzipjbinding.junit.junittools.annotations.LongRunning;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.ParameterIgnores;
import net.sf.sevenzipjbinding.junit.junittools.annotations.ParameterNames;

public class MyRunner extends Suite {

    public static class IgnoredFrameworkMethod extends FrameworkMethod {
        public IgnoredFrameworkMethod(Method method) {
            super(method);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            if (annotationType == Ignore.class) {
                return (T) IGNORE_ANNOTATION;
            }
            return super.getAnnotation(annotationType);
        }
    }
    public static class MultithreadedFrameworkMethod extends FrameworkMethodWithRuntimeInfo {
        public static final RuntimeInfoAnnotation RUN_MULTITHREADED_ANNOTATION = new RuntimeInfoAnnotation(true);
        private boolean ignored;

        public MultithreadedFrameworkMethod(Method method, boolean ignored) {
            super(method);
            this.ignored = ignored;
        }

        @Override
        public String getName() {
            return super.getName() + " <Multithreaded>";
        }

        @Override
        protected RuntimeInfoAnnotation getRuntimeInfoAnnotation() {
            return RUN_MULTITHREADED_ANNOTATION;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            if (ignored && annotationType == Ignore.class) {
                return (T) IGNORE_ANNOTATION;
            }
            return super.getAnnotation(annotationType);
        }
    }

    private class TestClassRunner extends BlockJUnit4ClassRunner {
        private final int parameterSetNumber;

        private final Object[] parameterSet;
        private Collection<String> parameterNamesList;

        TestClassRunner(Class<?> type, Object[] parameterSet, Collection<String> parameterNamesList,
                int parameterSetNumber) throws InitializationError {
            super(type);
            this.parameterSet = parameterSet;
            this.parameterNamesList = parameterNamesList;
            this.parameterSetNumber = parameterSetNumber;
        }

        @Override
        protected List<FrameworkMethod> getChildren() {
            List<FrameworkMethod> computeTestMethods = new ArrayList<FrameworkMethod>();
            TestClass targetClass = getTestClass();
            boolean multithreadedOnClass = targetClass.getJavaClass().getAnnotation(Multithreaded.class) != null;
            boolean multithreadedEnabled = TestConfiguration.getCurrent().isMultithreadedEnabled();
            for (FrameworkMethod frameworkMethod : targetClass.getAnnotatedMethods(Test.class)) {
                Method method = frameworkMethod.getMethod();
                boolean isIgnored = isIgnored(method);
                if (isIgnored) {
                    computeTestMethods.add(new IgnoredFrameworkMethod(method));
                } else {
                    computeTestMethods.add(frameworkMethod);
                }
                if (multithreadedOnClass || method.getAnnotation(Multithreaded.class) != null) {
                    computeTestMethods
                            .add(new MultithreadedFrameworkMethod(method, isIgnored || !multithreadedEnabled));
                }
            }
            return computeTestMethods;
        }

        private boolean isIgnored(Method method) {
            if (parameterSet != NO_PARAMETERS_ARRAY) {
                TestClass testClass = new TestClass(method.getDeclaringClass());
                FrameworkMethod ignoreMethod = getMethodWithAnnotation(ParameterIgnores.class, testClass);
                if (ignoreMethod != null) {
                    try {
                        boolean result = (Boolean) ignoreMethod.invokeExplosively(null, parameterSet);
                        if (result) {
                            return true;
                        }
                    } catch (Throwable e) {
                        throw new RuntimeException("Error invoking the @ParameterIgnores method: " + ignoreMethod, e);
                    }
                }
            }

            if (method.isAnnotationPresent(LongRunning.class)) {
                return !TestConfiguration.getCurrent().isLongRunning();
            }
            return false;
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

            String setDescription;
            if (parameterNamesList == null) {
                setDescription = Arrays.toString(parameterSet);
            } else {
                StringBuilder sb = new StringBuilder();
                Iterator<String> nameIterator = parameterNamesList.iterator();
                sb.append('[');
                for (int i = 0; i < parameterSet.length; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    if (nameIterator.hasNext()) {
                        String name = nameIterator.next();
                        sb.append(name);
                        sb.append(": ");
                    }
                    sb.append(parameterSet[i]);
                }
                sb.append(']');
                setDescription = sb.toString();
            }
            return "Set " + parameterSetNumber + ": " + setDescription;
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

    static final Ignore IGNORE_ANNOTATION = new Ignore() {
        public Class<? extends Annotation> annotationType() {
            return Ignore.class;
        }

        public String value() {
            return "Ignored";
        }
    };
    private final ArrayList<Runner> runners = new ArrayList<Runner>();
    private final Object[] NO_PARAMETERS_ARRAY = new Object[0];

    public MyRunner(Class<?> klass) throws Throwable {
        super(klass, Collections.<Runner> emptyList());
        TestConfiguration.init();
        TestClass testClass = getTestClass();
        List<Object[]> parametersList = getParametersList(testClass);
        Collection<String> parameterNamesList = getParameterNamesList(testClass);
        for (int i = 0; i < parametersList.size(); i++) {
            runners.add(new TestClassRunner(testClass.getJavaClass(), parametersList.get(i), parameterNamesList, i));
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getParameterNamesList(TestClass klass) throws Throwable {
        FrameworkMethod parameterNamesMethod = getMethodWithAnnotation(ParameterNames.class, klass);
        if (parameterNamesMethod != null) {
            return (Collection<String>) parameterNamesMethod.invokeExplosively(null);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getParametersList(TestClass klass) throws Throwable {
        FrameworkMethod parametersMethod = getMethodWithAnnotation(Parameters.class, klass);
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

    private FrameworkMethod getMethodWithAnnotation(Class<? extends Annotation> annotationClass, TestClass testClass) {
        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(annotationClass);
        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }
        }

        return null;
    }
}
