package net.sf.sevenzipjbinding.junit.misc;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.sevenzipjbinding.ISeekableStream;
import net.sf.sevenzipjbinding.junit.JUnitTestBase;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public abstract class ByteArrayStreamTest extends JUnitTestBase {
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

    public static class ByteArrayStreamTestWithBufferLength6 extends ByteArrayStreamTest {
        @Override
        protected byte[] getTestBuffer() {
            return "012345".getBytes();
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
            getRandom().nextBytes(buffers);
        }

        @Override
        protected byte[] getTestBuffer() {
            return buffers;
        }
    }

    private static final int MAX_SIZE = 8 * 1024 * 1024;

    private static Field chunkListField;
    private static Field currentPositionField;
    private static Field currentPositionInChunkField;
    private static Field currentChunkIndexField;
    private static Field sizeField;

    private byte[] testBuffer;
    private ByteArrayStream byteArrayStream;

    @Before
    public void init() throws Exception {
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
    public void testWrite6ByteFirst() throws Exception {
        write("ABCDEF".getBytes());
        write(testBuffer);
        compareWithExpectedBuffer("ABCDEF".getBytes(), testBuffer);
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
        getRandom().nextBytes(firstBytes);
        write(firstBytes);
        write(testBuffer);
        compareWithExpectedBuffer(firstBytes, testBuffer);
    }

    // ----------------------------------------------------------------
    @Test
    public void testWrite1SetBytes() throws Exception {
        setBytes("A".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite2SetBytes() throws Exception {
        setBytes("AB".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite3SetBytes() throws Exception {
        setBytes("ABC".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite4SetBytes() throws Exception {
        setBytes("ABCD".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite5SetBytes() throws Exception {
        setBytes("ABCDE".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite6SetBytes() throws Exception {
        setBytes("ABCDEF".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite7SetBytes() throws Exception {
        setBytes("ABCDEFG".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite8SetBytes() throws Exception {
        setBytes("ABCDEFGH".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite9SetBytes() throws Exception {
        setBytes("ABCDEFGHI".getBytes());
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
    }

    @Test
    public void testWrite50SetBytes() throws Exception {
        byte[] firstBytes = new byte[50];
        getRandom().nextBytes(firstBytes);
        setBytes(firstBytes);
        write(testBuffer);
        truncateToCurrentPosition();
        compareWithExpectedBuffer(testBuffer);
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
    public void testWriteFromInputStream6ByteFirst() throws Exception {
        writeFromInputStream("ABCDEF".getBytes());
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer("ABCDEF".getBytes(), testBuffer);
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
        getRandom().nextBytes(firstBytes);
        writeFromInputStream(firstBytes);
        writeFromInputStream(testBuffer);
        compareWithExpectedBuffer(firstBytes, testBuffer);
    }

    @Test
    public void testSetSizeEmptyArray() throws Exception {
        write(testBuffer);
        setSize(0);
        compareWithExpectedBuffer(); // Expect empty byte array stream
    }

    @Test
    public void testSetSize1ByteArray() throws Exception {
        write(testBuffer);
        setSize(1);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 1));
    }

    @Test
    public void testSetSize2ByteArray() throws Exception {
        write(testBuffer);
        setSize(2);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 2));
    }

    @Test
    public void testSetSize3ByteArray() throws Exception {
        write(testBuffer);
        setSize(3);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 3));
    }

    @Test
    public void testSetSize4ByteArray() throws Exception {
        write(testBuffer);
        setSize(4);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 4));
    }

    @Test
    public void testSetSize5ByteArray() throws Exception {
        write(testBuffer);
        setSize(5);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 5));
    }

    @Test
    public void testSetSize6ByteArray() throws Exception {
        write(testBuffer);
        setSize(6);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 6));
    }

    @Test
    public void testSetSize7ByteArray() throws Exception {
        write(testBuffer);
        setSize(7);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 7));
    }

    @Test
    public void testSetSize8ByteArray() throws Exception {
        write(testBuffer);
        setSize(8);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 8));
    }

    @Test
    public void testSetSize9ByteArray() throws Exception {
        write(testBuffer);
        setSize(9);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 9));
    }

    @Test
    public void testSetSize200ByteArray() throws Exception {
        write(testBuffer);
        setSize(200);
        compareWithExpectedBuffer(subarray(testBuffer, 0, 200));
    }

    @Test
    public void testSeekTo0() throws Exception {
        testSeekTo(0);
    }

    @Test
    public void testSeekTo1() throws Exception {
        testSeekTo(1);
    }

    @Test
    public void testSeekTo2() throws Exception {
        testSeekTo(2);
    }

    @Test
    public void testSeekTo3() throws Exception {
        testSeekTo(3);
    }

    @Test
    public void testSeekTo4() throws Exception {
        testSeekTo(4);
    }

    @Test
    public void testSeekTo5() throws Exception {
        testSeekTo(5);
    }

    @Test
    public void testSeekTo6() throws Exception {
        testSeekTo(6);
    }

    @Test
    public void testSeekTo7() throws Exception {
        testSeekTo(7);
    }

    @Test
    public void testSeekTo8() throws Exception {
        testSeekTo(8);
    }

    @Test
    public void testSeekTo9() throws Exception {
        testSeekTo(9);
    }

    @Test
    public void testSeekTo50() throws Exception {
        testSeekTo(50);
    }

    /**
     * The test checks, that the virtual content doesn't get expanded after a seek and a read over the file boundary.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testSeekToPlus0AndRead() throws Exception {
        testSeekToXAndRead(testBuffer.length);
    }

    /**
     * The test checks, that the virtual content doesn't get expanded after a seek and a read over the file boundary.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testSeekToPlus1AndRead() throws Exception {
        testSeekToXAndRead(testBuffer.length + 1);
    }

    /**
     * The test checks, that the virtual content doesn't get expanded after a seek and a read over the file boundary.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testSeekToPlus2AndRead() throws Exception {
        testSeekToXAndRead(testBuffer.length + 2);
    }

    /**
     * The test checks, that the virtual content doesn't get expanded after a seek and a read over the file boundary.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testSeekToPlus10AndRead() throws Exception {
        testSeekToXAndRead(testBuffer.length + 10);
    }

    @Test
    public void testReadFromBeginningEmptyArray() throws Exception {
        write(testBuffer);
        seek(0);
        int read = read(new byte[0]);
        assertEquals(Integer.valueOf(0), Integer.valueOf(read));
    }

    @Test
    public void testReadFromBeginning1Byte() throws Exception {
        testRead(1, 0, false);
    }

    @Test
    public void testReadFromBeginning2Byte() throws Exception {
        testRead(2, 0, false);
    }

    @Test
    public void testReadFromBeginning2ByteSingleByte() throws Exception {
        testRead(2, 0, true);
    }

    @Test
    public void testReadFromBeginning3Byte() throws Exception {
        testRead(3, 0, false);
    }

    @Test
    public void testReadFromBeginning3ByteSingleByte() throws Exception {
        testRead(3, 0, true);
    }

    @Test
    public void testReadFromBeginning4Byte() throws Exception {
        testRead(4, 0, false);
    }

    @Test
    public void testReadFromBeginning4ByteSingleByte() throws Exception {
        testRead(4, 0, true);
    }

    @Test
    public void testReadFromBeginning5Byte() throws Exception {
        testRead(5, 0, false);
    }

    @Test
    public void testReadFromBeginning5ByteSingleByte() throws Exception {
        testRead(5, 0, true);
    }

    @Test
    public void testReadFromBeginning6Byte() throws Exception {
        testRead(6, 0, false);
    }

    @Test
    public void testReadFromBeginning6ByteSingleByte() throws Exception {
        testRead(6, 0, true);
    }

    @Test
    public void testReadFromBeginning7Byte() throws Exception {
        testRead(7, 0, false);
    }

    @Test
    public void testReadFromBeginning7ByteSingleByte() throws Exception {
        testRead(7, 0, true);
    }

    @Test
    public void testReadFromBeginning8Byte() throws Exception {
        testRead(8, 0, false);
    }

    @Test
    public void testReadFromBeginning8ByteSingleByte() throws Exception {
        testRead(8, 0, true);
    }

    @Test
    public void testReadFromBeginning9Byte() throws Exception {
        testRead(9, 0, false);
    }

    @Test
    public void testReadFromBeginning9ByteSingleByte() throws Exception {
        testRead(9, 0, true);
    }

    @Test
    public void testReadFromBeginning40Byte() throws Exception {
        testRead(40, 0, false);
    }

    @Test
    public void testReadFromBeginning40ByteSingleByte() throws Exception {
        testRead(40, 0, true);
    }

    @Test
    public void testReadFrom1() throws Exception {
        testRead(10, 1, false);
    }

    @Test
    public void testReadFrom1SingleByte() throws Exception {
        testRead(10, 1, true);
    }

    @Test
    public void testReadFrom2() throws Exception {
        testRead(10, 2, false);
    }

    @Test
    public void testReadFrom2SingleByte() throws Exception {
        testRead(10, 2, true);
    }

    @Test
    public void testReadFrom3() throws Exception {
        testRead(10, 3, false);
    }

    @Test
    public void testReadFrom3SingleByte() throws Exception {
        testRead(10, 3, true);
    }

    @Test
    public void testReadFrom4() throws Exception {
        testRead(10, 4, false);
    }

    @Test
    public void testReadFrom4SingleByte() throws Exception {
        testRead(10, 4, true);
    }

    @Test
    public void testReadFrom5() throws Exception {
        testRead(10, 5, false);
    }

    @Test
    public void testReadFrom5SingleByte() throws Exception {
        testRead(10, 5, true);
    }

    @Test
    public void testReadFrom6() throws Exception {
        testRead(10, 6, false);
    }

    @Test
    public void testReadFrom6SingleByte() throws Exception {
        testRead(10, 6, true);
    }

    @Test
    public void testReadFrom7() throws Exception {
        testRead(10, 7, false);
    }

    @Test
    public void testReadFrom7SingleByte() throws Exception {
        testRead(10, 7, true);
    }

    @Test
    public void testReadFrom8() throws Exception {
        testRead(10, 8, false);
    }

    @Test
    public void testReadFrom8SingleByte() throws Exception {
        testRead(10, 8, true);
    }

    @Test
    public void testReadFrom9() throws Exception {
        testRead(10, 9, false);
    }

    @Test
    public void testReadFrom9SingleByte() throws Exception {
        testRead(10, 9, true);
    }

    @Test
    public void testReadFrom200() throws Exception {
        testRead(10, 200, false);
    }

    @Test
    public void testReadFrom200SingleByte() throws Exception {
        testRead(10, 200, true);
    }

    private void testRead(int length, int offset, boolean readSingleBytes) throws Exception {
        write(testBuffer);
        seek(offset);
        byte[] buffer = new byte[length];
        int read = 0;
        if (readSingleBytes) {
            byte[] singleByteArray = new byte[1];
            for (int i = 0; i < length; i++) {
                int readSingleByte = read(singleByteArray);
                if (readSingleByte == 0) {
                    break;
                }
                read++;
                buffer[i] = singleByteArray[0];
            }
        } else {
            read = read(buffer);
        }
        assertEquals(Integer.valueOf(max(0, min(length, testBuffer.length - offset))), Integer.valueOf(read));
        checkArray(testBuffer, offset, buffer, 0, read);
        assertTrue(read == length || byteArrayStream.isEOF());
    }

    private void testSeekTo(int pos) throws Exception {
        write(testBuffer);
        seek(pos);
        write("x".getBytes());
        if (testBuffer.length <= pos) {
            byte[] newTestBuffer = new byte[pos + 1];
            if (testBuffer.length != 0) {
                System.arraycopy(testBuffer, 0, newTestBuffer, 0, testBuffer.length);
            }
            testBuffer = newTestBuffer;
        }
        testBuffer[pos] = 'x';
        compareWithExpectedBuffer(testBuffer);
    }

    private void testSeekToXAndRead(int pos) throws Exception {
        write(testBuffer);
        seek(pos);
        byte[] buffer = new byte[1];
        assertEquals("EOF expected", 0, read(buffer));
        if (testBuffer.length > 0) {
            seek(0);
            assertEquals(1, read(buffer));
            assertEquals(testBuffer[0], buffer[0]);
        }
        compareWithExpectedBuffer(testBuffer);
    }

    private byte[] subarray(byte[] data, int startPosition, int length) {
        byte[] array = new byte[length];
        int lengthToCopy = data.length < length ? data.length : length;
        System.arraycopy(data, startPosition, array, 0, lengthToCopy);
        return array;
    }

    protected abstract byte[] getTestBuffer();

    private void write(byte[] buffer) throws Exception {
        int currentPosition = byteArrayStream.getCurrentPosition();
        int wrote = byteArrayStream.write(buffer);
        assertEquals(Integer.valueOf(buffer.length), Integer.valueOf(wrote));
        checkInvariant();
        assertEquals("Current position in the stream is incorrect. Initial position: " + currentPosition
                + ", buffer length: " + buffer.length, Integer.valueOf(currentPosition + buffer.length),
                Integer.valueOf(byteArrayStream.getCurrentPosition()));
    }

    private void setBytes(byte[] buffer) throws Exception {
        byteArrayStream.setBytes(buffer, true);
        checkInvariant();
    }

    private void truncate() throws Exception {
        byteArrayStream.truncate();
        checkInvariant();
    }

    private void truncateToCurrentPosition() throws Exception {
        byteArrayStream.setSize(byteArrayStream.getCurrentPosition());
        checkInvariant();
    }

    private void setSize(int newSize) throws Exception {
        byteArrayStream.setSize(newSize);
        checkInvariant();
    }

    private void seek(int position) throws Exception {
        if (byteArrayStream.getSize() > 2) {
            for (int i = byteArrayStream.getCurrentPosition(); i >= 0; i--) {
                doSeek(i);
            }

            assertEquals(Integer.valueOf(1), Integer.valueOf(byteArrayStream.read(new byte[1])));
            assertEquals(Integer.valueOf(1), Integer.valueOf(byteArrayStream.getCurrentPosition()));
        }
        doSeek(position);
    }

    private void doSeek(int position) throws Exception {
        long newPosition = byteArrayStream.seek(position, ISeekableStream.SEEK_SET);
        assertEquals(position, (int) newPosition);
        assertEquals(position, byteArrayStream.getCurrentPosition());
        checkInvariant();
    }

    private int read(byte[] bs) throws Exception {
        int read = byteArrayStream.read(bs);
        assertTrue(bs.length != 0 || read == 0);
        assertTrue(bs.length == 0 || read != 0 || byteArrayStream.isEOF());
        assertTrue(read <= bs.length);
        assertTrue(read == bs.length || byteArrayStream.isEOF());
        checkInvariant();
        return read;
    }

    private void writeFromInputStream(byte[] buffer) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        byteArrayStream.writeFromInputStream(byteArrayInputStream, false);
        checkInvariant();
    }

    private void compareWithExpectedBuffer(byte[]... buffers) throws Exception {
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

    @BeforeClass
    public static void initByteArrayStreamPrivateMethods() throws Exception {
        chunkListField = ByteArrayStream.class.getDeclaredField("chunkList");
        currentPositionField = ByteArrayStream.class.getDeclaredField("currentPosition");
        currentPositionInChunkField = ByteArrayStream.class.getDeclaredField("currentPositionInChunk");
        currentChunkIndexField = ByteArrayStream.class.getDeclaredField("currentChunkIndex");
        sizeField = ByteArrayStream.class.getDeclaredField("size");

        chunkListField.setAccessible(true);
        currentPositionField.setAccessible(true);
        currentPositionInChunkField.setAccessible(true);
        currentChunkIndexField.setAccessible(true);
        sizeField.setAccessible(true);
    }

    private void checkArray(byte[] array1, int startPosition1, byte[] array2, int startPosition2, int length) {
        for (int i = 0; i < length; i++) {
            assertEquals("Byte content differs i=" + i + ", startPosition1=" + startPosition1 + ", startPosition2="
                    + startPosition2, Byte.valueOf(array1[startPosition1 + i]),
                    Byte.valueOf(array2[startPosition2 + i]));
        }
    }

    @SuppressWarnings("unchecked")
    private void checkInvariant() throws Exception {
        List<byte[]> chunkList = (List<byte[]>) chunkListField.get(byteArrayStream);
        int currentPosition = currentPositionField.getInt(byteArrayStream);
        int currentPositionInChunk = currentPositionInChunkField.getInt(byteArrayStream);
        int currentChunkIndex = currentChunkIndexField.getInt(byteArrayStream);
        int size = sizeField.getInt(byteArrayStream);

        if (chunkList.size() == 0) {
            assertTrue(currentPosition == 0);
            assertTrue(currentPositionInChunk == 0);
            assertTrue(currentChunkIndex == -1);
            assertTrue(size == 0);
            return;
        }

        assertTrue(currentPositionInChunk >= 0);
        assertTrue(currentPosition >= 0);
        assertTrue(currentChunkIndex >= 0);
        assertTrue(size >= 0);

        int sizeBeforeLastChunk = 0;
        int sizeBeforeCurrentChunk = 0;
        for (int i = 0; i < chunkList.size() - 1; i++) {
            if (i < currentChunkIndex) {
                sizeBeforeCurrentChunk += chunkList.get(i).length;
            }
            if (i == currentChunkIndex) {
                if (currentChunkIndex < chunkList.size() - 1) {
                    assertTrue(currentPositionInChunk < chunkList.get(i).length);
                } else {
                    assertTrue(currentPositionInChunk <= chunkList.get(i).length);
                }
            }
            sizeBeforeLastChunk += chunkList.get(i).length;
        }
        assertTrue((sizeBeforeCurrentChunk + currentPositionInChunk) == currentPosition);
        assertTrue(sizeBeforeLastChunk <= size);
        assertTrue(sizeBeforeLastChunk + chunkList.get(chunkList.size() - 1).length >= size);

        if (byteArrayStream.isEOF()) {
            assertTrue("In EOF situation current position " + byteArrayStream.getCurrentPosition()
                    + " less then the size of the stream " + byteArrayStream.getSize(),
                    byteArrayStream.getCurrentPosition() >= byteArrayStream.getSize());
        } else {
            assertTrue(byteArrayStream.getCurrentPosition() < byteArrayStream.getSize());
        }
    }
}
