package net.sf.sevenzipjbinding.junit;

import junit.framework.Assert;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZip;

import org.junit.Ignore;
import org.junit.Test;

public class ArchiveFormatTest extends JUnitNativeTestBase {
    @Test
    @Ignore
    // TODO Check, this test. The outArchive should be at least closed afterwards.
    public void testForOutArchive() {
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            try {
                SevenZip.openOutArchive(archiveFormat);
                Assert.assertTrue("Problem with archive format " + archiveFormat, archiveFormat.isOutArchiveSupported());
            } catch (Exception e) {
                Assert.assertFalse("Problem with archive format " + archiveFormat,
                        archiveFormat.isOutArchiveSupported());
            }
        }
    }
}
