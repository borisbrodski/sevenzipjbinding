package net.sf.sevenzipjbinding.junit.jnitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.sevenzipjbinding.junit.DebugModeOnly;
import net.sf.sevenzipjbinding.junit.DebugModeOnlyTestRule;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Rule;
import org.junit.Test;

public class JNIToolsTest extends JUnitNativeTestBase {
    private static final int TEST_REPEAT_COUNT = 100;
    private static final int THREAD_COUNT = 40;
    private static final int THREAD_TIMEOUT = 200 * TEST_REPEAT_COUNT;

    // TODO Test Object methods
    private native String nativeAbstractClass(JTestAbstractClass jTestAbstractClass);

    private native String nativeAbstractClassStatic();

    private native String nativeFinalClass(JTestAbstractClass jTestFinalClass);

    private native String nativeInterface1(Interface1 interface1Impl, int offset, boolean fromClass);

    private native JTestFinalClass nativeJTestFinalClassNewInstance();

    private native boolean abstractClassIsInstance(Object obj);

    private native boolean finalClassIsInstance(Object obj);

    private native boolean abstractClassIsAssignableFromInstanceOf(Class<?> clazz);

    private native boolean finalClassIsAssignableFromInstanceOf(Class<?> clazz);

    @Rule
    public DebugModeOnlyTestRule debugModeOnlyTestRule = new DebugModeOnlyTestRule();

    @Test
    @DebugModeOnly
    public void testAbstractClassStatic() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            new JTestFinalClass(); // Init static fields
            checkErrorMessage(nativeAbstractClassStatic());

            assertEquals(7, JTestAbstractClass.getPrivateStaticLongMethodParameterI());
            assertEquals(8, JTestAbstractClass.getPrivateStaticStringMethodParameterI());
            assertEquals(9, JTestAbstractClass.getPrivateStaticVoidMethodParameterI());

            assertEquals(15l, JTestAbstractClass.getPrivateStaticLongField());
            assertEquals("16", JTestAbstractClass.getPrivateStaticStringField());
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            JTestFinalClass jTestFinalClass = new JTestFinalClass();
            checkErrorMessage(nativeAbstractClass(jTestFinalClass));
            assertEquals(1, jTestFinalClass.getPrivateLongMethodParameterI());
            assertEquals(2, jTestFinalClass.getPrivateStringMethodParameterI());
            assertEquals(3, jTestFinalClass.getPrivateVoidMethodParameterI());
            assertEquals(4, jTestFinalClass.getPrivateFinalLongMethodParameterI());
            assertEquals(5, jTestFinalClass.getPrivateFinalStringMethodParameterI());
            assertEquals(6, jTestFinalClass.getPrivateFinalVoidMethodParameterI());
            assertEquals(10, jTestFinalClass.getProtectedVirtualLongMethodParameterI());
            assertEquals(11, jTestFinalClass.getProtectedVirtualStringMethodParameterI());
            assertEquals(12, jTestFinalClass.getProtectedVirtualVoidMethodParameterI());
            assertEquals("14", jTestFinalClass.getPrivateStringField());
            assertEquals(13l, jTestFinalClass.getPrivateLongField());
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testAbstractClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl1() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            Interface1Impl1 interface1Impl1 = new Interface1Impl1();
            nativeInterface1(interface1Impl1, -1, false);
            assertEquals(17, interface1Impl1.getLongMethodParameterI());
            assertEquals(18, interface1Impl1.getStringMethodParameterI());
            assertEquals(19, interface1Impl1.getVoidMethodParameterI());
        }
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl1Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testInterface1Impl1();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testInterface2Impl1() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            Interface2Impl1 interface2Impl = new Interface2Impl1();
            nativeInterface1(interface2Impl, -1, false);
            assertEquals(17, interface2Impl.getLongMethodParameterI());
            assertEquals(18, interface2Impl.getStringMethodParameterI());
            assertEquals(19, interface2Impl.getVoidMethodParameterI());
            assertEquals(20, interface2Impl.getVoidMethodFromInterface2());
        }
    }

    // TODO Fix it
    @Test
    @DebugModeOnly
    public void testInterface2Impl1Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testInterface2Impl1();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl1FromClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            Interface1Impl1 interface1Impl1 = new Interface1Impl1();
            nativeInterface1(interface1Impl1, -1, true);
            assertEquals(17, interface1Impl1.getLongMethodParameterI());
            assertEquals(18, interface1Impl1.getStringMethodParameterI());
            assertEquals(19, interface1Impl1.getVoidMethodParameterI());
        }
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl1FromClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testInterface1Impl1FromClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl12() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            Interface1Impl12 interface1Impl12 = new Interface1Impl12();
            nativeInterface1(interface1Impl12, -12, false);
            assertEquals(6, interface1Impl12.getLongMethodParameterI());
            assertEquals(17, interface1Impl12.getLongMethodParameterI2());
            assertEquals(7, interface1Impl12.getStringMethodParameterI());
            assertEquals(18, interface1Impl12.getStringMethodParameterI2());
            assertEquals(8, interface1Impl12.getVoidMethodParameterI());
            assertEquals(19, interface1Impl12.getVoidMethodParameterI2());
        }
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl12Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testInterface1Impl12();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl12FromClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            Interface1Impl12 interface1Impl12 = new Interface1Impl12();
            nativeInterface1(interface1Impl12, -12, true);
            assertEquals(6, interface1Impl12.getLongMethodParameterI());
            assertEquals(17, interface1Impl12.getLongMethodParameterI2());
            assertEquals(7, interface1Impl12.getStringMethodParameterI());
            assertEquals(18, interface1Impl12.getStringMethodParameterI2());
            assertEquals(8, interface1Impl12.getVoidMethodParameterI());
            assertEquals(19, interface1Impl12.getVoidMethodParameterI2());
        }
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl12FromClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testInterface1Impl12FromClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl2() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            Interface1Impl2 interface1Impl2 = new Interface1Impl2();
            nativeInterface1(interface1Impl2, -2, false);
            assertEquals(17, interface1Impl2.getLongMethodParameterI());
            assertEquals(18, interface1Impl2.getStringMethodParameterI());
            assertEquals(19, interface1Impl2.getVoidMethodParameterI());
        }
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl2Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testInterface1Impl2();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl2FromClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            Interface1Impl2 interface1Impl2 = new Interface1Impl2();
            nativeInterface1(interface1Impl2, -2, true);
            assertEquals(17, interface1Impl2.getLongMethodParameterI());
            assertEquals(18, interface1Impl2.getStringMethodParameterI());
            assertEquals(19, interface1Impl2.getVoidMethodParameterI());
        }
    }

    @Test
    @DebugModeOnly
    public void testInterface1Impl2FromClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testInterface1Impl2FromClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testJTestFinalClassNewInstance1() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            JTestFinalClass instance = nativeJTestFinalClassNewInstance();
            assertNotNull(instance);
            assertEquals(-1, instance.getProtectedVirtualLongMethodParameterI());
            assertEquals(-1, instance.getPrivateVoidMethodParameterI());
            assertEquals(-1, instance.getProtectedVirtualStringMethodParameterI());
        }
    }

    @Test
    @DebugModeOnly
    public void testJTestFinalClassNewInstance1Multithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testJTestFinalClassNewInstance1();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testFinalClassFields() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            JTestFinalClass jTestFinalClass = new JTestFinalClass();
            checkErrorMessage(nativeFinalClass(jTestFinalClass));
            assertEquals(null, jTestFinalClass.getPrivateClassField());

            assertNotNull(jTestFinalClass.getPrivateJTestFinalClassField());
            assertEquals(200, jTestFinalClass.getPrivateJTestFinalClassField().getId());

            assertNotNull(jTestFinalClass.getPrivateJTestAbstractClassField());
            assertEquals(300, ((JTestFinalClass) jTestFinalClass.getPrivateJTestAbstractClassField()).getId());
        }
    }

    @Test
    @DebugModeOnly
    public void testFinalClassFieldsMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testFinalClassFields();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    private void checkErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            fail(errorMessage);
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsInstanceAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(abstractClassIsInstance(getOtherJTestAbstractInstance()));
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsInstanceAbstractClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testAbstractClassIsInstanceAbstractClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsInstanceFinalClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(abstractClassIsInstance(new JTestFinalClass()));
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsInstanceFinalClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testAbstractClassIsInstanceFinalClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsInstanceString() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(abstractClassIsInstance(""));
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsInstanceStringMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testAbstractClassIsInstanceString();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsInstanceAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(finalClassIsInstance(getOtherJTestAbstractInstance()));
        }
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsInstanceAbstractClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testFinalClassIsInstanceAbstractClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsInstanceFinalClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(finalClassIsInstance(new JTestFinalClass()));
        }
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsInstanceFinalClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testFinalClassIsInstanceFinalClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsInstanceString() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(finalClassIsInstance(""));
        }
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsInstanceStringMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testFinalClassIsInstanceString();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(abstractClassIsAssignableFromInstanceOf(JTestAbstractClass.class));
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfAbstractClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testAbstractClassIsAssignableFromInstanceOfAbstractClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfFinalClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(abstractClassIsAssignableFromInstanceOf(JTestFinalClass.class));
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfFinalClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testAbstractClassIsAssignableFromInstanceOfFinalClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfString() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(abstractClassIsAssignableFromInstanceOf(String.class));
        }
    }

    @Test
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfStringMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testAbstractClassIsAssignableFromInstanceOfString();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(finalClassIsAssignableFromInstanceOf(JTestAbstractClass.class));
        }
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfAbstractClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testFinalClassIsAssignableFromInstanceOfAbstractClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfFinalClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(finalClassIsAssignableFromInstanceOf(JTestFinalClass.class));
        }
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfFinalClassMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testFinalClassIsAssignableFromInstanceOfFinalClass();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfString() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(finalClassIsAssignableFromInstanceOf(String.class));
        }
    }

    @Test
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfStringMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testFinalClassIsAssignableFromInstanceOfString();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    private JTestAbstractClass getOtherJTestAbstractInstance() {
        return new JTestAbstractClass() {
            @Override
            protected void protectedVirtualVoidMethod(int i) {
            }

            @Override
            protected String protectedVirtualStringMethod(int i) {
                return null;
            }

            @Override
            protected long protectedVirtualLongMethod(int i) {
                return 0;
            }
        };
    }
}
