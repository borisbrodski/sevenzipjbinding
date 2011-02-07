package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.AfterClass;
import org.junit.Test;

/**
 * Tests setting compression level.
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class CompressFeatureSetLevel extends CompressAbstractTest {
    public static class CompressionFeatureSetLevelSevenZip extends CompressFeatureSetLevel {
        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.SEVEN_ZIP;
        }

        @AfterClass
        public static void afterClass() {
            assertTrue(checkCompressionLenghts(ArchiveFormat.SEVEN_ZIP));
        }
    }

    public static class CompressionFeatureSetLevelZip extends CompressFeatureSetLevel {
        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.ZIP;
        }

        @AfterClass
        public static void afterClass() {
            assertTrue(checkCompressionLenghts(ArchiveFormat.ZIP));
        }
    }

    public static class CompressionFeatureSetLevelBZip2 extends CompressFeatureSetLevel {
        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.BZIP2;
        }

        @AfterClass
        public static void afterClass() {
            assertTrue(checkCompressionLenghts(ArchiveFormat.BZIP2));
        }
    }

    public static class CompressionFeatureSetLevelGZip extends CompressFeatureSetLevel {
        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.GZIP;
        }

        @AfterClass
        public static void afterClass() {
            assertTrue(checkCompressionLenghts(ArchiveFormat.GZIP));
        }
    }

    private static final int ENTROPY = 1000;
    private static final int DATA_SIZE = 100000;
    private static final int EXPECTED_TESTS_COUNT = 6;

    private static Map<ArchiveFormat, SortedMap<Integer, Integer>> archiveLengthMapMap = new HashMap<ArchiveFormat, SortedMap<Integer, Integer>>();
    private static Set<ArchiveFormat> archiveLengthCheckedMap = new HashSet<ArchiveFormat>();

    @Test
    public void testCompressionFeatureSetLevel0() throws Exception {
        testCompressionFeatureSetLevel(0);
    }

    @Test
    public void testCompressionFeatureSetLevel1() throws Exception {
        testCompressionFeatureSetLevel(1);
    }

    @Test
    public void testCompressionFeatureSetLevel3() throws Exception {
        testCompressionFeatureSetLevel(3);
    }

    @Test
    public void testCompressionFeatureSetLevel5() throws Exception {
        testCompressionFeatureSetLevel(5);
    }

    @Test
    public void testCompressionFeatureSetLevel7() throws Exception {
        testCompressionFeatureSetLevel(7);
    }

    @Test
    public void testCompressionFeatureSetLevel9() throws Exception {
        testCompressionFeatureSetLevel(9);
    }

    @Test
    public void testCheckCompressionLenghts() throws Exception {
        checkCompressionLenghts(getArchiveFormat());
    }

    private static boolean checkCompressionLenghts(ArchiveFormat archiveFormat) {
        SortedMap<Integer, Integer> archiveLengthMap = archiveLengthMapMap.get(archiveFormat);
        if (archiveLengthCheckedMap.contains(archiveFormat)) {
            return true;
        }
        if (archiveLengthMap == null || archiveLengthMap.size() != EXPECTED_TESTS_COUNT) {
            return false;
        }

        int lastLength = DATA_SIZE * 2;
        for (Integer compressionLevel : archiveLengthMap.keySet()) {
            if (compressionLevel.equals(0) && archiveFormat != ArchiveFormat.BZIP2
                    && archiveFormat != ArchiveFormat.GZIP) {
                lastLength = archiveLengthMap.get(compressionLevel);
                assertTrue(lastLength >= DATA_SIZE);
                continue;
            }
            int length = archiveLengthMap.get(compressionLevel);
            assertTrue(length <= (int) (lastLength * 1.01));
            lastLength = length;
        }
        archiveLengthCheckedMap.add(archiveFormat);
        return true;
    }

    private void testCompressionFeatureSetLevel(int compressionLevel) throws Exception {
        RandomContext randomContext = new RandomContext(DATA_SIZE, ENTROPY);
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(DATA_SIZE * 2);

        IOutArchive outArchive = SevenZip.openOutArchive(getArchiveFormat());

        assertTrue(outArchive instanceof IFeatureSetLevel);
        IFeatureSetLevel featureOutArchive = (IFeatureSetLevel) outArchive;
        featureOutArchive.setLevel(compressionLevel);

        outArchive.updateItems(outputByteArrayStream, 1, new SingleFileArchiveUpdateCallback(randomContext));

        verifyCompressedArchive(randomContext, outputByteArrayStream);

        SortedMap<Integer, Integer> archiveLengthMap = archiveLengthMapMap.get(getArchiveFormat());
        if (archiveLengthMap == null) {
            archiveLengthMap = new TreeMap<Integer, Integer>();
            archiveLengthMapMap.put(getArchiveFormat(), archiveLengthMap);
        }

        archiveLengthMap.put(compressionLevel, outputByteArrayStream.getSize());
        System.out.println("" + compressionLevel + " -  " + outputByteArrayStream.getSize());
    }
}
