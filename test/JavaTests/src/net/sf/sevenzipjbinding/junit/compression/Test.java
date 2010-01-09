package net.sf.sevenzipjbinding.junit.compression;

import java.io.File;
import java.io.FileOutputStream;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.ISevenZipOutArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class Test {
    @org.junit.Test
    public void testname() throws Exception {
        VirtualContent virtualContent = new VirtualContent();
        virtualContent.fillRandomly(100, 3, 3, 100, 50);
        virtualContent.print();
        File directory = new File("testoutput");
        virtualContent.writeToDirectory(directory);

        ISevenZipOutArchive outArchive = SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP);
        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);

        virtualContent.updateOutArchive(outArchive, byteArrayStream);
        byteArrayStream.rewind();
        ISevenZipInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream);
        virtualContent.verifyInArchive(inArchive);
        byteArrayStream.writeToOutputStream(new FileOutputStream("test-2.7z"), true);
    }
}
