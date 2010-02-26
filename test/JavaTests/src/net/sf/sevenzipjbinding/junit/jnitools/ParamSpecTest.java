package net.sf.sevenzipjbinding.junit.jnitools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

public class ParamSpecTest extends JUnitNativeTestBase {
    private static class MyClass {
        public int value;

        public MyClass(int value) {
            super();
            this.value = value;
        }

        @Override
        public String toString() {
            return "MyClass(" + value + ")";
        }
    }

    private static final Random RANDOM = new Random(0);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWithNoParameters();

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWithNoParameters();

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith1Parameter(long l1);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith1Parameter(long l1);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith2Parameters(int i1, String s2);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith2Parameters(int i1, String s2);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith3Parameters(boolean b1, MyClass m2, int i3);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith3Parameters(boolean b1, MyClass m2, int i3);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith4Parameters(MyClass m1, int i2, String s3, boolean b4);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith4Parameters(MyClass m1, int i2, String s3, boolean b4);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith5Parameters(boolean b1, String s2, long l3, int i4, MyClass m5);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith5Parameters(boolean b1, String s2, long l3, int i4,
            MyClass m5);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith6Parameters(int i1, MyClass m2, long l3, int i4, boolean b5,
            MyClass m6);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith6Parameters(int i1, MyClass m2, long l3, int i4, boolean b5,
            MyClass m6);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith7Parameters(String s1, int i2, long l3, MyClass m4, int i5,
            boolean b6, long l7);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith7Parameters(String s1, int i2, long l3, MyClass m4, int i5,
            boolean b6, long l7);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith8Parameters(String s1, int i2, long l3, long l4, int i5,
            boolean b6, boolean b7, int i8);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith8Parameters(String s1, int i2, long l3, long l4, int i5,
            boolean b6, boolean b7, int i8);

    @SuppressWarnings("unused")
    private static native String nativeStringMethodWith9Parameters(MyClass m1, String s2, int i3, MyClass m4,
            boolean b5, int i6, String s7, long l8, boolean b9);

    @SuppressWarnings("unused")
    private static native List<String> nativeListMethodWith9Parameters(MyClass m1, String s2, int i3, MyClass m4,
            boolean b5, int i6, String s7, long l8, boolean b9);

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWithNoParameters() {
        return "stringMethodWithNoParameters()";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWithNoParameters() {
        return toList("listMethodWithNoParameters()");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith1Parameter(long l1) {
        return "stringMethodWith1Parameter(" + l1 + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith1Parameter(long l1) {
        return toList("listMethodWith1Parameter(" + l1 + ")");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith2Parameters(int i1, String s2) {
        return "stringMethodWith2Parameters(" + i1 + "," + s2 + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith2Parameters(int i1, String s2) {
        return toList("listMethodWith2Parameters(" + i1 + "," + s2 + ")");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith3Parameters(boolean b1, MyClass m2, int i3) {
        return "stringMethodWith3Parameters(" + b1 + "," + m2 + "," + i3 + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith3Parameters(boolean b1, MyClass m2, int i3) {
        return toList("listMethodWith3Parameters(" + b1 + "," + m2 + "," + i3 + ")");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith4Parameters(MyClass m1, int i2, String s3, boolean b4) {
        return "stringMethodWith4Parameters(" + m1 + "," + i2 + "," + s3 + "," + b4 + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith4Parameters(MyClass m1, int i2, String s3, boolean b4) {
        return toList("listMethodWith4Parameters(" + m1 + "," + i2 + "," + s3 + "," + b4 + ")");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith5Parameters(boolean b1, String s2, long l3, int i4, MyClass m5) {
        return "stringMethodWith5Parameters(" + b1 + "," + s2 + "," + l3 + "," + i4 + "," + m5 + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith5Parameters(boolean b1, String s2, long l3, int i4, MyClass m5) {
        return toList("listMethodWith5Parameters(" + b1 + "," + s2 + "," + l3 + "," + i4 + "," + m5 + ")");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith6Parameters(int i1, MyClass m2, long l3, int i4, boolean b5, MyClass m6) {
        return "stringMethodWith6Parameters(" + i1 + "," + m2 + "," + l3 + "," + i4 + "," + b5 + "," + m6 + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith6Parameters(int i1, MyClass m2, long l3, int i4, boolean b5, MyClass m6) {
        return toList("listMethodWith6Parameters(" + i1 + "," + m2 + "," + l3 + "," + i4 + "," + b5 + "," + m6 + ")");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith7Parameters(String s1, int i2, long l3, MyClass m4, int i5, boolean b6,
            long l7) {
        return "stringMethodWith7Parameters(" + s1 + "," + i2 + "," + l3 + "," + m4 + "," + i5 + "," + b6 + "," + l7
                + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith7Parameters(String s1, int i2, long l3, MyClass m4, int i5, boolean b6,
            long l7) {
        return toList("listMethodWith7Parameters(" + s1 + "," + i2 + "," + l3 + "," + m4 + "," + i5 + "," + b6 + ","
                + l7 + ")");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith8Parameters(String s1, int i2, long l3, long l4, int i5, boolean b6,
            boolean b7, int i8) {
        return "stringMethodWith8Parameters(" + s1 + "," + i2 + "," + l3 + "," + l4 + "," + i5 + "," + b6 + "," + b7
                + "," + i8 + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith8Parameters(String s1, int i2, long l3, long l4, int i5, boolean b6,
            boolean b7, int i8) {
        return toList("listMethodWith8Parameters(" + s1 + "," + i2 + "," + l3 + "," + l4 + "," + i5 + "," + b6 + ","
                + b7 + "," + i8 + ")");
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static String stringMethodWith9Parameters(MyClass m1, String s2, int i3, MyClass m4, boolean b5, int i6,
            String s7, long l8, boolean b9) {
        return "stringMethodWith9Parameters(" + m1 + "," + s2 + "," + i3 + "," + m4 + "," + b5 + "," + i6 + "," + s7
                + "," + l8 + "," + b9 + ")";
    }

    // Will be called from native code
    @SuppressWarnings("unused")
    private static List<String> listMethodWith9Parameters(MyClass m1, String s2, int i3, MyClass m4, boolean b5,
            int i6, String s7, long l8, boolean b9) {
        return toList("listMethodWith9Parameters(" + m1 + "," + s2 + "," + i3 + "," + m4 + "," + b5 + "," + i6 + ","
                + s7 + "," + l8 + "," + b9 + ")");
    }

    private static List<String> toList(String string) {
        List<String> list = new ArrayList<String>();
        list.add(string);
        return list;
    }

    @Test
    public void testStringMethodWithNoParameters() throws Exception {
        testMethod("StringMethodWithNoParameters");
    }

    @Test
    public void testListMethodWithNoParameters() throws Exception {
        testMethod("ListMethodWithNoParameters");
    }

    @Test
    public void testStringMethodWith1Parameter1() throws Exception {
        testMethod("StringMethodWith1Parameter");
    }

    @Test
    public void testStringMethodWith1Parameter2() throws Exception {
        testMethod("StringMethodWith1Parameter");
    }

    @Test
    public void testListMethodWith1Parameter1() throws Exception {
        testMethod("ListMethodWith1Parameter");
    }

    @Test
    public void testListMethodWith1Parameter2() throws Exception {
        testMethod("ListMethodWith1Parameter");
    }

    @Test
    public void testStringMethodWith2Parameters1() throws Exception {
        testMethod("StringMethodWith2Parameters");
    }

    @Test
    public void testStringMethodWith2Parameters2() throws Exception {
        testMethod("StringMethodWith2Parameters");
    }

    @Test
    public void testListMethodWith2Parameters1() throws Exception {
        testMethod("ListMethodWith2Parameters");
    }

    @Test
    public void testListMethodWith2Parameters2() throws Exception {
        testMethod("ListMethodWith2Parameters");
    }

    @Test
    public void testStringMethodWith3Parameters1() throws Exception {
        testMethod("StringMethodWith3Parameters");
    }

    @Test
    public void testStringMethodWith3Parameters2() throws Exception {
        testMethod("StringMethodWith3Parameters");
    }

    @Test
    public void testListMethodWith3Parameters1() throws Exception {
        testMethod("ListMethodWith3Parameters");
    }

    @Test
    public void testListMethodWith3Parameters2() throws Exception {
        testMethod("ListMethodWith3Parameters");
    }

    @Test
    public void testStringMethodWith4Parameters1() throws Exception {
        testMethod("StringMethodWith4Parameters");
    }

    @Test
    public void testStringMethodWith4Parameters2() throws Exception {
        testMethod("StringMethodWith4Parameters");
    }

    @Test
    public void testListMethodWith4Parameters1() throws Exception {
        testMethod("ListMethodWith4Parameters");
    }

    @Test
    public void testListMethodWith4Parameters2() throws Exception {
        testMethod("ListMethodWith4Parameters");
    }

    @Test
    public void testStringMethodWith5Parameters1() throws Exception {
        testMethod("StringMethodWith5Parameters");
    }

    @Test
    public void testStringMethodWith5Parameters2() throws Exception {
        testMethod("StringMethodWith5Parameters");
    }

    @Test
    public void testListMethodWith5Parameters1() throws Exception {
        testMethod("ListMethodWith5Parameters");
    }

    @Test
    public void testListMethodWith5Parameters2() throws Exception {
        testMethod("ListMethodWith5Parameters");
    }

    @Test
    public void testStringMethodWith6Parameters1() throws Exception {
        testMethod("StringMethodWith6Parameters");
    }

    @Test
    public void testStringMethodWith6Parameters2() throws Exception {
        testMethod("StringMethodWith6Parameters");
    }

    @Test
    public void testListMethodWith6Parameters1() throws Exception {
        testMethod("ListMethodWith6Parameters");
    }

    @Test
    public void testListMethodWith6Parameters2() throws Exception {
        testMethod("ListMethodWith6Parameters");
    }

    @Test
    public void testStringMethodWith7Parameters1() throws Exception {
        testMethod("StringMethodWith7Parameters");
    }

    @Test
    public void testStringMethodWith7Parameters2() throws Exception {
        testMethod("StringMethodWith7Parameters");
    }

    @Test
    public void testListMethodWith7Parameters1() throws Exception {
        testMethod("ListMethodWith7Parameters");
    }

    @Test
    public void testListMethodWith7Parameters2() throws Exception {
        testMethod("ListMethodWith7Parameters");
    }

    @Test
    public void testStringMethodWith8Parameters1() throws Exception {
        testMethod("StringMethodWith8Parameters");
    }

    @Test
    public void testStringMethodWith8Parameters2() throws Exception {
        testMethod("StringMethodWith8Parameters");
    }

    @Test
    public void testListMethodWith8Parameters1() throws Exception {
        testMethod("ListMethodWith8Parameters");
    }

    @Test
    public void testListMethodWith8Parameters2() throws Exception {
        testMethod("ListMethodWith8Parameters");
    }

    @Test
    public void testStringMethodWith9Parameters1() throws Exception {
        testMethod("StringMethodWith9Parameters");
    }

    @Test
    public void testStringMethodWith9Parameters2() throws Exception {
        testMethod("StringMethodWith9Parameters");
    }

    @Test
    public void testListMethodWith9Parameters1() throws Exception {
        testMethod("ListMethodWith9Parameters");
    }

    @Test
    public void testListMethodWith9Parameters2() throws Exception {
        testMethod("ListMethodWith9Parameters");
    }

    @SuppressWarnings("unchecked")
    private void testMethod(String name) throws Exception {
        Method nativeMethod = null;
        Method javaMethod = null;
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.getName().equals("native" + name)) {
                nativeMethod = method;
            } else if (method.getName().equalsIgnoreCase(name)) {
                javaMethod = method;
            }
        }
        nativeMethod.setAccessible(true);
        javaMethod.setAccessible(true);

        assertNotNull(nativeMethod);
        assertNotNull(javaMethod);
        assertArrayEquals(nativeMethod.getParameterTypes(), javaMethod.getParameterTypes());

        Object[] parameter = new Object[nativeMethod.getParameterTypes().length];
        int i = 0;
        for (Class<?> parameterClass : nativeMethod.getParameterTypes()) {
            if (parameterClass == int.class) {
                parameter[i] = RANDOM.nextInt();
            } else if (parameterClass == long.class) {
                parameter[i] = RANDOM.nextLong();
            } else if (parameterClass == boolean.class) {
                parameter[i] = RANDOM.nextBoolean();
            } else if (parameterClass == String.class) {
                parameter[i] = nextRandomString();
            } else if (parameterClass == MyClass.class) {
                parameter[i] = new MyClass(RANDOM.nextInt());
            } else {
                fail();
            }
            i++;
        }

        Object nativeResult = nativeMethod.invoke(this, parameter);
        Object javaResult = javaMethod.invoke(this, parameter);
        if (nativeMethod.getReturnType() == String.class) {
            assertEquals(javaResult, nativeResult);
        } else {
            myAssertEquals((List) javaResult, (List) nativeResult);
        }
    }

    private String nextRandomString() {
        StringBuilder stringBuilder = new StringBuilder();
        int n = RANDOM.nextInt(10);
        for (int i = 0; i < n; i++) {
            stringBuilder.append((char) (RANDOM.nextInt(100) + (byte) ' '));
        }
        return stringBuilder.toString();
    }

    private static void myAssertEquals(List<String> expected, List<String> actual) {
        assertEquals(1, expected.size());
        assertEquals(1, actual.size());
        assertEquals(expected.get(0), actual.get(0));
    }
}
