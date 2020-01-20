package net.sf.sevenzipjbinding.junit.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;

public class ArchiveFormatTest extends JUnitNativeTestBase<VoidContext> {
    @Test
    public void testAllOutArchiveFormats() {
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            try {
                SevenZip.openOutArchive(archiveFormat).close();
                assertTrue("Problem with archive format " + archiveFormat, archiveFormat.isOutArchiveSupported());
            } catch (Exception e) {
                assertFalse("Problem with archive format " + archiveFormat + ": " + e.getLocalizedMessage(),
                        archiveFormat.isOutArchiveSupported());
            }
        }
    }

    @Test
    public void testArchiveFormatsOutArchiveInfo() {
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            boolean outArchiveImplPresent = archiveFormat.getOutArchiveImplementation() != null;
            assertTrue(outArchiveImplPresent == archiveFormat.isOutArchiveSupported());
        }
    }
}
