package net.sf.sevenzipjbinding.junit.jnitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.DebugModeOnly;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

public class JNIToolsTest extends JUnitNativeTestBase<VoidContext> {
    private static final int TEST_REPEAT_COUNT = 100;
    private static final int THREAD_COUNT = 40;
    private static final int THREAD_TIMEOUT = 200 * TEST_REPEAT_COUNT;

    // TODO Test Object methods
    private native String nativeAbstractClass(JTestAbstractClass jTestAbstractClass);

    private native String nativeAbstractClassStatic();

    private native String nativeFinalClass(JTestAbstractClass jTestFinalClass);

    private native String nativeInterface1(Interface1 interface1Impl, int offset, boolean fromClass);

    private native boolean nativeInterfaceIsInstance(Object object);

    private native JTestFinalClass nativeJTestFinalClassNewInstance();

    private native boolean abstractClassIsInstance(Object obj);

    private native boolean finalClassIsInstance(Object obj);

    private native boolean abstractClassIsAssignableFromInstanceOf(Class<?> clazz);

    private native boolean finalClassIsAssignableFromInstanceOf(Class<?> clazz);

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
    @Multithreaded
    @Repeat
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
    @Multithreaded
    @Repeat
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
    public void testInterfaceInstanceOf() {
        assertFalse(nativeInterfaceIsInstance("test"));
        assertTrue(nativeInterfaceIsInstance(new Interface2Impl1()));
        assertFalse(nativeInterfaceIsInstance(null));
    }

    @Test
    @Multithreaded
    @Repeat
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

    @Test
    @Multithreaded
    @Repeat
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
    @Multithreaded
    @Repeat
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
    @Multithreaded
    @Repeat
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
    @Multithreaded
    @Repeat
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
    @Multithreaded
    @Repeat
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
    @Multithreaded
    @Repeat
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
    @Multithreaded
    @Repeat
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

    private void checkErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            fail(errorMessage);
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testAbstractClassIsInstanceAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(abstractClassIsInstance(getOtherJTestAbstractInstance()));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testAbstractClassIsInstanceFinalClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(abstractClassIsInstance(new JTestFinalClass()));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testAbstractClassIsInstanceString() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(abstractClassIsInstance(""));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testFinalClassIsInstanceAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(finalClassIsInstance(getOtherJTestAbstractInstance()));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testFinalClassIsInstanceFinalClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(finalClassIsInstance(new JTestFinalClass()));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testFinalClassIsInstanceString() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(finalClassIsInstance(""));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(abstractClassIsAssignableFromInstanceOf(JTestAbstractClass.class));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfFinalClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(abstractClassIsAssignableFromInstanceOf(JTestFinalClass.class));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testAbstractClassIsAssignableFromInstanceOfString() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(abstractClassIsAssignableFromInstanceOf(String.class));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfAbstractClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(finalClassIsAssignableFromInstanceOf(JTestAbstractClass.class));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfFinalClass() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertTrue(finalClassIsAssignableFromInstanceOf(JTestFinalClass.class));
        }
    }

    @Test
    @Multithreaded
    @Repeat
    @DebugModeOnly
    public void testFinalClassIsAssignableFromInstanceOfString() {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            assertFalse(finalClassIsAssignableFromInstanceOf(String.class));
        }
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
