package net.sf.sevenzipjbinding.junit.compression;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class CompressMultipleFile7zPassTest extends CompressMultipleFileAbstractTest {

    public CompressMultipleFile7zPassTest(int countOfFiles, int directoriesDepth, int maxSubdirectories,
            int averageFileLength, int deltaFileLength, boolean forbiddenRootDirectory) {
        super(countOfFiles, directoriesDepth, maxSubdirectories, averageFileLength, deltaFileLength,
                forbiddenRootDirectory);
    }

    @Before
    public void configureEncryption() {
        setUseEncryption(true);
        setUseEncryptionNoPassword(false);
        setUseHeaderEncryption(false);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

}
