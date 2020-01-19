package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileIsoTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileIsoTest() {
        super(ArchiveFormat.ISO, 0, 1, 2);
    }

    @Override
    protected boolean usingZippedTestArchive() {
        return true;
    }
}
