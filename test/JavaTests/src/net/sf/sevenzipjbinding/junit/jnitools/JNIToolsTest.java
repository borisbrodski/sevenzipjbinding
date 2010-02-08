package net.sf.sevenzipjbinding.junit.jnitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

public class JNIToolsTest extends JUnitNativeTestBase {
    private native String testAbstractClass(JTestAbstractClass jTestAbstractClass);

    private native String testFinalClass(JTestAbstractClass jTestFinalClass);

    private native String testInterface1(Interface1 interface1Impl, int offset, boolean fromClass);

    private native JTestFinalClass testJTestFinalClassNewInstance();

    private native boolean abstractClassIsInstance(Object obj);

    private native boolean finalClassIsInstance(Object obj);

    private native boolean abstractClassIsAssignableFromInstanceOf(Class<?> clazz);

    private native boolean finalClassIsAssignableFromInstanceOf(Class<?> clazz);

    @Test
    public void testAbstractClass() {
        JTestFinalClass jTestFinalClass = new JTestFinalClass();
        checkErrorMessage(testAbstractClass(jTestFinalClass));
        assertEquals(1, jTestFinalClass.getPrivateLongMethodParameterI());
        assertEquals(2, jTestFinalClass.getPrivateStringMethodParameterI());
        assertEquals(3, jTestFinalClass.getPrivateVoidMethodParameterI());
        assertEquals(4, jTestFinalClass.getPrivateFinalLongMethodParameterI());
        assertEquals(5, jTestFinalClass.getPrivateFinalStringMethodParameterI());
        assertEquals(6, jTestFinalClass.getPrivateFinalVoidMethodParameterI());
        assertEquals(7, JTestAbstractClass.getPrivateStaticLongMethodParameterI());
        assertEquals(8, JTestAbstractClass.getPrivateStaticStringMethodParameterI());
        assertEquals(9, JTestAbstractClass.getPrivateStaticVoidMethodParameterI());
        assertEquals(10, jTestFinalClass.getProtectedVirtualLongMethodParameterI());
        assertEquals(11, jTestFinalClass.getProtectedVirtualStringMethodParameterI());
        assertEquals(12, jTestFinalClass.getProtectedVirtualVoidMethodParameterI());
        assertEquals(13l, jTestFinalClass.getPrivateLongField());
        assertEquals("14", jTestFinalClass.getPrivateStringField());
        assertEquals(15l, JTestAbstractClass.getPrivateStaticLongField());
        assertEquals("16", JTestAbstractClass.getPrivateStaticStringField());
    }

    @Test
    public void testAbstractClassRepeated() {
        JTestFinalClass jTestFinalClass = new JTestFinalClass();
        checkErrorMessage(testAbstractClass(jTestFinalClass));
        assertEquals(1, jTestFinalClass.getPrivateLongMethodParameterI());
        assertEquals(2, jTestFinalClass.getPrivateStringMethodParameterI());
        assertEquals(3, jTestFinalClass.getPrivateVoidMethodParameterI());
        assertEquals(4, jTestFinalClass.getPrivateFinalLongMethodParameterI());
        assertEquals(5, jTestFinalClass.getPrivateFinalStringMethodParameterI());
        assertEquals(6, jTestFinalClass.getPrivateFinalVoidMethodParameterI());
        assertEquals(7, JTestAbstractClass.getPrivateStaticLongMethodParameterI());
        assertEquals(8, JTestAbstractClass.getPrivateStaticStringMethodParameterI());
        assertEquals(9, JTestAbstractClass.getPrivateStaticVoidMethodParameterI());
        assertEquals(10, jTestFinalClass.getProtectedVirtualLongMethodParameterI());
        assertEquals(11, jTestFinalClass.getProtectedVirtualStringMethodParameterI());
        assertEquals(12, jTestFinalClass.getProtectedVirtualVoidMethodParameterI());
        assertEquals(13l, jTestFinalClass.getPrivateLongField());
        assertEquals("14", jTestFinalClass.getPrivateStringField());
        assertEquals(15l, JTestAbstractClass.getPrivateStaticLongField());
        assertEquals("16", JTestAbstractClass.getPrivateStaticStringField());
    }

    @Test
    public void testInterface1Impl1() {
        Interface1Impl1 interface1Impl1 = new Interface1Impl1();
        testInterface1(interface1Impl1, -1, false);
        assertEquals(17, interface1Impl1.getLongMethodParameterI());
        assertEquals(18, interface1Impl1.getStringMethodParameterI());
        assertEquals(19, interface1Impl1.getVoidMethodParameterI());
    }

    @Test
    public void testInterface1Impl1FromClass() {
        Interface1Impl1 interface1Impl1 = new Interface1Impl1();
        testInterface1(interface1Impl1, -1, true);
        assertEquals(17, interface1Impl1.getLongMethodParameterI());
        assertEquals(18, interface1Impl1.getStringMethodParameterI());
        assertEquals(19, interface1Impl1.getVoidMethodParameterI());
    }

    @Test
    public void testInterface1Impl1Repeated() {
        Interface1Impl1 interface1Impl1 = new Interface1Impl1();
        testInterface1(interface1Impl1, -1, false);
        assertEquals(17, interface1Impl1.getLongMethodParameterI());
        assertEquals(18, interface1Impl1.getStringMethodParameterI());
        assertEquals(19, interface1Impl1.getVoidMethodParameterI());
    }

    @Test
    public void testInterface1Impl1RepeatedFromClass() {
        Interface1Impl1 interface1Impl1 = new Interface1Impl1();
        testInterface1(interface1Impl1, -1, true);
        assertEquals(17, interface1Impl1.getLongMethodParameterI());
        assertEquals(18, interface1Impl1.getStringMethodParameterI());
        assertEquals(19, interface1Impl1.getVoidMethodParameterI());
    }

    @Test
    public void testInterface1Impl12() {
        Interface1Impl12 interface1Impl12 = new Interface1Impl12();
        testInterface1(interface1Impl12, -12, false);
        assertEquals(6, interface1Impl12.getLongMethodParameterI());
        assertEquals(17, interface1Impl12.getLongMethodParameterI2());
        assertEquals(7, interface1Impl12.getStringMethodParameterI());
        assertEquals(18, interface1Impl12.getStringMethodParameterI2());
        assertEquals(8, interface1Impl12.getVoidMethodParameterI());
        assertEquals(19, interface1Impl12.getVoidMethodParameterI2());
    }

    @Test
    public void testInterface1Impl12FromClass() {
        Interface1Impl12 interface1Impl12 = new Interface1Impl12();
        testInterface1(interface1Impl12, -12, true);
        assertEquals(6, interface1Impl12.getLongMethodParameterI());
        assertEquals(17, interface1Impl12.getLongMethodParameterI2());
        assertEquals(7, interface1Impl12.getStringMethodParameterI());
        assertEquals(18, interface1Impl12.getStringMethodParameterI2());
        assertEquals(8, interface1Impl12.getVoidMethodParameterI());
        assertEquals(19, interface1Impl12.getVoidMethodParameterI2());
    }

    @Test
    public void testInterface1Impl12Repeated() {
        Interface1Impl12 interface1Impl12 = new Interface1Impl12();
        testInterface1(interface1Impl12, -12, false);
        assertEquals(6, interface1Impl12.getLongMethodParameterI());
        assertEquals(17, interface1Impl12.getLongMethodParameterI2());
        assertEquals(7, interface1Impl12.getStringMethodParameterI());
        assertEquals(18, interface1Impl12.getStringMethodParameterI2());
        assertEquals(8, interface1Impl12.getVoidMethodParameterI());
        assertEquals(19, interface1Impl12.getVoidMethodParameterI2());
    }

    @Test
    public void testInterface1Impl12RepeatedFromClass() {
        Interface1Impl12 interface1Impl12 = new Interface1Impl12();
        testInterface1(interface1Impl12, -12, true);
        assertEquals(6, interface1Impl12.getLongMethodParameterI());
        assertEquals(17, interface1Impl12.getLongMethodParameterI2());
        assertEquals(7, interface1Impl12.getStringMethodParameterI());
        assertEquals(18, interface1Impl12.getStringMethodParameterI2());
        assertEquals(8, interface1Impl12.getVoidMethodParameterI());
        assertEquals(19, interface1Impl12.getVoidMethodParameterI2());
    }

    @Test
    public void testInterface1Impl2() {
        Interface1Impl2 interface1Impl2 = new Interface1Impl2();
        testInterface1(interface1Impl2, -2, false);
        assertEquals(17, interface1Impl2.getLongMethodParameterI());
        assertEquals(18, interface1Impl2.getStringMethodParameterI());
        assertEquals(19, interface1Impl2.getVoidMethodParameterI());
    }

    @Test
    public void testInterface1Impl2FromClass() {
        Interface1Impl2 interface1Impl2 = new Interface1Impl2();
        testInterface1(interface1Impl2, -2, true);
        assertEquals(17, interface1Impl2.getLongMethodParameterI());
        assertEquals(18, interface1Impl2.getStringMethodParameterI());
        assertEquals(19, interface1Impl2.getVoidMethodParameterI());
    }

    @Test
    public void testInterface1Impl2Repeated() {
        Interface1Impl2 interface1Impl2 = new Interface1Impl2();
        testInterface1(interface1Impl2, -2, false);
        assertEquals(17, interface1Impl2.getLongMethodParameterI());
        assertEquals(18, interface1Impl2.getStringMethodParameterI());
        assertEquals(19, interface1Impl2.getVoidMethodParameterI());
    }

    @Test
    public void testInterface1Impl2RepeatedFromClass() {
        Interface1Impl2 interface1Impl2 = new Interface1Impl2();
        testInterface1(interface1Impl2, -2, true);
        assertEquals(17, interface1Impl2.getLongMethodParameterI());
        assertEquals(18, interface1Impl2.getStringMethodParameterI());
        assertEquals(19, interface1Impl2.getVoidMethodParameterI());
    }

    @Test
    public void testJTestFinalClassNewInstance1() {
        JTestFinalClass instance = testJTestFinalClassNewInstance();
        assertNotNull(instance);
        assertEquals(-1, instance.getProtectedVirtualLongMethodParameterI());
        assertEquals(-1, instance.getPrivateVoidMethodParameterI());
        assertEquals(-1, instance.getProtectedVirtualStringMethodParameterI());
    }

    @Test
    public void testJTestFinalClassNewInstance1Repeated() {
        JTestFinalClass instance = testJTestFinalClassNewInstance();
        assertNotNull(instance);
        assertEquals(-1, instance.getProtectedVirtualLongMethodParameterI());
        assertEquals(-1, instance.getPrivateVoidMethodParameterI());
        assertEquals(-1, instance.getProtectedVirtualStringMethodParameterI());
    }

    @Test
    public void testFinalClassFields() {
        JTestFinalClass jTestFinalClass = new JTestFinalClass();
        checkErrorMessage(testFinalClass(jTestFinalClass));
        assertEquals(null, jTestFinalClass.getPrivateClassField());

        assertNotNull(jTestFinalClass.getPrivateJTestFinalClassField());
        assertEquals(200, jTestFinalClass.getPrivateJTestFinalClassField().getId());

        assertNotNull(jTestFinalClass.getPrivateJTestAbstractClassField());
        assertEquals(300, ((JTestFinalClass) jTestFinalClass.getPrivateJTestAbstractClassField()).getId());
    }

    @Test
    public void testFinalClassFieldsRepeated() {
        JTestFinalClass jTestFinalClass = new JTestFinalClass();
        checkErrorMessage(testFinalClass(jTestFinalClass));
        assertEquals(null, jTestFinalClass.getPrivateClassField());

        assertNotNull(jTestFinalClass.getPrivateJTestFinalClassField());
        assertEquals(200, jTestFinalClass.getPrivateJTestFinalClassField().getId());

        assertNotNull(jTestFinalClass.getPrivateJTestAbstractClassField());
        assertEquals(300, ((JTestFinalClass) jTestFinalClass.getPrivateJTestAbstractClassField()).getId());
    }

    private void checkErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            fail(errorMessage);
        }
    }

    @Test
    public void testAbstractClassIsInstanceAbstractClass() {
        assertTrue(abstractClassIsInstance(getOtherJTestAbstractInstance()));
    }

    @Test
    public void testAbstractClassIsInstanceFinalClass() {
        assertTrue(abstractClassIsInstance(new JTestFinalClass()));
    }

    @Test
    public void testAbstractClassIsInstanceString() {
        assertFalse(abstractClassIsInstance(""));
    }

    @Test
    public void testFinalClassIsInstanceAbstractClass() {
        assertFalse(finalClassIsInstance(getOtherJTestAbstractInstance()));
    }

    @Test
    public void testFinalClassIsInstanceFinalClass() {
        assertTrue(finalClassIsInstance(new JTestFinalClass()));
    }

    @Test
    public void testFinalClassIsInstanceString() {
        assertFalse(finalClassIsInstance(""));
    }

    @Test
    public void testAbstractClassIsAssignableFromInstanceOfAbstractClass() {
        assertTrue(abstractClassIsAssignableFromInstanceOf(JTestAbstractClass.class));
    }

    @Test
    public void testAbstractClassIsAssignableFromInstanceOfFinalClass() {
        assertTrue(abstractClassIsAssignableFromInstanceOf(JTestFinalClass.class));
    }

    @Test
    public void testAbstractClassIsAssignableFromInstanceOfString() {
        assertFalse(abstractClassIsAssignableFromInstanceOf(String.class));
    }

    @Test
    public void testFinalClassIsAssignableFromInstanceOfAbstractClass() {
        assertFalse(finalClassIsAssignableFromInstanceOf(JTestAbstractClass.class));
    }

    @Test
    public void testFinalClassIsAssignableFromInstanceOfFinalClass() {
        assertTrue(finalClassIsAssignableFromInstanceOf(JTestFinalClass.class));
    }

    @Test
    public void testFinalClassIsAssignableFromInstanceOfString() {
        assertFalse(finalClassIsAssignableFromInstanceOf(String.class));
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
