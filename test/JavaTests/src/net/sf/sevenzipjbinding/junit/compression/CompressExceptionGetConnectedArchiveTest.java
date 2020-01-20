package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class CompressExceptionGetConnectedArchiveTest extends JUnitNativeTestBase<VoidContext> {

    @Test
    public void testGetConnectedOutArchive7z() throws Exception {
        IInArchive inArchive = getInArchive(ArchiveFormat.TAR);

        try {
            inArchive.getConnectedOutArchive7z();
            fail("SevenZipException expected");
        } catch (SevenZipException e) {
            assertEquals(
                    "Archive format specific update API for 7z-archives can't work with the currently opened Tar-archive",
                    e.getMessage());
        }

    }

    @Test
    public void testGetConnectedOutArchiveZip() throws Exception {
        IInArchive inArchive = getInArchive(ArchiveFormat.SEVEN_ZIP);

        try {
            inArchive.getConnectedOutArchiveZip();
            fail("SevenZipException expected");
        } catch (SevenZipException e) {
            assertEquals(
                    "Archive format specific update API for Zip-archives can't work with the currently opened 7z-archive",
                    e.getMessage());
        }

    }

    @Test
    public void testGetConnectedOutArchiveTar() throws Exception {
        IInArchive inArchive = getInArchive(ArchiveFormat.SEVEN_ZIP);

        try {
            inArchive.getConnectedOutArchiveTar();
            fail("SevenZipException expected");
        } catch (SevenZipException e) {
            assertEquals(
                    "Archive format specific update API for Tar-archives can't work with the currently opened 7z-archive",
                    e.getMessage());
        }

    }

    @Test
    public void testGetConnectedOutArchiveGZip() throws Exception {
        IInArchive inArchive = getInArchive(ArchiveFormat.SEVEN_ZIP);

        try {
            inArchive.getConnectedOutArchiveGZip();
            fail("SevenZipException expected");
        } catch (SevenZipException e) {
            assertEquals(
                    "Archive format specific update API for GZip-archives can't work with the currently opened 7z-archive",
                    e.getMessage());
        }

    }

    @Test
    public void testGetConnectedOutArchiveBZip2() throws Exception {
        IInArchive inArchive = getInArchive(ArchiveFormat.SEVEN_ZIP);

        try {
            inArchive.getConnectedOutArchiveBZip2();
            fail("SevenZipException expected");
        } catch (SevenZipException e) {
            assertEquals(
                    "Archive format specific update API for BZip2-archives can't work with the currently opened 7z-archive",
                    e.getMessage());
        }

    }

    private IInArchive getInArchive(ArchiveFormat archiveFormat) throws SevenZipException {
        VirtualContent virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(10, 1, 1, 100, 50, null, false);

        ByteArrayStream byteArrayStream = compressVirtualContext(virtualContent, archiveFormat);
        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(archiveFormat, byteArrayStream));
        byte[] newContent = new byte[getRandom().nextInt(1024) + 1024];
        getRandom().nextBytes(newContent);
        return inArchive;
    }


    private ByteArrayStream compressVirtualContext(VirtualContent virtualContent, ArchiveFormat archiveFormat)
            throws SevenZipException {
        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive<IOutItemAllFormats> outArchive = closeLater(SevenZip.openOutArchive(archiveFormat));
        virtualContent.createOutArchive(outArchive, byteArrayStream);
        return byteArrayStream;
    }
}
