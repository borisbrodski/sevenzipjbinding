package net.sf.sevenzipjbinding.junit.tools;

public class SevenZipDebug {
    private native int nativeGetObjectCount();

    private native void nativePrintObjects();

    public static int getCPPObjectCount() {
        return new SevenZipDebug().nativeGetObjectCount();
    }

    public static void printCPPObjects() {
        new SevenZipDebug().nativePrintObjects();
    }
}
