package net.sf.sevenzipjbinding.junit.tools;

import static net.sf.sevenzipjbinding.junit.tools.TypeVariableResolver.resolveTypeVariable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sf.sevenzipjbinding.junit.tools.TypeVariableResolver.TypeVariableResolverException;

public final class TypeVariableResolverTest {
    // TODO Add support for interfaces

    // Unfortunately, inner classes within the test method hit the java bug on ARM (armel and armhf).
    //   https://bugs.java.com/bugdatabase/view_bug.do?bug_id=2210448
    interface TestSimple {
        class A<T> {
        }

        class B extends A<String> {
        }
    }

    @Test
    public void testSimple() throws TypeVariableResolverException {
        // Unfortunately, inner classes within the test method hit the java bug on ARM (armel and armhf).
        //   https://bugs.java.com/bugdatabase/view_bug.do?bug_id=2210448
        //
        // class A<T> {
        // }
        // class B extends A<String> {
        // }
        Class<?> result = resolveTypeVariable(TestSimple.B.class, TestSimple.A.class, "T");
        assertEquals(String.class, result);
    }

    interface TestVarNotDefined {
        class A<T> {
        }

        class B extends A<String> {
        }
    }

    @Test
    public void testVarNotDefined() {
        try {
            resolveTypeVariable(TestVarNotDefined.B.class, TestVarNotDefined.A.class, "A");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals("Type variable 'A' is not defined in the " + TestVarNotDefined.A.class.getName() + " class",
                    e.getMessage());
        }
    }

    interface TestClassDoesntExtend {
        class A<T> {
        }
    }

    @Test
    public void testClassDoesntExtend() {
        try {
            resolveTypeVariable(String.class, TestClassDoesntExtend.A.class, "T");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals("The concreteClass (java.lang.String) should extend the definingClass ("
                    + TestClassDoesntExtend.A.class.getName() + ")", e.getMessage());
        }
    }

    interface TestSameClass {
        class A<T> {
        }
    }

    @Test
    public void testSameClass() {
        try {
            resolveTypeVariable(TestSameClass.A.class, TestSameClass.A.class, "T");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals("The concreteClass (" + TestSameClass.A.class.getName() + ") should extend the definingClass ("
                    + TestSameClass.A.class.getName() + ")", e.getMessage());
        }
    }

    interface TestNonGenericClasses {
        class A {
        }

        class B extends A {
        }
    }

    @Test
    public void testNonGenericClasses() {
        try {
            resolveTypeVariable(TestNonGenericClasses.B.class, TestNonGenericClasses.A.class, "T");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals("Type variable 'T' is not defined in the " + //
                    TestNonGenericClasses.A.class.getName() + " class", e.getMessage());
        }
    }

    interface TestVariableWithoutValue {
        class A<T> {
        }

        class B<T> extends A<T> {
        }
    }

    @Test
    public void testVariableWithoutValue() {
        try {
            resolveTypeVariable(TestVariableWithoutValue.B.class, TestVariableWithoutValue.A.class, "T");
            fail("Exception expected");
        } catch (TypeVariableResolverException e) {
            assertEquals("Can't trace the type variable to the concrete type", e.getMessage());
        }
    }

    interface TestNonGenericChildClass {
        class A<T> {
        }

        class B extends A<String> {
        }

        class C extends B {
        }
    }

    @Test
    public void testNonGenericChildClass() throws TypeVariableResolverException {
        Class<?> result = resolveTypeVariable(TestNonGenericChildClass.C.class, TestNonGenericChildClass.A.class, "T");
        assertEquals(String.class, result);
    }

    interface TestTwoTypeVarsInTwoLevelHierarchy {
        class A<T1> {
        }

        class B<T2> extends A<String> {
        }

        class C extends B<Integer> {
        }
    }

    @Test
    public void testTwoTypeVarsInTwoLevelHierarchy() throws TypeVariableResolverException {
        Class<?> result = resolveTypeVariable(TestTwoTypeVarsInTwoLevelHierarchy.C.class,
                TestTwoTypeVarsInTwoLevelHierarchy.A.class, "T1");
        assertEquals(String.class, result);
    }

    interface TestTwoTypeVarsInTwoLevelHierarchy2 {
        class A<T1> {
        }

        class B<T2, T3> extends A<T3> {
        }

        class C extends B<Integer, String> {
        }
    }

    @Test
    public void testTwoTypeVarsInTwoLevelHierarchy2() throws TypeVariableResolverException {
        Class<?> result = resolveTypeVariable(TestTwoTypeVarsInTwoLevelHierarchy2.C.class,
                TestTwoTypeVarsInTwoLevelHierarchy2.A.class, "T1");
        assertEquals(String.class, result);
    }

    interface TestComplex {
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
    }

    @Test
    public void testComplex() throws TypeVariableResolverException {
        assertEquals(String.class, resolveTypeVariable(TestComplex.H.class, TestComplex.A.class, "T1"));
        assertEquals(Integer.class, resolveTypeVariable(TestComplex.H.class, TestComplex.A.class, "T2"));
        assertEquals(Long.class, resolveTypeVariable(TestComplex.H.class, TestComplex.A.class, "T3"));

        assertEquals(Long.class, resolveTypeVariable(TestComplex.H.class, TestComplex.B.class, "T2"));
        assertEquals(String.class, resolveTypeVariable(TestComplex.H.class, TestComplex.B.class, "T3"));
        assertEquals(Integer.class, resolveTypeVariable(TestComplex.H.class, TestComplex.B.class, "T4"));

        assertEquals(Integer.class, resolveTypeVariable(TestComplex.H.class, TestComplex.C.class, "T5"));
        assertEquals(Long.class, resolveTypeVariable(TestComplex.H.class, TestComplex.C.class, "T6"));

        assertEquals(Long.class, resolveTypeVariable(TestComplex.H.class, TestComplex.D.class, "T1"));

        assertEquals(Byte.class, resolveTypeVariable(TestComplex.H.class, TestComplex.G.class, "T1"));
        assertEquals(Character.class, resolveTypeVariable(TestComplex.H.class, TestComplex.G.class, "T2"));
        assertEquals(Float.class, resolveTypeVariable(TestComplex.H.class, TestComplex.G.class, "T3"));
        assertEquals(Double.class, resolveTypeVariable(TestComplex.H.class, TestComplex.G.class, "T4"));
        assertEquals(Boolean.class, resolveTypeVariable(TestComplex.H.class, TestComplex.G.class, "T5"));
        assertEquals(Short.class, resolveTypeVariable(TestComplex.H.class, TestComplex.G.class, "T6"));
    }
}
