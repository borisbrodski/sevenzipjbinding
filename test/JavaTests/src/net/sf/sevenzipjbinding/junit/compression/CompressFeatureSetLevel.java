package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests setting compression level.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressFeatureSetLevel extends CompressFeatureAbstractSingleFile<VoidContext> {
    private static final int ENTROPY = 100;
    private static final int DATA_SIZE = 300000;
    private static final int DATA_SIZE_LOW_MEMORY = 30000;
    private final ArchiveFormat archiveFormat;
    private final int level;

    private int getDataSize() {
        if (TestConfiguration.getCurrent().isOnLowMemory()) {
            return DATA_SIZE_LOW_MEMORY;
        }
        return DATA_SIZE;
    }

    @Parameters
    public static Collection<Object> getLevels() {
        List<Object> result = new ArrayList<Object>();
        ArchiveFormat[] formats = new ArchiveFormat[] {//
                ArchiveFormat.SEVEN_ZIP, //
                ArchiveFormat.ZIP, //
                ArchiveFormat.BZIP2, //
                ArchiveFormat.GZIP};
        for (ArchiveFormat archiveFormat : formats) {
            for (int level : Arrays.asList(0, 3, 5, 7, 9)) {
                result.add(new Object[] { archiveFormat, level });
            }
        }

        return result;
    }

    public CompressFeatureSetLevel(ArchiveFormat archiveFormat, int level) {
        this.archiveFormat = archiveFormat;
        this.level = level;
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    @Test
    @Multithreaded
    @Repeat
    public void testCompressionFeatureSetLevel() throws Exception {
        IOutCreateArchive<IOutItemAllFormats> outArchive = createArchive();
        addCloseable(outArchive);

        assertTrue(outArchive instanceof IOutFeatureSetLevel);
        IOutFeatureSetLevel featureOutArchive = (IOutFeatureSetLevel) outArchive;
        featureOutArchive.setLevel(level);

        RandomContext randomContext = new RandomContext(getDataSize(), ENTROPY);
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(getDataSize() * 2);
        outArchive.createArchive(outputByteArrayStream, 1, new FeatureSingleFileCreateArchiveCallback(randomContext));
        verifySingleFileArchive(randomContext, outputByteArrayStream);
    }
}
