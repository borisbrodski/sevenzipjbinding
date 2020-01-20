package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.VolumedArchiveInStream;

public abstract class VolumedArchiveInStreamTest {

    public abstract static class NoReadLimit extends VolumedArchiveInStreamTest {
        public NoReadLimit() {
            super(Integer.MAX_VALUE);
        }
    }

    public abstract static class ReadSingleBytes extends VolumedArchiveInStreamTest {
        public ReadSingleBytes() {
            super(1);
        }
    }

    public abstract static class ReadMaxTwoBytes extends VolumedArchiveInStreamTest {
        public ReadMaxTwoBytes() {
            super(2);
        }
    }

    public abstract static class ReadMaxThreeBytes extends VolumedArchiveInStreamTest {
        public ReadMaxThreeBytes() {
            super(3);
        }
    }

    public static class NoReadLimitSeekSet extends NoReadLimit {
        public NoReadLimitSeekSet() {
            setSeekMode(IInStream.SEEK_SET);
        }
    }

    public static class ReadSingleBytesSeekSet extends ReadSingleBytes {
        public ReadSingleBytesSeekSet() {
            setSeekMode(IInStream.SEEK_SET);
        }
    }

    public static class ReadMaxTwoBytesSeekSet extends ReadMaxTwoBytes {
        public ReadMaxTwoBytesSeekSet() {
            setSeekMode(IInStream.SEEK_SET);
        }
    }

    public static class ReadMaxThreeBytesSeekSet extends ReadMaxThreeBytes {
        public ReadMaxThreeBytesSeekSet() {
            setSeekMode(IInStream.SEEK_SET);
        }
    }

    public static class NoReadLimitSeekCur extends NoReadLimit {
        public NoReadLimitSeekCur() {
            setSeekMode(IInStream.SEEK_CUR);
        }
    }

    public static class ReadSingleBytesSeekCur extends ReadSingleBytes {
        public ReadSingleBytesSeekCur() {
            setSeekMode(IInStream.SEEK_CUR);
        }
    }

    public static class ReadMaxTwoBytesSeekCur extends ReadMaxTwoBytes {
        public ReadMaxTwoBytesSeekCur() {
            setSeekMode(IInStream.SEEK_CUR);
        }
    }

    public static class ReadMaxThreeBytesSeekCur extends ReadMaxThreeBytes {
        public ReadMaxThreeBytesSeekCur() {
            setSeekMode(IInStream.SEEK_CUR);
        }
    }

    public static class NoReadLimitSeekEnd extends NoReadLimit {
        public NoReadLimitSeekEnd() {
            setSeekMode(IInStream.SEEK_CUR);
        }
    }

    public static class ReadSingleBytesSeekEnd extends ReadSingleBytes {
        public ReadSingleBytesSeekEnd() {
            setSeekMode(IInStream.SEEK_CUR);
        }
    }

    public static class ReadMaxTwoBytesSeekEnd extends ReadMaxTwoBytes {
        public ReadMaxTwoBytesSeekEnd() {
            setSeekMode(IInStream.SEEK_CUR);
        }
    }

    public static class ReadMaxThreeBytesSeekEnd extends ReadMaxThreeBytes {
        public ReadMaxThreeBytesSeekEnd() {
            setSeekMode(IInStream.SEEK_END);
        }
    }

    final int maxBlockLengthToRead;
    //	final Random random = new Random(this.getClass().getCanonicalName().hashCode());
    private int seekMode;

    @Test
    public void testReadSingleVolume1() throws Exception {
        readTest(new long[] { 1 }, //
                new int[] { 1, eof(1), 1, eof(0) });
    }

    @Test
    public void testReadSingleVolume2() throws Exception {
        readTest(new long[] { 1 }, //
                new int[] { 2, eof(1) });
    }

    @Test
    public void testReadSingleVolume3() throws Exception {
        readTest(new long[] { 10 }, //
                new int[] { 1 });
    }

    @Test
    public void testReadSingleVolume4() throws Exception {
        readTest(new long[] { 10 }, //
                new int[] { 1, 1 });
    }

    @Test
    public void testReadSingleVolume5() throws Exception {
        readTest(new long[] { 10 }, //
                new int[] { 3, 3, 3 });
    }

    @Test
    public void testReadSingleVolume6() throws Exception {
        readTest(new long[] { 10 }, //
                new int[] { 3, 3, 4, eof(4) });
    }

    @Test
    public void testReadSingleVolume7() throws Exception {
        readTest(new long[] { 10 }, //
                new int[] { 3, 3, 5, eof(4) });
    }

    @Test
    public void testReadMultipleVolumes1() throws Exception {
        readTest(new long[] { 1, 1 }, //
                new int[] { 1 });
    }

    @Test
    public void testReadMultipleVolumes2() throws Exception {
        readTest(new long[] { 1, 1 }, //
                new int[] { 1, 1, eof(1) });
    }

    @Test
    public void testReadMultipleVolumes3() throws Exception {
        readTest(new long[] { 1, 1 }, //
                new int[] { 2, eof(2) });
    }

    @Test
    public void testReadMultipleVolumes4() throws Exception {
        readTest(new long[] { 1, 1 }, //
                new int[] { 1, 1, 1, eof(0) });
    }

    @Test
    public void testReadMultipleVolumes5() throws Exception {
        readTest(new long[] { 1, 1 }, //
                new int[] { 3, eof(2) });
    }

    @Test
    public void testReadMultipleVolumes6() throws Exception {
        readTest(new long[] { 2, 2 }, //
                new int[] { 1, 1, 1, 1, eof(1) });
    }

    @Test
    public void testReadMultipleVolumes7() throws Exception {
        readTest(new long[] { 2, 2 }, //
                new int[] { 2, 2, eof(2) });
    }

    @Test
    public void testReadMultipleVolumes8() throws Exception {
        readTest(new long[] { 2, 2 }, //
                new int[] { 3, 3, eof(1) });
    }

    @Test
    public void testReadMultipleVolumes9() throws Exception {
        readTest(new long[] { 100, 200, 300, 400 }, //
                new int[] { 1000, eof(1000) });
    }

    @Test
    public void testReadMultipleVolumes10() throws Exception {
        readTest(new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 }, //
                new int[] { 9, eof(9) });
    }

    @Test
    public void testReadAndSeekSingleVolumes1() throws Exception {
        readTest(new long[] { 1 }, //
                new int[] { 1, eof(1), seek(0), 1, eof(1), seek(0), 1, eof(1) });
    }

    @Test
    public void testReadAndSeekSingleVolumes2() throws Exception {
        readTest(new long[] { 1, 1 }, //
                new int[] { 1, seek(0), 1, seek(0), 2, eof(2) });
    }

    @Test
    public void testReadAndSeekSingleVolumes3() throws Exception {
        readTest(new long[] { 1, 1, 1 }, //
                new int[] { seek(2), 1, eof(1), seek(1), 2, eof(2), seek(0), 3, eof(3) });
    }

    @Test
    public void testReadAndSeekSingleVolumes4() throws Exception {
        readTest(new long[] { 1, 1, 1 }, //
                new int[] { seek(3), 1, eof(0) });
    }

    @Test
    public void testReadAndSeekSingleVolumes5() throws Exception {
        readTest(new long[] { 1, 1, 1 }, //
                new int[] { seek(1), seek(2), seek(1), 2, eof(2), seek(1), 1, seek(3), 1, eof(0) });
    }

    @Test
    public void testReadAndSeekSingleVolumes6() throws Exception {
        readTest(new long[] { 2, 2, 2 }, //
                new int[] { seek(2), 2, 2, seek(1), 2, seek(5), 1, eof(1) });
    }

    @Test
    public void testReadAndSeekSingleVolumes7() throws Exception {
        readTest(new long[] { 10, 10, 10, 10 }, //
                new int[] { seek(1), 30, seek(2), 30, /*seek(3), 30*/});
    }

    @Test
    public void testReadAndSeekSingleVolumes8() throws Exception {
        readTest(new long[] { 10, 10, 10, 10 }, //
                new int[] { seek(1000), 1, eof(0), seek(0), 40, eof(40) });
    }

    @Test
    public void testReadAndSeekSingleVolumes9() throws Exception {
        readTest(new long[] { 10, 1, 10, 1 }, //
                new int[] { 22, eof(22), seek(0), 22, eof(22), seek(1), 22, eof(21),//
                        seek(2), 22, eof(20), seek(8), 22, eof(14), seek(9), 22, eof(13), //
                        seek(10), 22, eof(12), seek(11), 22, eof(11), seek(12), 22, eof(10), //
                        seek(13), 22, eof(9), seek(19), 22, eof(3), seek(20), 22, eof(2), //
                        seek(21), 22, eof(1), seek(22), 22, eof(0) });
    }

    @Test
    public void testReadAndSeekSingleVolumes10() throws Exception {
        readTest(new long[] { 10, 10, 10, 10 }, //
                new int[] { seek(0), 1, 2, 3 });
    }

    @Test
    public void testReadAndSeekSingleVolumes11() throws Exception {
        readTest(new long[] { 124 }, //
                new int[] { seek(0), 32, seek(38), 86, seek(32) });
    }

    VolumedArchiveInStreamTest(int maxBlockLengthToRead) {
        this.maxBlockLengthToRead = maxBlockLengthToRead;

    }

    private void readTest(long[] streamSizes, int[] readSizes) throws SevenZipException {
        VolumedArchiveInStream volumedArchiveInStream;
        try {
            volumedArchiveInStream = new VolumedArchiveInStream("file.7z.001", //
                    new TestArchiveOpenVolumeCallback("file.7z.001", streamSizes));
        } catch (SevenZipException e) {
            throw new Error(e);
        }

        int entireSize = 0;
        for (int i = 0; i < streamSizes.length; i++) {
            entireSize += streamSizes[i];
        }

        int processedSize;
        long offset = 0;
        for (int i = 0; i < readSizes.length; i++) {
            int toRead = readSizes[i];
            int wasRead = 0;
            int expectToRead = toRead;
            boolean expectEOF = false;
            if (i + 1 < readSizes.length && readSizes[i + 1] > Integer.MAX_VALUE - 10000) {
                expectToRead = Integer.MAX_VALUE - readSizes[i + 1];
                expectEOF = true;
                i++;
            }
            if (toRead <= 0) {
                // Seek
                long absolutePosition = -1;
                switch (seekMode) {
                case 0:
                    // Use SEEK_SET
                    absolutePosition = volumedArchiveInStream.seek(-toRead, IInStream.SEEK_SET);
                    break;
                case 1:
                    // Use SEEK_CUR
                    absolutePosition = volumedArchiveInStream.seek(-toRead - offset, IInStream.SEEK_CUR);
                    break;
                case 2:
                    // Use SEEK_END
                    absolutePosition = volumedArchiveInStream.seek(-toRead - entireSize, IInStream.SEEK_END);
                    break;
                }
                assertEquals(-toRead > entireSize ? entireSize : -toRead, absolutePosition);
                offset = absolutePosition;

            } else {
                // Read
                do {
                    byte[] data = new byte[toRead];

                    processedSize = volumedArchiveInStream.read(data);
                    if (processedSize == 0) {
                        assertEquals(entireSize, offset);
                        processedSize = volumedArchiveInStream.read(data);
                        assertEquals(0, processedSize);
                        assertTrue(expectEOF);
                        expectEOF = false;
                        toRead = 0;
                        break;
                    }
                    for (int j = 0; j < processedSize; j++) {
                        assertEquals(data[j], getByteByOffset(offset + j));
                    }
                    offset += processedSize;
                    toRead -= processedSize;
                    wasRead += processedSize;
                } while (toRead > 0);
                assertEquals(expectToRead, wasRead);
                if (expectEOF) {
                    processedSize = volumedArchiveInStream.read(new byte[1]);
                    assertEquals(0, processedSize);
                }
            }
        }
    }

    private static byte getByteByOffset(long offset) {
        //		int hashCode = Long.toString(offset).hashCode();
        //		return (byte) (hashCode ^ (hashCode >> 8) ^ (hashCode >> 16) ^ (hashCode >> 24));
        return (byte) offset;
    }

    private static int eof(int expectToRead) {
        return Integer.MAX_VALUE - expectToRead;
    }

    private static int seek(int offset) {
        return -offset;
    }

    protected void setSeekMode(int seekMode) {
        this.seekMode = seekMode;
    }

    class TestArchiveOpenVolumeCallback implements IArchiveOpenVolumeCallback {
        private final String firstVolumeFilename;
        private final long[] streamSizes;

        TestArchiveOpenVolumeCallback(String firstVolumeFilename, long[] streamSizes) {
            this.firstVolumeFilename = firstVolumeFilename;
            this.streamSizes = streamSizes;
        }

        public Object getProperty(PropID propID) {
            return null;
        }

        public IInStream getStream(String filename) {
            assertEquals(firstVolumeFilename.substring(0, firstVolumeFilename.length() - 3), //
                    filename.substring(0, filename.length() - 3));

            int index = Integer.valueOf(filename.substring(filename.length() - 3));

            if (index > streamSizes.length) {
                return null;
            }

            int initialOffset = 0;
            for (int i = 1; i < index; i++) {
                initialOffset += streamSizes[i - 1];
            }
            return new TestInStream(initialOffset, streamSizes[index - 1]);
        }
    }

    class TestInStream implements IInStream {
        private final long initialOffset;
        private final long size;
        private long offset;

        TestInStream(long initialOffset, long size) {
            this.initialOffset = initialOffset;
            this.size = size;
            offset = 0;
        }

        public long seek(long offset, int seekOrigin) throws SevenZipException {
            switch (seekOrigin) {
            case SEEK_SET:
                this.offset = offset;
                break;

            case SEEK_CUR:
                this.offset += offset;
                break;

            case SEEK_END:
                this.offset = size + offset;
                break;

            default:
                throw new SevenZipException("Seek: unknown origin: " + seekOrigin);
            }
            return this.offset;
        }

        public int read(byte[] data) {
            int read = 0;

            for (int i = 0; i < data.length; i++) {
                if (offset >= size) {
                    break;
                }
                if (i >= maxBlockLengthToRead) {
                    break;
                }
                data[i] = getByteByOffset(initialOffset + offset);
                offset++;
                read++;
            }

            return read;
        }

        public void close() throws IOException {
        }
    }
}
