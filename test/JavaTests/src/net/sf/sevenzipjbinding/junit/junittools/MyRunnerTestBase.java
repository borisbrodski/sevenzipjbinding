package net.sf.sevenzipjbinding.junit.junittools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(MyRunner.class)
public class MyRunnerTestBase {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface ClassCheckMethod {
    }

    private static int sequence = 1;

    private static int beforeClassCallNumber;
    private static int afterClassCallNumber;

    private static List<Class<?>> classesToCheck = new ArrayList<Class<?>>();
    private static List<Object> instancesToCheck = new ArrayList<Object>();

    private int beforeCallNumber;
    private int afterCallNumber;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                if (afterClassCallNumber == 0) {
                    RuntimeException runtimeException = new RuntimeException(
                            "@AfterClass method of MyRunnerTestBase wasn't called.");
                    runtimeException.printStackTrace();
                    throw runtimeException;
                }
            }
        }));
    }

    @BeforeClass
    public static void beforeBaseClass() {
        beforeClassCallNumber = nextNumber();
    }

    @AfterClass
    public static void afterBaseClass() throws Exception {
        afterClassCallNumber = nextNumber();
        runInstanceChecks();
        runClassChecks();

    }

    protected static void baseClassCheck(int maxNumber) {
        assertEquals(1, beforeClassCallNumber);

    }

    @Before
    public void baseBefore() {
        beforeCallNumber = nextNumber();
    }

    @After
    public void baseAfter() {
        afterCallNumber = nextNumber();
    }

    protected void checkBeforeAndAfter(int testCallNumber) {
        assertTrue(beforeCallNumber > 0);
        assertTrue(beforeCallNumber < testCallNumber);
        assertTrue(afterCallNumber > testCallNumber);
    }

    protected synchronized static int nextNumber() {
        return sequence++;
    }

    protected synchronized static void addClassToCheck(Class<?> clazz) {
        classesToCheck.add(clazz);
    }

    protected synchronized void addInstanceToCheck(Object object) {
        instancesToCheck.add(object);
    }

    private static void runClassChecks() throws Exception {
        for (Class<?> clazz : classesToCheck) {
            Class<?> superClass = clazz;
            while (superClass != Object.class) {
                for (Method method : clazz.getMethods()) {
                    if (Modifier.isStatic(method.getModifiers())
                            && method.getAnnotation(ClassCheckMethod.class) != null) {
                        method.setAccessible(true);
                        method.invoke(null);
                    }
                }
                superClass = superClass.getSuperclass();
            }
        }
    }

    private static void runInstanceChecks() throws Exception {
        for (Object object : instancesToCheck) {
            Class<?> clazz = object.getClass();
            Class<?> superClass = clazz;
            while (superClass != Object.class) {
                for (Method method : clazz.getMethods()) {
                    if (!Modifier.isStatic(method.getModifiers())
                            && method.getAnnotation(ClassCheckMethod.class) != null) {
                        method.setAccessible(true);
                        method.invoke(object);
                    }
                }
                superClass = superClass.getSuperclass();
            }
        }
    }
}
