package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests compression and extraction of a single file.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public abstract class CompressSingleFileAbstractTest extends CompressAbstractTest {

    private static final int MINIMUM_STREAM_LENGTH = 32769;

    @Test
    public void test0Entropy0() throws Exception {
        doTest(0, 0, false);
    }

    @Test
    public void test0Entropy0Multithreaded() throws Exception {
        doTest(0, 0, true);
    }

    @Test
    public void test1Entropy0() throws Exception {
        doTest(1, 0, false);
    }

    @Test
    public void test1Entropy0Multithreaded() throws Exception {
        doTest(1, 0, true);
    }

    @Test
    public void test2Entropy0() throws Exception {
        doTest(2, 0, false);
    }

    @Test
    public void test2Entropy0Multithreaded() throws Exception {
        doTest(2, 0, true);
    }

    @Test
    public void test3Entropy0() throws Exception {
        doTest(3, 0, false);
    }

    @Test
    public void test3Entropy0Multithreaded() throws Exception {
        doTest(3, 0, true);
    }

    @Test
    public void test4Entropy0() throws Exception {
        doTest(4, 0, false);
    }

    @Test
    public void test4Entropy0Multithreaded() throws Exception {
        doTest(4, 0, true);
    }

    @Test
    public void test5Entropy0() throws Exception {
        doTest(5, 0, false);
    }

    @Test
    public void test5Entropy0Multithreaded() throws Exception {
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
    @Ignore
    // TODO Separate stress tests from functional tests
    public void test20000000Entropy50() throws Exception {
        doTest(20000000, 50, false);
    }

    @Test
    @Ignore
    // TODO Separate stress tests from functional tests
    public void test20000000Entropy50Multithreaded() throws Exception {
        doTest(20000000, 50, true);
    }

    @Test
    @Ignore
    // TODO Separate stress tests from functional tests
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
        outArchive.updateItems(outputByteArrayStream, 1, new SingleFileArchiveUpdateCallback(randomContext));

        System.out.println("Length: " + dataSize + ", entropy: " + entropy + ": compressed size: "
                + outputByteArrayStream.getSize());

        verifyCompressedArchive(randomContext, outputByteArrayStream);

        return outputByteArrayStream.getSize();
    }
}
