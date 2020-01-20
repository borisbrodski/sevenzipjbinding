package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.DebugModeOnly;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

public class CHeadCacheInStreamTest extends JUnitNativeTestBase<VoidContext> {
    private native String nativeSimpleTests(boolean printLog);

    @Test
    @Repeat
    @DebugModeOnly
    public void simpleTests() {
        String errors = nativeSimpleTests(TestConfiguration.getCurrent().isTrace());
        assertNull(errors, errors);
    }
}


