package net.sf.sevenzipjbinding.junit.jnitools;

public class Interface2Impl1 extends Interface1Impl1 implements Interface2 {

    private int voidMethodFromInterface2 = -1;

    public int getVoidMethodFromInterface2() {
        return voidMethodFromInterface2;
    }

    public void voidMethodFromInterface2(int i) {
        voidMethodFromInterface2 = (1 + i);
    }
}
