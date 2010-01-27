package net.sf.sevenzipjbinding.junit.jnitools;

public class Interface1Impl1 implements Interface1 {

    private int voidMethodParameterI = -1;
    private int stringMethodParameterI = -1;
    private int longMethodParameterI = -1;

    public long longMethod(int i) {
        longMethodParameterI = (1 + i);
        return 12000 + (1 + i);
    }

    public String stringMethod(int i) {
        stringMethodParameterI = (1 + i);
        return "Interface.I = " + (1 + i);
    }

    public void voidMethod(int i) {
        voidMethodParameterI = (1 + i);
    }

    public int getVoidMethodParameterI() {
        return voidMethodParameterI;
    }

    public int getStringMethodParameterI() {
        return stringMethodParameterI;
    }

    public int getLongMethodParameterI() {
        return longMethodParameterI;
    }
}
