package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

/**
 * Tests setting compression level.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class CompressFeatureSetLevel extends CompressFeatureAbstractSingleFile {
    public static class CompressionFeatureSetLevelSevenZip extends CompressFeatureSetLevel {
        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.SEVEN_ZIP;
        }

        @Override
        protected int[] getCompressionLevels() {
            return new int[] { 1, 3, 5, 7 };
        }
    }

    public static class CompressionFeatureSetLevelZip extends CompressFeatureSetLevel {
        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.ZIP;
        }

        @Override
        protected int[] getCompressionLevels() {
            return new int[] { 1, 5, 9 };
        }
    }

    public static class CompressionFeatureSetLevelBZip2 extends CompressFeatureSetLevel {
        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.BZIP2;
        }

        @Override
        protected int[] getCompressionLevels() {
            return new int[] { 1, 4, 7 };
        }
    }

    public static class CompressionFeatureSetLevelGZip extends CompressFeatureSetLevel {
        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.GZIP;
        }

        @Override
        protected int[] getCompressionLevels() {
            return new int[] { 1, 5, 9 };
        }
    }

    private static final int ENTROPY = 100;
    private static final int DATA_SIZE = 300000;

    @Test
    public void testCompressionFeatureSetLevel0() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(0);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel0Multithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(0);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel3() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(3);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel3Multithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(3);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel5() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(5);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel5Multithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(5);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel7() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(7);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel7Multithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(7);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel9() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(9);
            }
        });
    }

    @Test
    public void testCompressionFeatureSetLevel9Multithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionFeatureSetLevel(9);
            }
        });
    }

    protected abstract int[] getCompressionLevels();

    @Test
    public void testCompressionRationImpact() throws Exception {
        double ration = 0;
        for (int level : getCompressionLevels()) {
            double newRation = calcCompressionRation(level);
            assertTrue("Level " + level + ", ration: " + ration + ", newRation: " + newRation, newRation > ration);
            ration = newRation;
        }
    }

    protected void doTestCompressionFeatureSetLevel(int compressionLevel) throws Exception {
        calcCompressionRation(compressionLevel);
    }

    private double calcCompressionRation(int compressionLevel) throws Exception {
        IOutCreateArchive<IOutItemAllFormats> outArchive = createArchive();

        assertTrue(outArchive instanceof IOutFeatureSetLevel);
        IOutFeatureSetLevel featureOutArchive = (IOutFeatureSetLevel) outArchive;
        featureOutArchive.setLevel(compressionLevel);

        RandomContext randomContext = new RandomContext(DATA_SIZE, ENTROPY);
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(DATA_SIZE * 2);
        outArchive.createArchive(outputByteArrayStream, 1, new FeatureSingleFileCreateArchiveCallback(randomContext));
        verifySingleFileArchive(randomContext, outputByteArrayStream);

        closeArchive(outArchive);
        return ((double) randomContext.getSize()) / outputByteArrayStream.getSize();
    }
}
