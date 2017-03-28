package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutFeatureSetEncryptHeader;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public abstract class CompressMultipleFileAbstractTest extends CompressAbstractTest {

    private static final int OUTARCHIVE_MAX_SIZE = 5000000;
    private static final String PASSWORD = "test-pass-321";

    private VirtualContentConfiguration virtualContentConfiguration;

    private boolean useEncryption;
    private boolean useEncryptionNoPassword;
    private boolean useHeaderEncryption;

    public void setUseEncryptionNoPassword(boolean useEncryptionNoPassword) {
        this.useEncryptionNoPassword = useEncryptionNoPassword;
    }

    public void setUseEncryption(boolean useEncryption) {
        this.useEncryption = useEncryption;
    }

    public void setUseHeaderEncryption(boolean useHeaderEncryption) {
        this.useHeaderEncryption = useHeaderEncryption;
    }

    @Before
    public void init() {
        virtualContentConfiguration = new VirtualContentConfiguration();

    }

    @Test
    public void compressEmptyFile() throws Exception {
        fillRandomlyAndTest(1, 0, 0, 0, 0, false);
    }

    @Test
    public void compressEmptyFileMultithreaded() throws Exception {
        fillRandomlyAndTest(1, 0, 0, 0, 0, true);
    }

    @Test
    public void compressSingleFile() throws Exception {
        fillRandomlyAndTest(1, 0, 0, 300, 200, false);
    }

    @Test
    public void compressSingleFileMultithreaded() throws Exception {
        fillRandomlyAndTest(1, 0, 0, 300, 200, true);
    }

    @Test
    public void compressTwoEmptyFiles() throws Exception {
        fillRandomlyAndTest(2, 0, 0, 0, 0, false);
    }

    @Test
    public void compressTwoEmptyFilesMultithreaded() throws Exception {
        fillRandomlyAndTest(2, 0, 0, 0, 0, true);
    }

    @Test
    public void compressTwoFiles() throws Exception {
        fillRandomlyAndTest(2, 0, 0, 300, 200, false);
    }

    @Test
    public void compressTwoFilesMultithreaded() throws Exception {
        fillRandomlyAndTest(2, 0, 0, 300, 200, true);
    }

    @Test
    public void compressThreeFiles() throws Exception {
        fillRandomlyAndTest(3, 0, 0, 300, 200, false);
    }

    @Test
    public void compressThreeFilesMultithreaded() throws Exception {
        fillRandomlyAndTest(3, 0, 0, 300, 200, true);
    }

    @Test
    public void compressThreeFilesOneDir() throws Exception {
        virtualContentConfiguration.setForbiddenRootDirectory(true);
        fillRandomlyAndTest(3, 1, 1, 300, 200, false);
    }

    @Test
    public void compressThreeFilesOneDirMultithreaded() throws Exception {
        virtualContentConfiguration.setForbiddenRootDirectory(true);
        fillRandomlyAndTest(3, 1, 1, 300, 200, true);
    }

    @Test
    public void compress65FilesManyDepthDirs() throws Exception {
        virtualContentConfiguration.setAllowEmptyFiles(true);
        fillRandomlyAndTest(65, 5, 2, 200, 150, false);
    }

    @Test
    public void compress65FilesManyDepthDirsMultithreaded() throws Exception {
        virtualContentConfiguration.setAllowEmptyFiles(true);
        fillRandomlyAndTest(65, 5, 2, 200, 150, true);
    }

    @Test
    public void compress20FilesVeryDeepDirs() throws Exception {
        virtualContentConfiguration.setAllowEmptyFiles(true);
        fillRandomlyAndTest(20, 15, 1, 200, 150, false);
    }

    @Test
    public void compress20FilesVeryDeepDirsMultithreaded() throws Exception {
        virtualContentConfiguration.setAllowEmptyFiles(true);
        fillRandomlyAndTest(20, 15, 1, 200, 150, true);
    }

    @Test
    public void compress90FilesManyDirs() throws Exception {
        virtualContentConfiguration.setAllowEmptyFiles(true);
        fillRandomlyAndTest(90, 3, 4, 200, 150, false);
    }

    @Test
    public void compress90FilesManyDirsMultithreaded() throws Exception {
        virtualContentConfiguration.setAllowEmptyFiles(true);
        fillRandomlyAndTest(90, 3, 4, 200, 150, true);
    }

    @Test
    public void compress10BigFiles() throws Exception {
        fillRandomlyAndTest(10, 1, 2, OUTARCHIVE_MAX_SIZE / 11, 5000, false);
    }

    @Test
    public void compress10BigFilesMultithreaded() throws Exception {
        fillRandomlyAndTest(10, 1, 2, OUTARCHIVE_MAX_SIZE / 11, 5000, true);
    }

    /**
     * Core dump was caused ones by this test.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void compress100BigFilesCoreDump() throws Exception {
        fillRandomlyAndTest(50, 1, 2, 80000, 5000, false); //.writeToDirectory(new File("testoutput-1"));
    }

    @Test
    public void compress100BigFilesCoreDumpMultithreaded() throws Exception {
        fillRandomlyAndTest(50, 1, 2, 80000, 5000, true);
    }

    @Override
    protected abstract ArchiveFormat getArchiveFormat();

    private VirtualContent fillRandomlyAndTest(final int countOfFiles, final int directoriesDepth,
            final int maxSubdirectories, final int averageFileLength, final int deltaFileLength, boolean multithreaded)
            throws Exception {
        VirtualContent virtualContent = null;
        if (multithreaded) {
            runMultithreaded(new RunnableThrowsException() {
                public void run() throws Exception {
                    fillRandomlyAndTest(countOfFiles, directoriesDepth, maxSubdirectories, averageFileLength,
                            deltaFileLength, false);
                }
            }, null);
        } else {
            for (int i = 0; i < SINGLE_TEST_REPEAT_COUNT; i++) {
                virtualContent = doTest(countOfFiles, directoriesDepth, maxSubdirectories, averageFileLength,
                        deltaFileLength);
            }
        }
        return virtualContent;
    }

    private VirtualContent doTest(int countOfFiles, int directoriesDepth, int maxSubdirectories, int averageFileLength,
            int deltaFileLength) throws Exception {
        VirtualContent virtualContent = new VirtualContent(virtualContentConfiguration);
        ArchiveFormat archiveFormat = getArchiveFormat();
        virtualContent.fillRandomly(countOfFiles, directoriesDepth, maxSubdirectories, averageFileLength,
                deltaFileLength, null, archiveFormat == ArchiveFormat.TAR);
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(archiveFormat);
        addCloseable(outArchive);

        if (useHeaderEncryption) {
            assertTrue(outArchive instanceof IOutFeatureSetEncryptHeader);
            ((IOutFeatureSetEncryptHeader) outArchive).setHeaderEncryption(true);
        }

        String password = useEncryptionNoPassword ? null : PASSWORD;

        ByteArrayStream byteArrayStream;
        byteArrayStream = new ByteArrayStream(OUTARCHIVE_MAX_SIZE);

        virtualContent.createOutArchive(outArchive, byteArrayStream, useEncryption, password);

        outArchive.close();
        removeCloseable(outArchive);

        byteArrayStream.writeToOutputStream(new FileOutputStream("/tmp/x.7z"), true);
        byteArrayStream.rewind();
        if (useHeaderEncryption) {
            assertHeaderCrypted(byteArrayStream, archiveFormat);
        }
        IInArchive inArchive;
        if (useHeaderEncryption) {
            inArchive = SevenZip.openInArchive(archiveFormat, byteArrayStream, password);
        } else {
            inArchive = SevenZip.openInArchive(archiveFormat, byteArrayStream);
        }
        addCloseable(inArchive);
        if (useEncryption && !useEncryptionNoPassword) {
            assertAllItemsCrypted(inArchive);
        }

        virtualContent.verifyInArchive(inArchive, password);
        // byteArrayStream.writeToOutputStream(new FileOutputStream("test-2.7z"), true);

        return virtualContent;
    }
}
