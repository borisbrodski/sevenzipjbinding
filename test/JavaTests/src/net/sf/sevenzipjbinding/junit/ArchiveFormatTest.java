package net.sf.sevenzipjbinding.junit;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZip;

import org.junit.Test;

public class ArchiveFormatTest extends JUnitNativeTestBase {
    @Test
    public void testAllOutArchiveFormats() {
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            try {
                SevenZip.openOutArchive(archiveFormat).close();
                assertTrue("Problem with archive format " + archiveFormat, archiveFormat.isOutArchiveSupported());
            } catch (Exception e) {
                assertFalse("Problem with archive format " + archiveFormat,
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
