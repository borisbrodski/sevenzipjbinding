package net.sf.sevenzipjbinding.junit.compression;

import java.util.Date;
import java.util.Random;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveUpdateCallback;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.ISeekableStream;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests compression and extraction of a single file.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public abstract class CompressSingleFileAbstractTest extends JUnitNativeTestBase {
    static class RandomContext implements IInStream {

        private long currentPosition;
        private final long size;
        private long step;

        RandomContext(long size, long entropy) {
            this.size = size;
            currentPosition = 0;
            if (entropy > 0) {
                step = size / entropy;
            } else {
                step = 0;
            }
        }

        /**
         * ${@inheritDoc}
         */
        public int read(byte[] data) throws SevenZipException {
            byte[] buffer = new byte[1];
            for (int i = 0; i < data.length; i++, currentPosition++) {
                if (currentPosition >= size) {
                    return i; // EOF
                }
                long section = step == 0 ? 1 : currentPosition / step;
                if (section % 2 == 0) {
                    new Random(currentPosition).nextBytes(buffer);
                } else {
                    Random sectionRandom = new Random(section * 1000 + size);
                    switch (sectionRandom.nextInt(3)) {
                    case 0:
                        sectionRandom.nextBytes(buffer);
                        break;
                    case 1:
                        sectionRandom.nextBytes(buffer);
                        buffer[0] = (byte) (buffer[0] + currentPosition);
                        break;
                    case 2:
                        sectionRandom.nextBytes(buffer);
                        buffer[0] = (byte) (buffer[0] - currentPosition * 7);
                        break;
                    default:
                        throw new IllegalStateException("Invalid switch value");
                    }
                }
                data[i] = buffer[0];
            }

            return data.length;
        }

        /**
         * ${@inheritDoc}
         */
        public long seek(long offset, int seekOrigin) throws SevenZipException {
            switch (seekOrigin) {
            case SEEK_SET:
                currentPosition = offset;
                break;

            case SEEK_CUR:
                currentPosition = currentPosition + offset;
                break;

            case SEEK_END:
                currentPosition = size + offset;
                break;

            default:
                throw new SevenZipException("Seek: unknown origin: " + seekOrigin);
            }
            return currentPosition;
        }

    }

    public class TestSequentionOutputStream implements ISequentialOutStream {

        private final IInStream inStream;

        public TestSequentionOutputStream(IInStream inStream) {
            this.inStream = inStream;
        }

        public int write(byte[] data) throws SevenZipException {
            byte[] expected = new byte[data.length];
            Assert.assertEquals("Extracted data exceeds expected.", data.length, inStream.read(expected));
            Assert.assertArrayEquals("Extracted data doesn't match expected", expected, data);
            return data.length;
        }

    }

    public class SingleFileArchiveUpdateCallback implements IArchiveUpdateCallback {
        private long size;
        private IInStream inputStream;

        SingleFileArchiveUpdateCallback(long size, IInStream inputStream) {
            super();
            this.size = size;
            this.inputStream = inputStream;
        }

        public Object getProperty(int index, PropID propID) {
            switch (propID) {
            case PATH:
                return "content";

            case IS_FOLDER:
            case IS_ANTI:
                return Boolean.FALSE;

            case SIZE:
                return Long.valueOf(size);
            case LAST_MODIFICATION_TIME:
                return new Date();
            default:
                System.out.println("Unknown property: " + propID);
            }
            return null;
        }

        public ISequentialInStream getStream(int index) {
            return inputStream;
        }

        public void setOperationResult(boolean operationResultOk) {

        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long completeValue) throws SevenZipException {

        }

        public boolean isNewData(int index) {
            return true;
        }

        public boolean isNewProperties(int index) {
            return true;
        }

        public int getOldArchiveItemIndex(int index) {
            return 0;
        }

    }

    private static final int MINIMUM_STREAM_LENGTH = 32769;

    protected abstract ArchiveFormat getArchiveFormat();

    @Test
    public void test0() throws Exception {
        doTest(0, 0, false);
    }

    @Test
    public void test0Multithreaded() throws Exception {
        doTest(0, 0, true);
    }

    @Test
    public void test1() throws Exception {
        doTest(1, 0, false);
    }

    @Test
    public void test1Multithreaded() throws Exception {
        doTest(1, 0, true);
    }

    @Test
    public void test2() throws Exception {
        doTest(2, 0, false);
    }

    @Test
    public void test2Multithreaded() throws Exception {
        doTest(2, 0, true);
    }

    @Test
    public void test3() throws Exception {
        doTest(3, 0, false);
    }

    @Test
    public void test3Multithreaded() throws Exception {
        doTest(3, 0, true);
    }

    @Test
    public void test4() throws Exception {
        doTest(4, 0, false);
    }

    @Test
    public void test4Multithreaded() throws Exception {
        doTest(4, 0, true);
    }

    @Test
    public void test5() throws Exception {
        doTest(5, 0, false);
    }

    @Test
    public void test5Multithreaded() throws Exception {
        doTest(5, 0, true);
    }

    @Test
    public void test10Entropy0() throws Exception {
        doTest(10, 0, false);
    }

    @Test
    public void test10Entropy0Multithreaded() throws Exception {
        doTest(10, 0, true);
    }

    @Test
    public void test10Entropy10() throws Exception {
        doTest(10, 10, false);
    }

    @Test
    public void test10Entropy10Multithreaded() throws Exception {
        doTest(10, 10, true);
    }

    @Test
    public void test11Entropy0() throws Exception {
        doTest(11, 0, false);
    }

    @Test
    public void test11Entropy0Multithreaded() throws Exception {
        doTest(11, 0, true);
    }

    @Test
    public void test11Entropy2() throws Exception {
        doTest(11, 2, false);
    }

    @Test
    public void test11Entropy2Multithreaded() throws Exception {
        doTest(11, 2, true);
    }

    @Test
    public void test11Entropy11() throws Exception {
        doTest(11, 11, false);
    }

    @Test
    public void test11Entropy11Multithreaded() throws Exception {
        doTest(11, 11, true);
    }

    @Test
    public void test57Entropy0() throws Exception {
        doTest(57, 0, false);
    }

    @Test
    public void test57Entropy0Multithreaded() throws Exception {
        doTest(57, 0, true);
    }

    @Test
    public void test57Entropy2() throws Exception {
        doTest(57, 2, false);
    }

    @Test
    public void test57Entropy2Multithreaded() throws Exception {
        doTest(57, 2, true);
    }

    @Test
    public void test57Entropy5() throws Exception {
        doTest(57, 5, false);
    }

    @Test
    public void test57Entropy5Multithreaded() throws Exception {
        doTest(57, 5, true);
    }

    @Test
    public void test57Entropy10() throws Exception {
        doTest(57, 10, false);
    }

    @Test
    public void test57Entropy10Multithreaded() throws Exception {
        doTest(57, 10, true);
    }

    @Test
    public void test57Entropy30() throws Exception {
        doTest(57, 30, false);
    }

    @Test
    public void test57Entropy30Multithreaded() throws Exception {
        doTest(57, 30, true);
    }

    @Test
    public void test57Entropy57() throws Exception {
        doTest(57, 57, false);
    }

    @Test
    public void test57Entropy57Multithreaded() throws Exception {
        doTest(57, 57, true);
    }

    @Test
    public void test277Entropy0() throws Exception {
        doTest(277, 0, false);
    }

    @Test
    public void test277Entropy0Multithreaded() throws Exception {
        doTest(277, 0, true);
    }

    @Test
    public void test277Entropy5() throws Exception {
        doTest(277, 5, false);
    }

    @Test
    public void test277Entropy5Multithreaded() throws Exception {
        doTest(277, 5, true);
    }

    @Test
    public void test277Entropy8() throws Exception {
        doTest(277, 8, false);
    }

    @Test
    public void test277Entropy8Multithreaded() throws Exception {
        doTest(277, 8, true);
    }

    @Test
    public void test277Entropy20() throws Exception {
        doTest(277, 20, false);
    }

    @Test
    public void test277Entropy20Multithreaded() throws Exception {
        doTest(277, 20, true);
    }

    @Test
    public void test277Entropy100() throws Exception {
        doTest(277, 100, false);
    }

    @Test
    public void test277Entropy100Multithreaded() throws Exception {
        doTest(277, 100, true);
    }

    @Test
    public void test277Entropy277() throws Exception {
        doTest(277, 277, false);
    }

    @Test
    public void test277Entropy277Multithreaded() throws Exception {
        doTest(277, 277, true);
    }

    @Test
    public void test1000Entropy0() throws Exception {
        doTest(1000, 0, false);
    }

    @Test
    public void test1000Entropy0Multithreaded() throws Exception {
        doTest(1000, 0, true);
    }

    @Test
    public void test1000Entropy1() throws Exception {
        doTest(1000, 1, false);
    }

    @Test
    public void test1000Entropy1Multithreaded() throws Exception {
        doTest(1000, 1, true);
    }

    @Test
    public void test1000Entropy2() throws Exception {
        doTest(1000, 2, false);
    }

    @Test
    public void test1000Entropy2Multithreaded() throws Exception {
        doTest(1000, 2, true);
    }

    @Test
    public void test1000Entropy5() throws Exception {
        doTest(1000, 5, false);
    }

    @Test
    public void test1000Entropy5Multithreaded() throws Exception {
        doTest(1000, 5, true);
    }

    @Test
    public void test1000Entropy20() throws Exception {
        doTest(1000, 20, false);
    }

    @Test
    public void test1000Entropy20Multithreaded() throws Exception {
        doTest(1000, 20, true);
    }

    @Test
    public void test1000Entropy50() throws Exception {
        doTest(1000, 50, false);
    }

    @Test
    public void test1000Entropy50Multithreaded() throws Exception {
        doTest(1000, 50, true);
    }

    @Test
    public void test1000Entropy200() throws Exception {
        doTest(1000, 200, false);
    }

    @Test
    public void test1000Entropy200Multithreaded() throws Exception {
        doTest(1000, 200, true);
    }

    @Test
    public void test1000Entropy600() throws Exception {
        doTest(1000, 600, false);
    }

    @Test
    public void test1000Entropy600Multithreaded() throws Exception {
        doTest(1000, 600, true);
    }

    @Test
    public void test1000Entropy1000() throws Exception {
        doTest(1000, 1000, false);
    }

    @Test
    public void test1000Entropy1000Multithreaded() throws Exception {
        doTest(1000, 1000, true);
    }

    @Test
    public void test5000Entropy0() throws Exception {
        doTest(5000, 0, false);
    }

    @Test
    public void test5000Entropy0Multithreaded() throws Exception {
        doTest(5000, 0, true);
    }

    @Test
    public void test5000Entropy100() throws Exception {
        doTest(5000, 100, false);
    }

    @Test
    public void test5000Entropy100Multithreaded() throws Exception {
        doTest(5000, 100, true);
    }

    @Test
    public void test5000Entropy5000() throws Exception {
        doTest(5000, 5000, false);
    }

    @Test
    public void test5000Entropy5000Multithreaded() throws Exception {
        doTest(5000, 5000, true);
    }

    @Test
    public void test20000Entropy0() throws Exception {
        doTest(20000, 0, false);
    }

    @Test
    public void test20000Entropy0Multithreaded() throws Exception {
        doTest(20000, 0, true);
    }

    @Test
    public void test20000Entropy200() throws Exception {
        doTest(20000, 200, false);
    }

    @Test
    public void test20000Entropy200Multithreaded() throws Exception {
        doTest(20000, 200, true);
    }

    @Test
    public void test20000Entropy20000() throws Exception {
        doTest(20000, 20000, false);
    }

    @Test
    public void test20000Entropy20000Multithreaded() throws Exception {
        doTest(20000, 20000, true);
    }

    @Test
    public void test400000Entropy0() throws Exception {
        doTest(400000, 0, false);
    }

    @Test
    public void test400000Entropy0Multithreaded() throws Exception {
        doTest(400000, 0, true);
    }

    @Test
    public void test400000Entropy300() throws Exception {
        doTest(400000, 300, false);
    }

    @Test
    public void test400000Entropy300Multithreaded() throws Exception {
        doTest(400000, 300, true);
    }

    @Test
    public void test400000Entropy400000() throws Exception {
        doTest(400000, 400000, false);
    }

    @Test
    public void test400000Entropy400000Multithreaded() throws Exception {
        doTest(400000, 400000, true);
    }

    @Test
    public void test3000000Entropy0() throws Exception {
        doTest(3000000, 0, false);
    }

    @Test
    public void test3000000Entropy0Multithreaded() throws Exception {
        doTest(3000000, 0, true);
    }

    @Test
    public void test3000000Entropy800() throws Exception {
        doTest(3000000, 8000, false);
    }

    @Test
    public void test3000000Entropy800Multithreaded() throws Exception {
        doTest(3000000, 8000, true);
    }

    @Test
    public void test3000000Entropy3000000() throws Exception {
        doTest(3000000, 3000000, false);
    }

    @Test
    public void test3000000Entropy3000000Multithreaded() throws Exception {
        doTest(3000000, 3000000, true);
    }

    @Test
    public void test20000000Entropy50() throws Exception {
        doTest(20000000, 50, false);
    }

    @Test
    public void test20000000Entropy50Multithreaded() throws Exception {
        doTest(20000000, 50, true);
    }

    @Test
    public void test500000000Entropy50() throws Exception {
        doTest(500000000, 50);
    }

    private long doTest(final int dataSize, final int entropy, boolean multithreaded) throws Exception {
        long result = 0;
        if (multithreaded) {
            runMultithreaded(new RunnableThrowsException() {
                public void run() throws Exception {
                    doTest(dataSize, entropy, false);
                }
            }, null);
        } else {
            int repeatCount = dataSize == 0 ? SINGLE_TEST_REPEAT_COUNT * 100
                    : 1 + (SINGLE_TEST_REPEAT_COUNT * 100 / dataSize);

            for (int i = 0; i < repeatCount; i++) {
                result = doTest(dataSize, entropy);
            }
        }
        return result;
    }

    private long doTest(int dataSize, int entropy) throws Exception {
        System.out.println(getArchiveFormat());
        RandomContext randomContext = new RandomContext(dataSize, entropy);
        int maxStreamSize = dataSize + MINIMUM_STREAM_LENGTH;
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(maxStreamSize);

        IOutArchive outArchive = SevenZip.openOutArchive(getArchiveFormat());
        //        if (outArchiveInitialization != null) {
        //            outArchiveInitialization.initializeOutArchive(outArchive);
        //        }
        outArchive.updateItems(outputByteArrayStream, 1, new SingleFileArchiveUpdateCallback(dataSize, randomContext));

        System.out.println("Length: " + dataSize + ", entropy: " + entropy + ": compressed size: "
                + outputByteArrayStream.getSize());

        randomContext.seek(0, ISeekableStream.SEEK_SET);
        outputByteArrayStream.rewind();

        ISevenZipInArchive inArchive = null;
        boolean successfull = false;
        try {
            inArchive = SevenZip.openInArchive(null, outputByteArrayStream);
            Assert.assertEquals(getArchiveFormat(), inArchive.getArchiveFormat());
            inArchive.extractSlow(0, new TestSequentionOutputStream(randomContext));
            successfull = true;
        } finally {
            try {
                if (inArchive != null) {
                    inArchive.close();
                }
            } catch (Throwable throwable) {
                if (successfull) {
                    throw new RuntimeException("Error closing InArchive", throwable);
                }
            }
        }

        return outputByteArrayStream.getSize();
    }
}
