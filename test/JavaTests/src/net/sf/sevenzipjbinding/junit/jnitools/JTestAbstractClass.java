package net.sf.sevenzipjbinding.junit.jnitools;

public abstract class JTestAbstractClass {
    private static int privateStaticLongMethodParameterI = -1;
    private static int privateStaticStringMethodParameterI = -1;
    private static int privateStaticVoidMethodParameterI = -1;
    private int privateLongMethodParameterI;
    private int privateStringMethodParameterI = -1;
    private int privateVoidMethodParameterI = -1;
    private int privateFinalStringMethodParameterI = -1;
    private int privateFinalVoidMethodParameterI = -1;
    private int privateFinalLongMethodParameterI = -1;

    private long privateLongField = -1;
    private String privateStringField = "-1";
    private static long privateStaticLongField = -1;
    private static String privateStaticStringField = "-1";

    JTestAbstractClass() {
        privateStaticLongMethodParameterI = -1;
        privateStaticStringMethodParameterI = -1;
        privateStaticVoidMethodParameterI = -1;
        privateStaticLongField = -1;
        privateStaticStringField = "-1";
    }

    @SuppressWarnings("unused")
    private long privateLongMethod(int i) {
        privateLongMethodParameterI = i;
        return i + 1000;
    }

    @SuppressWarnings("unused")
    private String privateStringMethod(int i) {
        privateStringMethodParameterI = i;
        return "I1 = " + i;
    }

    @SuppressWarnings("unused")
    private void privateVoidMethod(int i) {
        privateVoidMethodParameterI = i;
    }

    @SuppressWarnings("unused")
    private final long privateFinalLongMethod(int i) {
        privateFinalLongMethodParameterI = i;
        return i + 1000;
    }

    @SuppressWarnings("unused")
    private final String privateFinalStringMethod(int i) {
        privateFinalStringMethodParameterI = i;
        return "I2 = " + i;
    }

    @SuppressWarnings("unused")
    private final void privateFinalVoidMethod(int i) {
        privateFinalVoidMethodParameterI = i;
    }

    @SuppressWarnings("unused")
    private static String privateStaticStringMethod(int i) {
        privateStaticStringMethodParameterI = i;
        return "I3 = " + i;
    }

    @SuppressWarnings("unused")
    private static void privateStaticVoidMethod(int i) {
        privateStaticVoidMethodParameterI = i;
    }

    @SuppressWarnings("unused")
    private static long privateStaticLongMethod(int i) {
        privateStaticLongMethodParameterI = i;
        return i + 2000;
    }

    protected abstract long protectedVirtualLongMethod(int i);

    protected abstract String protectedVirtualStringMethod(int i);

    protected abstract void protectedVirtualVoidMethod(int i);

    public static int getPrivateStaticLongMethodParameterI() {
        return privateStaticLongMethodParameterI;
    }

    public int getPrivateLongMethodParameterI() {
        return privateLongMethodParameterI;
    }

    public static int getPrivateStaticStringMethodParameterI() {
        return privateStaticStringMethodParameterI;
    }

    public static int getPrivateStaticVoidMethodParameterI() {
        return privateStaticVoidMethodParameterI;
    }

    public int getPrivateStringMethodParameterI() {
        return privateStringMethodParameterI;
    }

    public int getPrivateVoidMethodParameterI() {
        return privateVoidMethodParameterI;
    }

    public int getPrivateFinalStringMethodParameterI() {
        return privateFinalStringMethodParameterI;
    }

    public int getPrivateFinalVoidMethodParameterI() {
        return privateFinalVoidMethodParameterI;
    }

    public int getPrivateFinalLongMethodParameterI() {
        return privateFinalLongMethodParameterI;
    }

    public long getPrivateLongField() {
        return privateLongField;
    }

    public String getPrivateStringField() {
        return privateStringField;
    }

    public static long getPrivateStaticLongField() {
        return privateStaticLongField;
    }

    public static String getPrivateStaticStringField() {
        return privateStaticStringField;
    }
}
