package net.sf.sevenzipjbinding.junit.common;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.impl.InArchiveImpl;
import net.sf.sevenzipjbinding.impl.OutArchiveImpl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests, that all 7-Zip-JBinding native methods declare throws {@link SevenZipException}.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public class DeclareThrowsSevenZipExceptionTest {
    private static final Class<?>[] classesToTest = new Class<?>[] { SevenZip.class, InArchiveImpl.class,
            OutArchiveImpl.class };

    @Test
    public void testDeclareThrowsSevenZipException() {
        for (Class<?> classToTest : classesToTest) {
            methodLoop: for (Method method : classToTest.getDeclaredMethods()) {
                if (method.getName().startsWith("nativeGetVersion")) {
                    // nativeGetVersionXxx methods are written without usage of the JBindingContext
                    // and can't throw SevenZipExceptions.
                    continue;
                }
                if (Modifier.isNative(method.getModifiers())) {
                    for (Class<?> exceptionClass : method.getExceptionTypes()) {
                        if (exceptionClass == SevenZipException.class
                                || exceptionClass == SevenZipNativeInitializationException.class) {
                            continue methodLoop;
                        }
                    }
                    Assert.fail("Native method doesn't declare throws SevenZipException: "
                            + classToTest.getCanonicalName() + "." + method.getName() + "(...)");
                }
            }
        }
    }

}
