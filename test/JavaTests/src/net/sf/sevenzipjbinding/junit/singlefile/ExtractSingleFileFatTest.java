package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileFatTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileFatTest() {
        super(ArchiveFormat.FAT, 1, 2, 4);
    }

    @Override
    protected boolean usingZippedTestArchive() {
        return true;
    }
}
