package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests setting compression level.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressFeatureSetLevelRatioImpact extends CompressFeatureAbstractSingleFile<VoidContext> {

    private static final int ENTROPY = 100;
    private static final int DATA_SIZE = 200000;
    private final ArchiveFormat archiveFormat;
    private final List<Integer> levels;

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(//
                new Object[] { ArchiveFormat.SEVEN_ZIP, Arrays.asList(1, 3, 5, 7) }, //
                new Object[] { ArchiveFormat.ZIP, Arrays.asList(1, 5, 9) }, //
                new Object[] { ArchiveFormat.BZIP2, Arrays.asList(1, 4, 7) }, //
                new Object[] { ArchiveFormat.GZIP, Arrays.asList(1, 5, 9) }//
        );
    }

    public CompressFeatureSetLevelRatioImpact(ArchiveFormat archiveFormat, List<Integer> levels) {
        this.archiveFormat = archiveFormat;
        this.levels = levels;
    }

    @Test
    public void testCompressionRatioImpact() throws Exception {
        double ration = 0;
        for (int level : levels) {
            double newRation = calcCompressionRatio(level);
            assertTrue("Level " + level + ", ration: " + ration + ", newRation: " + newRation, newRation > ration);
            ration = newRation;
        }
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    private double calcCompressionRatio(int compressionLevel) throws Exception {
        IOutCreateArchive<IOutItemAllFormats> outArchive = createArchive();
        addCloseable(outArchive);

        assertTrue(outArchive instanceof IOutFeatureSetLevel);
        IOutFeatureSetLevel featureOutArchive = (IOutFeatureSetLevel) outArchive;
        featureOutArchive.setLevel(compressionLevel);

        RandomContext randomContext = new RandomContext(DATA_SIZE, ENTROPY);
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(DATA_SIZE * 2);
        outArchive.createArchive(outputByteArrayStream, 1, new FeatureSingleFileCreateArchiveCallback(randomContext));
        verifySingleFileArchive(randomContext, outputByteArrayStream);

        removeCloseable(outArchive);
        closeArchive(outArchive);
        return ((double) randomContext.getSize()) / outputByteArrayStream.getSize();
    }
}
