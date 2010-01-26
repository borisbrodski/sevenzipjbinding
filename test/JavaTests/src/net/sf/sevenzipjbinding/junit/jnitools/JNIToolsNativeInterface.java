package net.sf.sevenzipjbinding.junit.jnitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

public class JNIToolsNativeInterface extends JUnitNativeTestBase {
    public native String testAbstractClass(JTestAbstractClass jTestAbstractClass);

    @Test
    public void test() {
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

        jTestFinalClass = new JTestFinalClass();
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
    }

    private void checkErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            fail(errorMessage);
        }
    }
}
