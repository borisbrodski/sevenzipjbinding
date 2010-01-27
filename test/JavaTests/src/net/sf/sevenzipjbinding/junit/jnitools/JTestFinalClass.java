package net.sf.sevenzipjbinding.junit.jnitools;

public final class JTestFinalClass extends JTestAbstractClass {
    private int protectedVirtualLongMethodParameterI = -1;
    private int protectedVirtualStringMethodParameterI = -1;
    private int protectedVirtualVoidMethodParameterI = -1;

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
