package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sf.sevenzipjbinding.NFileTimeType;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.DebugModeOnly;

public class EnumTest extends JUnitNativeTestBase<VoidContext> {
    private static native int getPropertyIndexSymLink();

    private static native int getPropertyIndexHardLink();

    private static native int getPropertyIndexCopyLink();

    private static native int getPropertyIndexArcFileName();

    private static native int getPropertyIndexUserId();

    private static native int getPropertyIndexDevMinor();

    private static native int getPropertyIndexNumDefined();

    private static native int getFileTimeTypeDos();

    private static native int getFileTimeType1ns();

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

    @Test
    @DebugModeOnly
    public void checkEnumArcFileName() {
        assertEquals(PropID.ARC_FILE_NAME.ordinal(), getPropertyIndexArcFileName());
    }

    @Test
    @DebugModeOnly
    public void checkEnumUserId() {
        assertEquals(PropID.USER_ID.ordinal(), getPropertyIndexUserId());
    }

    @Test
    @DebugModeOnly
    public void checkEnumDevMinor() {
        assertEquals(PropID.DEV_MINOR.ordinal(), getPropertyIndexDevMinor());
    }

    @Test
    @DebugModeOnly
    public void checkEnumNumDefined() {
        // Every engine property id below this one has a Java constant with the same index.
        assertEquals(PropID.NUM_DEFINED.ordinal(), getPropertyIndexNumDefined());
    }

    @Test
    @DebugModeOnly
    public void checkEnumFileTimeType() {
        assertEquals(NFileTimeType.DOS.ordinal(), getFileTimeTypeDos());
        assertEquals(NFileTimeType.NANOSECONDS.ordinal(), getFileTimeType1ns());
    }
}
