package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.DebugModeOnly;

public class EnumTest extends JUnitNativeTestBase<VoidContext> {
    private static native int getPropertyIndexSymLink();

    private static native int getPropertyIndexHardLink();

    private static native int getPropertyIndexCopyLink();

    @Test
    @DebugModeOnly
    public void checkEnumSymLink() {
        assertEquals(PropID.SYM_LINK.ordinal(), getPropertyIndexSymLink());
    }

    @Test
    @DebugModeOnly
    public void checkEnumHardLink() {
        assertEquals(PropID.HARD_LINK.ordinal(), getPropertyIndexHardLink());
    }

    @Test
    @DebugModeOnly
    public void checkEnumCopyLink() {
        assertEquals(PropID.COPY_LINK.ordinal(), getPropertyIndexCopyLink());
    }
}
