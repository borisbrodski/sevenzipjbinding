package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

/**
 * Test 7-Zip-JBinding compression/update tracing.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class TraceCompressionTest extends JUnitNativeTestBase {
    private class OutCreateArchive implements IOutCreateCallback<IOutItemAllFormats> {
        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            byteArrayStream.rewind();

            IOutItemAllFormats outItem = outItemFactory.createOutItem();

            outItem.setDataSize((long) byteArrayStream.getSize());
            outItem.setPropertyPath(virtualContent.getItemPath(index));

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return virtualContent.getItemStream(index);
        }
    }

    private class OutUpdateArchive implements IOutCreateCallback<IOutItemAllFormats> {
        private OutCreateArchive outCreateArchive = new OutCreateArchive();

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            if (index % 2 == 0) {
                // Keep item
                IOutItemAllFormats outItem = outItemFactory.createOutItem(index);
                return outItem;
            }

            return outCreateArchive.getItemInformation(index, outItemFactory);

        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return outCreateArchive.getStream(index);
        }
    }

    VirtualContent virtualContent;

    @Test
    public void testCompression7z() throws Exception {
        testCompression(ArchiveFormat.SEVEN_ZIP, "Compressing 2 items\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 0)\n" + //
                "Get property 'propertyAttributes' (index: 0)\n" + //
                "Get property 'propertyLastModificationTime' (index: 0)\n" + //
                "Get property 'propertyPath' (index: 0)\n" + //
                "Get property 'propertyIsDir' (index: 0)\n" + //
                "Get property 'propertyIsAnti' (index: 0)\n" + //
                "Get property 'dataSize' (index: 0)\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 1)\n" + //
                "Get property 'propertyAttributes' (index: 1)\n" + //
                "Get property 'propertyLastModificationTime' (index: 1)\n" + //
                "Get property 'propertyPath' (index: 1)\n" + //
                "Get property 'propertyIsDir' (index: 1)\n" + //
                "Get property 'propertyIsAnti' (index: 1)\n" + //
                "Get property 'dataSize' (index: 1)\n" + //
                "Get stream (index: 0)\n" + //
                "Get stream (index: 1)\n");
    }

    @Test
    public void testCompressionZip() throws Exception {
        testCompression(ArchiveFormat.ZIP, "Compressing 2 items\n"
                + "Get update info (new data: true) (new props: true) (old index: -1) (index: 0)\n"
                + "Get property 'propertyAttributes' (index: 0)\n" + "Get property 'propertyPath' (index: 0)\n"
                + "Get property 'propertyIsDir' (index: 0)\n"
                + "Get property 'propertyLastModificationTime' (index: 0)\n"
                + "Get property 'propertyLastAccessTime' (index: 0)\n"
                + "Get property 'propertyCreationTime' (index: 0)\n" + "Get property 'dataSize' (index: 0)\n"
                + "Get update info (new data: true) (new props: true) (old index: -1) (index: 1)\n"
                + "Get property 'propertyAttributes' (index: 1)\n" + "Get property 'propertyPath' (index: 1)\n"
                + "Get property 'propertyIsDir' (index: 1)\n"
                + "Get property 'propertyLastModificationTime' (index: 1)\n"
                + "Get property 'propertyLastAccessTime' (index: 1)\n"
                + "Get property 'propertyCreationTime' (index: 1)\n" + "Get property 'dataSize' (index: 1)\n"
                + "Get stream (index: 0)\n" +
                "Get stream (index: 1)\n");
    }

    @Test
    public void testCompressionTar() throws Exception {
        testCompression(ArchiveFormat.TAR, "Compressing 2 items\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 0)\n" + //
                "Get property 'propertyIsDir' (index: 0)\n" + //
                "Get property 'propertyPosixAttributes' (index: 0)\n" + //
                "Get property 'propertyLastModificationTime' (index: 0)\n" + //
                "Get property 'propertyPath' (index: 0)\n" + //
                "Get property 'propertyUser' (index: 0)\n" + //
                "Get property 'propertyGroup' (index: 0)\n" + //
                "Get property 'dataSize' (index: 0)\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 1)\n" + //
                "Get property 'propertyIsDir' (index: 1)\n" + //
                "Get property 'propertyPosixAttributes' (index: 1)\n" + //
                "Get property 'propertyLastModificationTime' (index: 1)\n" + //
                "Get property 'propertyPath' (index: 1)\n" + //
                "Get property 'propertyUser' (index: 1)\n" + //
                "Get property 'propertyGroup' (index: 1)\n" + //
                "Get property 'dataSize' (index: 1)\n" + //
                "Get stream (index: 0)\n" + //
                "Get stream (index: 1)\n");
    }

    @Test
    public void testCompressionGZip() throws Exception {
        testCompression(ArchiveFormat.GZIP, "Compressing 1 items\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 0)\n" + //
                "Get property 'propertyLastModificationTime' (index: 0)\n" + //
                "Get property 'propertyPath' (index: 0)\n" + //
                "Get property 'dataSize' (index: 0)\n" + //
                "Get stream (index: 0)\n");
    }

    @Test
    public void testCompressionBZip2() throws Exception {
        testCompression(ArchiveFormat.BZIP2, "Compressing 1 items\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 0)\n" + //
                "Get property 'dataSize' (index: 0)\n" + //
                "Get stream (index: 0)\n");
    }

    @Test
    public void testUpdateZip() throws Exception {
        testUpdate(ArchiveFormat.ZIP, "Updating 2 items\n"
                + "Get update info (new data: false) (new props: false) (old index: 0) (index: 0)\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 1)\n" + //
                "Get property 'propertyAttributes' (index: 1)\n" + //
                "Get property 'propertyPath' (index: 1)\n" + //
                "Get property 'propertyIsDir' (index: 1)\n" + //
                "Get property 'propertyLastModificationTime' (index: 1)\n" + //
                "Get property 'propertyLastAccessTime' (index: 1)\n" + //
                "Get property 'propertyCreationTime' (index: 1)\n" + //
                "Get property 'dataSize' (index: 1)\n" + //
                "Get stream (index: 1)\n");
    }

    @Test
    public void testUpdate7z() throws Exception {
        testUpdate(ArchiveFormat.SEVEN_ZIP, "Updating 2 items\n" + //
                "Get update info (new data: false) (new props: false) (old index: 0) (index: 0)\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 1)\n" + //
                "Get property 'propertyAttributes' (index: 1)\n" + //
                "Get property 'propertyLastModificationTime' (index: 1)\n" + //
                "Get property 'propertyPath' (index: 1)\n" + //
                "Get property 'propertyIsDir' (index: 1)\n" + //
                "Get property 'propertyIsAnti' (index: 1)\n" + //
                "Get property 'dataSize' (index: 1)\n" + //
                "Get stream (index: 1)\n");
    }

    @Test
    public void testUpdateTar() throws Exception {
        testUpdate(ArchiveFormat.TAR, "Updating 2 items\n" + //
                "Get update info (new data: false) (new props: false) (old index: 0) (index: 0)\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 1)\n" + //
                "Get property 'propertyIsDir' (index: 1)\n" + //
                "Get property 'propertyPosixAttributes' (index: 1)\n" + //
                "Get property 'propertyLastModificationTime' (index: 1)\n" + //
                "Get property 'propertyPath' (index: 1)\n" + //
                "Get property 'propertyUser' (index: 1)\n" + //
                "Get property 'propertyGroup' (index: 1)\n" + //
                "Get property 'dataSize' (index: 1)\n" + //
                "Get stream (index: 1)\n");
    }

    @Test
    public void testUpdateGZip() throws Exception {
        testUpdate(ArchiveFormat.GZIP, "Updating 1 items\n" + //
                "Get update info (new data: false) (new props: false) (old index: 0) (index: 0)\n");
    }

    @Test
    public void testUpdateBZip2() throws Exception {
        testUpdate(ArchiveFormat.BZIP2, "Updating 1 items\n" + //
                "Get update info (new data: false) (new props: false) (old index: 0) (index: 0)\n");
    }

    @Test
    public void testCompression7zNoTrace() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(2, 0, 0, 100, 50, null);

        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);

        IOutCreateArchive7z outNewArchive7z = closeLater(SevenZip.openOutArchive7z());

        assertEquals(ArchiveFormat.SEVEN_ZIP, outNewArchive7z.getArchiveFormat());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outNewArchive7z.setTracePrintStream(new PrintStream(out));
        outNewArchive7z.setTrace(false);
        outNewArchive7z.createArchive(byteArrayStream, virtualContent.getItemCount(), new OutCreateArchive());

        assertEquals("", new String(out.toByteArray()));
    }

    private void testUpdate(ArchiveFormat format, String log) throws Exception {
        ByteArrayStream archive = compress(format, null);
        archive.rewind();
        IInArchive inArchive = closeLater(SevenZip.openInArchive(format, archive));
        IOutUpdateArchive<IOutItemAllFormats> outArchive = inArchive.getConnectedOutArchive();

        outArchive.setTrace(true);
        ByteArrayOutputStream traceLogOutputStream = new ByteArrayOutputStream();
        outArchive.setTracePrintStream(new PrintStream(traceLogOutputStream));

        outArchive.updateItems(new ByteArrayStream(10000), inArchive.getNumberOfItems(), new OutUpdateArchive());
        assertEquals(log, new String(traceLogOutputStream.toByteArray()));
    }

    private void testCompression(ArchiveFormat format, String log) throws Exception {
        ByteArrayOutputStream traceLog = new ByteArrayOutputStream();
        compress(format, traceLog);
        assertEquals(log, new String(traceLog.toByteArray()));
    }

    private ByteArrayStream compress(ArchiveFormat format, OutputStream traceLog) throws SevenZipException {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        int countOfFiles = format.supportMultipleFiles() ? 2 : 1;
        virtualContent.fillRandomly(countOfFiles, 0, 0, 100, 50, null);

        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive<IOutItemAllFormats> outNewArchive = closeLater(SevenZip.openOutArchive(format));

        assertEquals(format, outNewArchive.getArchiveFormat());

        if (traceLog != null) {
            outNewArchive.setTracePrintStream(new PrintStream(traceLog));
            outNewArchive.setTrace(true);
        }

        outNewArchive.createArchive(byteArrayStream, virtualContent.getItemCount(), new OutCreateArchive());
        return byteArrayStream;
    }
}
