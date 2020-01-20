package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileUdfTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileUdfTest() {
        super(ArchiveFormat.UDF, 102, 150, 150);
    }

    @Override
    protected boolean usingZippedTestArchive() {
        return true;
    }
}
