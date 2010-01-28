package net.sf.sevenzipjbinding.junit.jnitools;

public final class JTestFinalClass extends JTestAbstractClass {
    private int protectedVirtualLongMethodParameterI = -1;
    private int protectedVirtualStringMethodParameterI = -1;
    private int protectedVirtualVoidMethodParameterI = -1;

    private Class<?> privateClassField = StringBuffer.class;

    private JTestFinalClass privateJTestFinalClassField = null;
    private JTestAbstractClass privateJTestAbstractClassField = null;

    private static JTestFinalClass privateStaticJTestFinalClassField = null;
    private static JTestAbstractClass privateStaticJTestAbstractClassField = null;

    private long id;

    @Override
    protected long protectedVirtualLongMethod(int i) {
        protectedVirtualLongMethodParameterI = i;
        return i + 3000;
    }

    @Override
    protected String protectedVirtualStringMethod(int i) {
        protectedVirtualStringMethodParameterI = i;
        return "I4 = " + i;
    }

    @Override
    protected void protectedVirtualVoidMethod(int i) {
        protectedVirtualVoidMethodParameterI = i;
    }

    public int getProtectedVirtualLongMethodParameterI() {
        return protectedVirtualLongMethodParameterI;
    }

    public int getProtectedVirtualStringMethodParameterI() {
        return protectedVirtualStringMethodParameterI;
    }

    public int getProtectedVirtualVoidMethodParameterI() {
        return protectedVirtualVoidMethodParameterI;
    }

    public Class<?> getPrivateClassField() {
        return privateClassField;
    }

    public JTestAbstractClass getPrivateJTestAbstractClassField() {
        return privateJTestAbstractClassField;
    }

    public JTestFinalClass getPrivateJTestFinalClassField() {
        return privateJTestFinalClassField;
    }

    public static JTestFinalClass getPrivateStaticJTestFinalClassField() {
        return privateStaticJTestFinalClassField;
    }

    public static JTestAbstractClass getPrivateStaticJTestAbstractClassField() {
        return privateStaticJTestAbstractClassField;
    }

    public long getId() {
        return id;
    }
}
