package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileIsoTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileIsoTest() {
        super(ArchiveFormat.ISO, 0, 0, 0);
    }

    @Override
    protected boolean usingZippedTestArchive() {
        return true;
    }
}
