package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;

import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.junittools.annotations.DebugModeOnly;
import net.sf.sevenzipjbinding.junit.junittools.rules.DebugModeOnlyTestRule;

public class CHeadCacheInStreamTest extends JUnitNativeTestBase {
    @Rule
    public DebugModeOnlyTestRule debugModeOnlyTestRule = new DebugModeOnlyTestRule();

    private native String nativeSimpleTests();

    @Test
    @DebugModeOnly
    public void simpleTests() {
        String errors = nativeSimpleTests();
        assertNull(errors, errors);
    }
}


