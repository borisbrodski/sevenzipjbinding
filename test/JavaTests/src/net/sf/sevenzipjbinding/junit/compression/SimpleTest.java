package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

/**
 * TODO Remove this test
 *
 * @author Boris Brodski
 * @version 4.65-1
 */
public class SimpleTest extends JUnitNativeTestBase {
    @Test
    public void testCompression() throws Exception {
        try {
            VirtualContent virtualContent = new VirtualContent(new VirtualContentConfiguration());
            virtualContent.fillRandomly(100, 3, 3, 100, 50, null);
            // virtualContent.print();
            // File directory = new File("testoutput");
            // virtualContent.writeToDirectory(directory);

            ByteArrayStream byteArrayStream = new ByteArrayStream(100000);

            IOutArchive outArchive = SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP);
            try {
                virtualContent.updateOutArchive(outArchive, byteArrayStream);
            } finally {
                outArchive.close();
            }

            //    net.sf.sevenzipjbinding.IInStream inStream = new net.sf.sevenzipjbinding.impl.RandomAccessFileInStream(new RandomAccessFile("test-2.7z", "r"));
            byteArrayStream.rewind();
            IInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream);
            try {
                // ISevenZipInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, inStream);
                virtualContent.verifyInArchive(inArchive);
                // byteArrayStream.writeToOutputStream(new FileOutputStream("test-2.7z"), true);
                System.out.println("Archive size: " + byteArrayStream.getSize() + " Bytes");
            } finally {
                inArchive.close();
            }
        } catch (SevenZipException sevenZipException) {
            sevenZipException.printStackTraceExtended();
            throw sevenZipException;
        }
    }
}
