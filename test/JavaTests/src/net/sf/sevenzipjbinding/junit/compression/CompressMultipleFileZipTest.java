package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class CompressMultipleFileZipTest extends CompressMultipleFileAbstractTest {

    public CompressMultipleFileZipTest(int countOfFiles, int directoriesDepth, int maxSubdirectories,
            int averageFileLength, int deltaFileLength, boolean forbiddenRootDirectory) {
        super(countOfFiles, directoriesDepth, maxSubdirectories, averageFileLength, deltaFileLength,
                forbiddenRootDirectory);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }

}
