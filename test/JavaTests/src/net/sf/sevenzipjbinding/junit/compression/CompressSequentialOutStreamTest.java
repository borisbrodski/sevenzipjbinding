package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Archive creation and update into a plain {@link ISequentialOutStream}, an output stream without seek. 7z needs a
 * seekable output and has to say so with a {@link SevenZipException}; zip, tar, gzip, bzip2 and xz are written
 * sequentially. Before 26.06-2.4 every output stream was wrapped as seekable, so 7-Zip's first seek looked up a
 * <code>seek()</code> method the object doesn't have and aborted the JVM ("Method not found: seek()").
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public class CompressSequentialOutStreamTest extends JUnitNativeTestBase<VoidContext> {
    private static final String PATH = "item.txt";
    private static final byte[] DATA = data(20000);

    /**
     * Write-only stream: implements {@link ISequentialOutStream} and nothing else.
     */
    private static class SequentialOnlyOutStream implements ISequentialOutStream {
        final ByteArrayStream target = new ByteArrayStream(Integer.MAX_VALUE);

        public int write(byte[] data) throws SevenZipException {
            return target.write(data);
        }

        byte[] bytes() {
            return Arrays.copyOf(target.getBytes(), (int) target.getSize());
        }
    }

    private static class CreateCallback implements IOutCreateCallback<IOutItemAllFormats> {
        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory) {
            IOutItemAllFormats item = outItemFactory.createOutItem();
            item.setPropertyPath(PATH);
            item.setDataSize((long) DATA.length);
            return item;
        }

        public ISequentialInStream getStream(int index) {
            return new ByteArrayStream(DATA, true);
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public void setTotal(long total) {
        }

        public void setCompleted(long complete) {
        }
    }

    private static byte[] data(int size) {
        Random random = new Random(size);
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) ('a' + random.nextInt(8));
        }
        return data;
    }

    @Test
    public void sevenZipNeedsSeekableOutput() throws Exception {
        SequentialOnlyOutStream outStream = new SequentialOnlyOutStream();
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP);
        try {
            outArchive.createArchive(outStream, 1, new CreateCallback());
            fail("7z into a plain ISequentialOutStream must fail");
        } catch (SevenZipException e) {
            assertSeekableOutputRequired(e);
        } finally {
            outArchive.close();
        }
    }

    @Test
    public void sevenZipUpdateNeedsSeekableOutput() throws Exception {
        byte[] archive = createWithSeekableOutput(ArchiveFormat.SEVEN_ZIP);
        IInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new ByteArrayStream(archive, false));
        try {
            IOutUpdateArchive<IOutItemAllFormats> outArchive = inArchive.getConnectedOutArchive();
            outArchive.updateItems(new SequentialOnlyOutStream(), 1, new IOutCreateCallback<IOutItemAllFormats>() {
                public IOutItemAllFormats getItemInformation(int index,
                        OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                    return outItemFactory.createOutItem(index);
                }

                public ISequentialInStream getStream(int index) {
                    return null;
                }

                public void setOperationResult(boolean operationResultOk) {
                }

                public void setTotal(long total) {
                }

                public void setCompleted(long complete) {
                }
            });
            fail("7z update into a plain ISequentialOutStream must fail");
        } catch (SevenZipException e) {
            assertSeekableOutputRequired(e);
        } finally {
            inArchive.close();
        }
    }

    @Test
    public void zipWritesSequentially() throws Exception {
        createSequentiallyAndVerify(ArchiveFormat.ZIP);
    }

    @Test
    public void tarWritesSequentially() throws Exception {
        createSequentiallyAndVerify(ArchiveFormat.TAR);
    }

    @Test
    public void gzipWritesSequentially() throws Exception {
        createSequentiallyAndVerify(ArchiveFormat.GZIP);
    }

    @Test
    public void bzip2WritesSequentially() throws Exception {
        createSequentiallyAndVerify(ArchiveFormat.BZIP2);
    }

    @Test
    public void xzWritesSequentially() throws Exception {
        createSequentiallyAndVerify(ArchiveFormat.XZ);
    }

    private void assertSeekableOutputRequired(SevenZipException e) {
        String message = e.getMessage();
        assertTrue("Unexpected message: " + message, message.contains("needs a seekable output stream"));
        assertTrue("Unexpected message: " + message, message.contains("IOutStream"));
    }

    private byte[] createWithSeekableOutput(ArchiveFormat format) throws Exception {
        ByteArrayStream outStream = new ByteArrayStream(Integer.MAX_VALUE);
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(format);
        try {
            outArchive.createArchive(outStream, 1, new CreateCallback());
        } finally {
            outArchive.close();
        }
        return Arrays.copyOf(outStream.getBytes(), (int) outStream.getSize());
    }

    private void createSequentiallyAndVerify(ArchiveFormat format) throws Exception {
        SequentialOnlyOutStream outStream = new SequentialOnlyOutStream();
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(format);
        try {
            outArchive.createArchive(outStream, 1, new CreateCallback());
        } finally {
            outArchive.close();
        }
        byte[] archive = outStream.bytes();
        assertTrue("Empty archive", archive.length > 0);

        IInArchive inArchive = SevenZip.openInArchive(format, new ByteArrayStream(archive, false));
        try {
            assertEquals(1, inArchive.getNumberOfItems());
            ByteArrayStream extracted = new ByteArrayStream(Integer.MAX_VALUE);
            assertEquals(ExtractOperationResult.OK, inArchive.extractSlow(0, extracted));
            assertArrayEquals(DATA, Arrays.copyOf(extracted.getBytes(), (int) extracted.getSize()));
        } finally {
            inArchive.close();
        }
    }
}
