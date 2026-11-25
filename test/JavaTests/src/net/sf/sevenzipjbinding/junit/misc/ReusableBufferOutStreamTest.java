package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IReusableBufferOutStream;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * {@link IReusableBufferOutStream} (issue #69): an output stream implementing it gets the same <code>byte[]</code>
 * for every chunk with the chunk length as parameter, instead of a new array per call; a plain
 * {@link ISequentialOutStream} keeps getting a fresh array of exactly the chunk size. Checked for the extraction output
 * and for the archive output of a creation, with data large enough for several chunks.
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public class ReusableBufferOutStreamTest extends JUnitNativeTestBase<VoidContext> {
    private static final int DATA_SIZE = 5 * 1024 * 1024 + 12345; // Not a multiple of any chunk size
    private static final byte[] DATA = data(DATA_SIZE);

    /**
     * Records every call: the array instances seen, the call count and whether a call had fewer valid bytes than the
     * array holds.
     */
    private static class RecordingStream implements IReusableBufferOutStream {
        final ByteArrayStream target = new ByteArrayStream(Integer.MAX_VALUE);
        final Map<byte[], Boolean> arrays = new IdentityHashMap<byte[], Boolean>();
        int calls;
        int callsWithShorterLength;

        public int write(byte[] buffer, int offset, int length) throws SevenZipException {
            calls++;
            arrays.put(buffer, Boolean.TRUE);
            assertEquals(0, offset);
            assertTrue("length " + length, length > 0 && length <= buffer.length);
            if (length < buffer.length) {
                callsWithShorterLength++;
            }
            return target.write(Arrays.copyOfRange(buffer, offset, offset + length));
        }

        byte[] bytes() {
            return Arrays.copyOf(target.getBytes(), (int) target.getSize());
        }
    }

    /**
     * Plain stream: every array must be exactly the chunk (no reuse, no trailing bytes).
     */
    private static class PlainStream implements ISequentialOutStream {
        final ByteArrayStream target = new ByteArrayStream(Integer.MAX_VALUE);
        final Map<byte[], Boolean> arrays = new IdentityHashMap<byte[], Boolean>();
        int calls;

        public int write(byte[] data) throws SevenZipException {
            calls++;
            arrays.put(data, Boolean.TRUE);
            return target.write(data);
        }

        byte[] bytes() {
            return Arrays.copyOf(target.getBytes(), (int) target.getSize());
        }
    }

    private static byte[] data(int size) {
        Random random = new Random(size);
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) ('a' + random.nextInt(16));
        }
        return data;
    }

    private static byte[] createZip(ISequentialOutStream outStream) throws Exception {
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(ArchiveFormat.ZIP);
        try {
            outArchive.createArchive(outStream, 1, new IOutCreateCallback<IOutItemAllFormats>() {
                public IOutItemAllFormats getItemInformation(int index,
                        OutItemFactory<IOutItemAllFormats> outItemFactory) {
                    IOutItemAllFormats item = outItemFactory.createOutItem();
                    item.setPropertyPath("data.bin");
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
            });
        } finally {
            outArchive.close();
        }
        return outStream instanceof RecordingStream ? ((RecordingStream) outStream).bytes()
                : ((PlainStream) outStream).bytes();
    }

    private static void extract(byte[] archive, ISequentialOutStream outStream) throws Exception {
        IInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.ZIP, new ByteArrayStream(archive, false));
        try {
            assertEquals(1, inArchive.getNumberOfItems());
            assertEquals(ExtractOperationResult.OK, inArchive.extractSlow(0, outStream));
        } finally {
            inArchive.close();
        }
    }

    @Test
    public void extractionReusesTheBuffer() throws Exception {
        byte[] archive = createZip(new PlainStream());
        RecordingStream outStream = new RecordingStream();
        extract(archive, outStream);
        assertArrayEquals(DATA, outStream.bytes());
        assertTrue("calls: " + outStream.calls, outStream.calls >= 3);
        assertTrue("arrays: " + outStream.arrays.size() + " for " + outStream.calls + " calls",
                outStream.arrays.size() < outStream.calls);
        assertTrue("the last chunk is shorter than the buffer", outStream.callsWithShorterLength >= 1);
    }

    @Test
    public void creationReusesTheBuffer() throws Exception {
        RecordingStream outStream = new RecordingStream();
        byte[] archive = createZip(outStream);
        assertTrue("calls: " + outStream.calls, outStream.calls >= 3);
        assertTrue("arrays: " + outStream.arrays.size() + " for " + outStream.calls + " calls",
                outStream.arrays.size() < outStream.calls);
        PlainStream check = new PlainStream();
        extract(archive, check);
        assertArrayEquals(DATA, check.bytes());
    }

    @Test
    public void plainStreamGetsOneArrayPerChunk() throws Exception {
        byte[] archive = createZip(new PlainStream());
        PlainStream outStream = new PlainStream();
        extract(archive, outStream);
        assertArrayEquals(DATA, outStream.bytes());
        assertTrue("calls: " + outStream.calls, outStream.calls >= 3);
        assertEquals("every call gets its own array", outStream.calls, outStream.arrays.size());
    }

    @Test
    public void defaultWriteDelegatesToTheBufferVariant() throws Exception {
        RecordingStream outStream = new RecordingStream();
        byte[] data = data(1000);
        assertEquals(data.length, outStream.write(data));
        assertEquals(1, outStream.calls);
        assertArrayEquals(data, outStream.bytes());
    }
}
