package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileFatTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileFatTest() {
        super(ArchiveFormat.FAT, 1, 2, 4);
    }

    @Override
    protected boolean usingZippedTestArchive() {
        return true;
    }
}
