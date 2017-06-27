package net.sf.sevenzipjbinding.junit.tools;

import static net.sf.sevenzipjbinding.junit.tools.TypeVariableResolver.resolveTypeVariable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sf.sevenzipjbinding.junit.tools.TypeVariableResolver.TypeVariableResolverException;

public final class TypeVariableResolverTest {
    // TODO Add support for interfaces

    @Test
    public void testSimple() throws TypeVariableResolverException {
        class A<T> {
        }
        class B extends A<String> {
        }

        Class<?> result = resolveTypeVariable(B.class, A.class, "T");
        assertEquals(String.class, result);
    }

    @Test
    public void testVarNotDefined() {
        class A<T> {
        }
        class B extends A<String> {
        }
        try {
            resolveTypeVariable(B.class, A.class, "A");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals(
                    "Type variable 'A' is not defined in the " + A.class.getName() + " class",
                    e.getMessage());
        }
    }

    @Test
    public void testClassDoesntExtend() {
        class A<T> {
        }
        try {
            resolveTypeVariable(String.class, A.class, "T");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals(
                    "The concreteClass (java.lang.String) should extend the definingClass (" + A.class.getName() + ")",
                    e.getMessage());
        }
    }

    @Test
    public void testSameClass() {
        class A<T> {
        }
        try {
            resolveTypeVariable(A.class, A.class, "T");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals("The concreteClass (" + A.class.getName() + ") should extend the definingClass ("
                    + A.class.getName() + ")", e.getMessage());
        }
    }

    @Test
    public void testNonGenericClasses() {
        class A {
        }
        class B extends A {
        }
        try {
            resolveTypeVariable(B.class, A.class, "T");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals("Type variable 'T' is not defined in the " + A.class.getName() + " class", e.getMessage());
        }
    }

    @Test
    public void testVariableWithoutValue() {
        class A<T> {
        }
        class B<T> extends A<T> {
        }
        try {
            resolveTypeVariable(B.class, A.class, "T");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals("Can't trace the type variable to the concrete type", e.getMessage());
        }
    }

    @Test
    public void testNonGenericChildClass() throws TypeVariableResolverException {
        class A<T> {
        }
        class B extends A<String> {
        }
        class C extends B {
        }

        Class<?> result = resolveTypeVariable(C.class, A.class, "T");
        assertEquals(String.class, result);
    }

    @Test
    public void testTwoTypeVarsInTwoLevelHierarchy() throws TypeVariableResolverException {
        class A<T1> {
        }
        class B<T2> extends A<String> {
        }
        class C extends B<Integer> {
        }

        Class<?> result = resolveTypeVariable(C.class, A.class, "T1");
        assertEquals(String.class, result);
    }

    @Test
    public void testTwoTypeVarsInTwoLevelHierarchy2() throws TypeVariableResolverException {
        class A<T1> {
        }
        class B<T2, T3> extends A<T3> {
        }
        class C extends B<Integer, String> {
        }

        Class<?> result = resolveTypeVariable(C.class, A.class, "T1");
        assertEquals(String.class, result);
    }

    @Test
    public void testComplex() throws TypeVariableResolverException {
        class A<T1, T2, T3> {
        }
        class B<T2, T3, T4> extends A<T3, T4, T2> {
        }
        class C<T5, T6> extends B<T6, String, T5> {
        }
        class D<T1> extends C<Integer, T1> {
        }
        class E extends D<Long> {
        }
        class F extends E {
        }
        class G<T1, T2, T3, T4, T5, T6> extends F {
        }
        class H extends G<Byte, Character, Float, Double, Boolean, Short> {
        }

        assertEquals(String.class, resolveTypeVariable(H.class, A.class, "T1"));
        assertEquals(Integer.class, resolveTypeVariable(H.class, A.class, "T2"));
        assertEquals(Long.class, resolveTypeVariable(H.class, A.class, "T3"));

        assertEquals(Long.class, resolveTypeVariable(H.class, B.class, "T2"));
        assertEquals(String.class, resolveTypeVariable(H.class, B.class, "T3"));
        assertEquals(Integer.class, resolveTypeVariable(H.class, B.class, "T4"));

        assertEquals(Integer.class, resolveTypeVariable(H.class, C.class, "T5"));
        assertEquals(Long.class, resolveTypeVariable(H.class, C.class, "T6"));

        assertEquals(Long.class, resolveTypeVariable(H.class, D.class, "T1"));

        assertEquals(Byte.class, resolveTypeVariable(H.class, G.class, "T1"));
        assertEquals(Character.class, resolveTypeVariable(H.class, G.class, "T2"));
        assertEquals(Float.class, resolveTypeVariable(H.class, G.class, "T3"));
        assertEquals(Double.class, resolveTypeVariable(H.class, G.class, "T4"));
        assertEquals(Boolean.class, resolveTypeVariable(H.class, G.class, "T5"));
        assertEquals(Short.class, resolveTypeVariable(H.class, G.class, "T6"));
    }
}
