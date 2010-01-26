package net.sf.sevenzipjbinding.junit.jnitools;

public final class JTestFinalClass extends JTestAbstractClass {
    private int protectedVirtualLongMethodParameterI;
    private int protectedVirtualStringMethodParameterI;
    private int protectedVirtualVoidMethodParameterI;

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
}
