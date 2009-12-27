package net.sf.sevenzipjbinding.junit.util;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Before;
import org.junit.Test;

public abstract class ByteArrayStreamTest {
    public static class ByteArrayStreamTestWithEmptyBuffer extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return new byte[0];
        }
    }

    public static class ByteArrayStreamTestWithBufferLength1 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "0".getBytes();
        }
    }

    public static class ByteArrayStreamTestWithBufferLength2 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "01".getBytes();
        }
    }

    public static class ByteArrayStreamTestWithBufferLength3 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "012".getBytes();
        }
    }

    public static class ByteArrayStreamTestWithBufferLength4 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "0123".getBytes();
        }
    }

    public static class ByteArrayStreamTestWithBufferLength5 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "01234".getBytes();
        }
    }

    public static class ByteArrayStreamTestWithBufferLength7 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "0123456".getBytes();
        }
    }

    public static class ByteArrayStreamTestWithBufferLength8 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "01234567".getBytes();
        }
    }

    public static class ByteArrayStreamTestWithBufferLength9 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "012345678".getBytes();
        }
    }

    public static class ByteArrayStreamTestWithBufferLength100 extends ByteArrayStreamTest {
        private byte[] buffers = new byte[100];
        {
            random.nextBytes(buffers);
        }

        @Override
        protected byte[] getTestBuffer() {
            return buffers;
        }
    }

    private static final int MAX_SIZE = 8 * 1024 * 1024;
    private static Random random = new Random(0);
    private byte[] testBuffer;
    private ByteArrayStream byteArrayStream;

    @Before
    public void init() {
        testBuffer = getTestBuffer();
        byteArrayStream = new ByteArrayStream(4, MAX_SIZE);
        checkInvariant();
    }

    @Test
    public void testSimpleWrite() throws Exception {
        write(testBuffer);
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testSimpleWriteAfterTruncateEmpty() throws Exception {
        truncate();
        write(testBuffer);
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testSimpleWriteAfterTruncateNonEmpty() throws Exception {
        write("ABCDEF".getBytes());
        truncate();
        write(testBuffer);
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite1ByteFirst() throws Exception {
        write("A".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("A".getBytes(), testBuffer);
    }

    @Test
    public void testWrite2ByteFirst() throws Exception {
        write("AB".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("AB".getBytes(), testBuffer);
    }

    @Test
    public void testWrite3ByteFirst() throws Exception {
        write("ABC".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("ABC".getBytes(), testBuffer);
    }

    @Test
    public void testWrite4ByteFirst() throws Exception {
        write("ABCD".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("ABCD".getBytes(), testBuffer);
    }

    @Test
    public void testWrite5ByteFirst() throws Exception {
        write("ABCDE".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("ABCDE".getBytes(), testBuffer);
    }

    @Test
    public void testWrite7ByteFirst() throws Exception {
        write("ABCDEFG".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("ABCDEFG".getBytes(), testBuffer);
    }

    @Test
    public void testWrite8ByteFirst() throws Exception {
        write("ABCDEFGH".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("ABCDEFGH".getBytes(), testBuffer);
    }

    @Test
    public void testWrite9ByteFirst() throws Exception {
        write("ABCDEFGHI".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("ABCDEFGHI".getBytes(), testBuffer);
    }

    @Test
    public void testWrite50ByteFirst() throws Exception {
        byte[] firstBytes = new byte[50];
        random.nextBytes(firstBytes);
        write(firstBytes);
        write(testBuffer);
        compareWithExpectedBuffer(firstBytes, testBuffer);
    }

    // ----------------------------------------------------------------

    @Test
    public void testSimpleWriteFromInputStream() throws Exception {
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testSimpleWriteFromInputStreamAfterTruncateEmpty() throws Exception {
        truncate();
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testSimpleWriteFromInputStreamAfterTruncateNonEmpty() throws Exception {
        writeFromInputStream("ABCDEF".getBytes());
        truncate();
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWriteFromInputStream1ByteFirst() throws Exception {
        writeFromInputStream("A".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("A".getBytes(), testBuffer);
    }

    @Test
    public void testWriteFromInputStream2ByteFirst() throws Exception {
        writeFromInputStream("AB".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("AB".getBytes(), testBuffer);
    }

    @Test
    public void testWriteFromInputStream3ByteFirst() throws Exception {
        writeFromInputStream("ABC".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("ABC".getBytes(), testBuffer);
    }

    @Test
    public void testWriteFromInputStream4ByteFirst() throws Exception {
        writeFromInputStream("ABCD".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("ABCD".getBytes(), testBuffer);
    }

    @Test
    public void testWriteFromInputStream5ByteFirst() throws Exception {
        writeFromInputStream("ABCDE".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("ABCDE".getBytes(), testBuffer);
    }

    @Test
    public void testWriteFromInputStream7ByteFirst() throws Exception {
        writeFromInputStream("ABCDEFG".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("ABCDEFG".getBytes(), testBuffer);
    }

    @Test
    public void testWriteFromInputStream8ByteFirst() throws Exception {
        writeFromInputStream("ABCDEFGH".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("ABCDEFGH".getBytes(), testBuffer);
    }

    @Test
    public void testWriteFromInputStream9ByteFirst() throws Exception {
        writeFromInputStream("ABCDEFGHI".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("ABCDEFGHI".getBytes(), testBuffer);
    }

    @Test
    public void testWriteFromInputStream50ByteFirst() throws Exception {
        byte[] firstBytes = new byte[50];
        random.nextBytes(firstBytes);
        writeFromInputStream(firstBytes);
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer(firstBytes, testBuffer);
    }

    protected abstract byte[] getTestBuffer();

    private void write(byte[] buffer) {
        byteArrayStream.write(buffer);
        checkInvariant();
    }

    private void truncate() {
        byteArrayStream.truncate();
        checkInvariant();
    }

    private void writeFromInputStream(byte[] buffer) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        byteArrayStream.writeFromInputStream(byteArrayInputStream, false);
        checkInvariant();

    }

    private void compareWithExpectedBuffer(byte[]... buffers) throws IOException {
        ByteArrayOutputStream expectedByteArrayOutputStream = new ByteArrayOutputStream();
        for (byte[] buffer : buffers) {
            expectedByteArrayOutputStream.write(buffer);
        }
        byte[] expected = expectedByteArrayOutputStream.toByteArray();
        checkInvariant();
        assertArrayEquals(expected, byteArrayStream.getBytes());
        checkInvariant();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayStream.writeToOutputStream(byteArrayOutputStream, true);
        checkInvariant();
        assertArrayEquals(expected, byteArrayOutputStream.toByteArray());
    }

    private void checkInvariant() {
        byteArrayStream.checkInvariant();

    }
}
