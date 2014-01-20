package net.sf.sevenzipjbinding.junit;

import junit.framework.Assert;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZip;

import org.junit.Test;

public class ArchiveFormatTest extends JUnitNativeTestBase {
    @Test
    public void testForOutArchive() {
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            try {
                SevenZip.openOutArchive(archiveFormat).close();
                Assert.assertTrue("Problem with archive format " + archiveFormat, archiveFormat.isOutArchiveSupported());
            } catch (Exception e) {
                Assert.assertFalse("Problem with archive format " + archiveFormat,
                        archiveFormat.isOutArchiveSupported());
            }
        }
    }
}