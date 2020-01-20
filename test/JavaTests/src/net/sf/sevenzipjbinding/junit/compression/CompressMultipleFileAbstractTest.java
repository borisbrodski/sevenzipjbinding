package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutFeatureSetEncryptHeader;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.junit.AbstractTestContext;
import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFileAbstractTest.CompressMultipleFileAbstractTestContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.ParameterIgnores;
import net.sf.sevenzipjbinding.junit.junittools.annotations.ParameterNames;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public abstract class CompressMultipleFileAbstractTest
        extends CompressAbstractTest<CompressMultipleFileAbstractTestContext> {
    public static class CompressMultipleFileAbstractTestContext extends AbstractTestContext {
        VirtualContentConfiguration virtualContentConfiguration;

        boolean useEncryption;
        boolean useEncryptionNoPassword;
        boolean useHeaderEncryption;
    }

    private static final int OUTARCHIVE_MAX_SIZE = 5000000;
    private static final String PASSWORD = "test-pass-321";

    private final int countOfFiles;
    private final int directoriesDepth;
    private final int maxSubdirectories;
    private final int averageFileLength;
    private final int deltaFileLength;
    private final boolean forbiddenRootDirectory;

    @Override
    protected abstract ArchiveFormat getArchiveFormat();

    public void setUseEncryptionNoPassword(boolean useEncryptionNoPassword) {
        context().useEncryptionNoPassword = useEncryptionNoPassword;
    }

    public void setUseEncryption(boolean useEncryption) {
        context().useEncryption = useEncryption;
    }

    public void setUseHeaderEncryption(boolean useHeaderEncryption) {
        context().useHeaderEncryption = useHeaderEncryption;
    }

    @Before
    public void init() {
        context().virtualContentConfiguration = new VirtualContentConfiguration();

    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] { //
                { 1, 0, 0, 0, 0, false }, //
                { 1, 0, 0, 300, 200, false }, //
                { 2, 0, 0, 0, 0, false }, //
                { 2, 0, 0, 300, 200, false }, //
                { 3, 0, 0, 300, 200, false }, //
                { 3, 1, 1, 300, 200, true }, //
                { 65, 5, 2, 200, 150, true }, //
                { 20, 15, 1, 200, 150, true }, //
                { 90, 3, 4, 200, 150, true }, //
                { 10, 1, 2, OUTARCHIVE_MAX_SIZE / 11, 5000, false }, //
                { 50, 1, 2, 80000, 5000, false }//
        });
    }

    @ParameterIgnores
    public static boolean isParameterIgnores(int countOfFiles, int directoriesDepth, int maxSubdirectories,
            int averageFileLength, int deltaFileLength, boolean forbiddenRootDirectory) {
        if (TestConfiguration.getCurrent().isOnLowMemory()) {
            return averageFileLength >= 10000;
        }
        return false;
    }

    @ParameterNames
    public static Collection<String> getParameterNames() {
        return Arrays.asList("count", "depth", "subdirs", "avSize", "dSize", "no-root");
    }

    public CompressMultipleFileAbstractTest(int countOfFiles, int directoriesDepth, int maxSubdirectories,
            int averageFileLength, int deltaFileLength, boolean forbiddenRootDirectory) {
        this.countOfFiles = countOfFiles;
        this.directoriesDepth = directoriesDepth;
        this.maxSubdirectories = maxSubdirectories;
        this.averageFileLength = averageFileLength;
        this.deltaFileLength = deltaFileLength;
        this.forbiddenRootDirectory = forbiddenRootDirectory;
    }

    @Test
    @Multithreaded
    @Repeat
    public void compress() throws Exception {
        if (forbiddenRootDirectory) {
            context().virtualContentConfiguration.setForbiddenRootDirectory(true);
        }
        VirtualContent virtualContent = new VirtualContent(context().virtualContentConfiguration);
        ArchiveFormat archiveFormat = getArchiveFormat();
        virtualContent.fillRandomly(countOfFiles, directoriesDepth, maxSubdirectories, averageFileLength,
                deltaFileLength, null, archiveFormat == ArchiveFormat.TAR);
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(archiveFormat);
        addCloseable(outArchive);

        if (context().useHeaderEncryption) {
            assertTrue(outArchive instanceof IOutFeatureSetEncryptHeader);
            ((IOutFeatureSetEncryptHeader) outArchive).setHeaderEncryption(true);
        }

        String password = context().useEncryptionNoPassword ? null : PASSWORD;

        ByteArrayStream byteArrayStream;
        byteArrayStream = new ByteArrayStream(OUTARCHIVE_MAX_SIZE);

        virtualContent.createOutArchive(outArchive, byteArrayStream, context().useEncryption, password);

        outArchive.close();
        removeCloseable(outArchive);

        // byteArrayStream.writeToOutputStream(new FileOutputStream("/tmp/x.7z"), true);
        byteArrayStream.rewind();
        if (context().useHeaderEncryption) {
            assertHeaderCrypted(byteArrayStream, archiveFormat);
        }
        IInArchive inArchive;
        if (context().useHeaderEncryption) {
            inArchive = SevenZip.openInArchive(archiveFormat, byteArrayStream, password);
        } else {
            inArchive = SevenZip.openInArchive(archiveFormat, byteArrayStream);
        }
        addCloseable(inArchive);
        if (context().useEncryption && !context().useEncryptionNoPassword) {
            assertAllItemsCrypted(inArchive);
        }

        virtualContent.verifyInArchive(inArchive, password);
        // byteArrayStream.writeToOutputStream(new FileOutputStream("test-2.7z"), true);
        //.writeToDirectory(new File("testoutput-1"));
    }
}
