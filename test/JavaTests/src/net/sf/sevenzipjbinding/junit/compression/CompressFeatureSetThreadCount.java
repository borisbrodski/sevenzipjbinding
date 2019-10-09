package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests compression feature: thread count.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressFeatureSetThreadCount extends CompressFeatureAbstractSingleFile<VoidContext> {

    private class ThreadCountingCreateArchiveCallback extends FeatureSingleFileCreateArchiveCallback {
        private Set<Long> threadIdSet = Collections.synchronizedSet(new HashSet<Long>());

        public ThreadCountingCreateArchiveCallback(RandomContext randomContext) {
            super(randomContext);
        }

        @Override
        public ISequentialInStream getStream(int index) throws SevenZipException {
            final ISequentialInStream stream = super.getStream(index);

            return new ISequentialInStream() {

                public int read(byte[] data) throws SevenZipException {
                    threadIdSet.add(Thread.currentThread().getId());
                    return stream.read(data);
                }

                public void close() throws IOException {

                }
            };
        }

        int getInvolvedThreadCount() {
            return threadIdSet.size();
        }
    }

    private static final int ENTROPY = 100;
    private static final int DATA_SIZE = 1000000;
    private static final int DATA_SIZE_LOW_MEMORY = 10000;

    private int getDataSize() {
        if (TestConfiguration.getCurrent().isOnLowMemory()) {
            return DATA_SIZE_LOW_MEMORY;
        }
        return DATA_SIZE;
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

    @Test
    @Multithreaded
    @Repeat
    public void testThreadCount1() throws Exception {
        doTestCompressionFeatureSetMultithreading(1);
    }

    @Test
    @Multithreaded
    @Repeat
    public void testThreadCount2() throws Exception {
        doTestCompressionFeatureSetMultithreading(2);
    }

    private void doTestCompressionFeatureSetMultithreading(int threadCount) throws Exception {
        IOutCreateArchive<IOutItemAllFormats> outArchive = createArchive();
        addCloseable(outArchive);

        assertTrue(outArchive instanceof IOutFeatureSetLevel);
        IOutFeatureSetMultithreading featureOutArchive = (IOutFeatureSetMultithreading) outArchive;
        featureOutArchive.setThreadCount(1);
        featureOutArchive.setThreadCount(2);
        featureOutArchive.setThreadCount(-1); // Set default
        featureOutArchive.setThreadCount(threadCount);

        RandomContext randomContext = new RandomContext(getDataSize(), ENTROPY);
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(getDataSize() * 2);
        ThreadCountingCreateArchiveCallback callback = new ThreadCountingCreateArchiveCallback(randomContext);
        outArchive.createArchive(outputByteArrayStream, 1, callback);
        verifySingleFileArchive(randomContext, outputByteArrayStream);

        assertEquals(threadCount, callback.getInvolvedThreadCount());
    }
}
