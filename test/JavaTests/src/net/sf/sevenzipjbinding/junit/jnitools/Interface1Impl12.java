package net.sf.sevenzipjbinding.junit.jnitools;

public class Interface1Impl12 extends Interface1Impl1 {

    private int voidMethodParameterI2 = -1;
    private int stringMethodParameterI2 = -1;
    private int longMethodParameterI2 = -1;

    @Override
    public long longMethod(int i) {
        super.longMethod(i);
        longMethodParameterI2 = (12 + i);
        return 12000 + (12 + i);
    }

    @Override
    public String stringMethod(int i) {
        super.stringMethod(i);
        stringMethodParameterI2 = (12 + i);
        return "Interface.I = " + (12 + i);
    }

    @Override
    public void voidMethod(int i) {
        super.voidMethod(i);
        voidMethodParameterI2 = (12 + i);
    }

    public int getVoidMethodParameterI2() {
        return voidMethodParameterI2;
    }

    public int getStringMethodParameterI2() {
        return stringMethodParameterI2;
    }

    public int getLongMethodParameterI2() {
        return longMethodParameterI2;
    }

}
