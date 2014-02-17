package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
