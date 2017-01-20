package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertEquals;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.junit.DebugModeOnly;
import net.sf.sevenzipjbinding.junit.DebugModeOnlyTestRule;

import org.junit.Rule;
import org.junit.Test;

public class EnumTest extends JBindingToolsTestBase {
    private static native int getPropertyIndexSymLink();

    private static native int getPropertyIndexHardLink();

    private static native int getPropertyIndexCopyLink();

    @Rule
    public DebugModeOnlyTestRule skipDebugModeOnlyTestRule = new DebugModeOnlyTestRule();

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
