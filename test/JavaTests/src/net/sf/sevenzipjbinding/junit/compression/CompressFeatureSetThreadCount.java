//package net.sf.sevenzipjbinding.junit.compression;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import net.sf.sevenzipjbinding.ArchiveFormat;
//import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
//import net.sf.sevenzipjbinding.IOutArchive;
//import net.sf.sevenzipjbinding.ISequentialInStream;
//import net.sf.sevenzipjbinding.IInArchive;
//import net.sf.sevenzipjbinding.SevenZip;
//import net.sf.sevenzipjbinding.SevenZipException;
//import net.sf.sevenzipjbinding.junit.tools.RandomContext;
//import net.sf.sevenzipjbinding.util.ByteArrayStream;
//
//import org.junit.Test;
//
///**
// * Tests setting solid.
// *
// * @author Boris Brodski
// * @version 9.13-2.00
// */
//public abstract class CompressFeatureSetThreadCount extends CompressAbstractTest {
//    private static final int ENTROPY = 100;
//    private static final int DATA_SIZE = 1000000;
//
//    public class CompressFeatureSetThreadCountSequentialInStream implements ISequentialInStream {
//        private final ISequentialInStream sequentialInStream;
//
//        public CompressFeatureSetThreadCountSequentialInStream(ISequentialInStream sequentialInStream) {
//            this.sequentialInStream = sequentialInStream;
//        }
//
//        public int read(byte[] data) throws SevenZipException {
//            synchronized (threadSet) {
//                threadSet.add(Thread.currentThread().getId());
//            }
//            return sequentialInStream.read(data);
//        }
//
//    }
//
//    public class CompressFeatureSetThreadCountUpdateCallback extends SingleFileCreateArchiveCallback {
//        CompressFeatureSetThreadCountUpdateCallback(RandomContext randomContext) {
//            super(randomContext);
//        }
//
//        @Override
//        public ISequentialInStream getStream(int index) {
//            ISequentialInStream sequentialInStream = super.getStream(index);
//            if (sequentialInStream != null) {
//                return new CompressFeatureSetThreadCountSequentialInStream(sequentialInStream);
//            }
//            return null;
//        }
//    }
//
//    public static class CompressionFeatureSetThreadCountSevenZip extends CompressFeatureSetThreadCount {
//        @Override
//        protected ArchiveFormat getArchiveFormat() {
//            return ArchiveFormat.SEVEN_ZIP;
//        }
//    }
//
//    private interface FeatureSetMultithreadingTester {
//        public void applyFeatures(IOutFeatureSetMultithreading outArchive);
//    }
//
//    Set<Long> threadSet = new HashSet<Long>();
//
//    @Test
//    public void testCompressionFeatureSetMultithreading() throws Exception {
//        long count11 = testCompressionFeatureSetThreadCount(new FeatureSetMultithreadingTester() {
//            public void applyFeatures(IOutFeatureSetMultithreading outArchive) {
//                outArchive.setThreadCount(1);
//            }
//        });
//        long count12 = testCompressionFeatureSetThreadCount(new FeatureSetMultithreadingTester() {
//            public void applyFeatures(IOutFeatureSetMultithreading outArchive) {
//                outArchive.setThreadCount(2);
//                outArchive.setThreadCount(1);
//            }
//        });
//        long count01 = testCompressionFeatureSetThreadCount(new FeatureSetMultithreadingTester() {
//            public void applyFeatures(IOutFeatureSetMultithreading outArchive) {
//                outArchive.setThreadCount(0);
//            }
//        });
//        long count02 = testCompressionFeatureSetThreadCount(new FeatureSetMultithreadingTester() {
//            public void applyFeatures(IOutFeatureSetMultithreading outArchive) {
//                outArchive.setThreadCount(1);
//                outArchive.setThreadCount(0);
//            }
//        });
//        long countDef1 = testCompressionFeatureSetThreadCount(new FeatureSetMultithreadingTester() {
//            public void applyFeatures(IOutFeatureSetMultithreading outArchive) {
//            }
//        });
//        long countDef2 = testCompressionFeatureSetThreadCount(new FeatureSetMultithreadingTester() {
//            public void applyFeatures(IOutFeatureSetMultithreading outArchive) {
//                outArchive.setThreadCount(8);
//                outArchive.setThreadCount(-1);
//            }
//        });
//
//        assertEquals(1, count11);
//        assertEquals(1, count12);
//        assertEquals(count01, count02);
//        assertTrue(1 < count01);
//        assertTrue(1 < count02);
//        assertEquals(countDef1, countDef2);
//    }
//
//    private long testCompressionFeatureSetThreadCount(FeatureSetMultithreadingTester tester) throws Exception {
//        threadSet.clear();
//        ByteArrayStream outputByteArrayStream = new ByteArrayStream(2 * DATA_SIZE);
//
//        IOutArchive outArchive = SevenZip.openOutArchive(getArchiveFormat());
//
//        assertTrue(outArchive instanceof IOutFeatureSetMultithreading);
//        IOutFeatureSetMultithreading featureOutArchive = (IOutFeatureSetMultithreading) outArchive;
//
//        tester.applyFeatures(featureOutArchive);
//        RandomContext randomContext = new RandomContext(DATA_SIZE, ENTROPY);
//
//        //        long start = System.currentTimeMillis();
//        outArchive
//                .createArchive(outputByteArrayStream, 1, new CompressFeatureSetThreadCountUpdateCallback(randomContext));
//        //        long stop = System.currentTimeMillis();
//
//        outputByteArrayStream.rewind();
//        IInArchive inArchive = SevenZip.openInArchive(getArchiveFormat(), outputByteArrayStream);
//        verifyCompressedArchive(randomContext, outputByteArrayStream);
//        inArchive.close();
//
//        // System.out.println("Time: " + (stop - start) + "   Threads: " + threadSet.size());
//        return threadSet.size();
//    }
//}
