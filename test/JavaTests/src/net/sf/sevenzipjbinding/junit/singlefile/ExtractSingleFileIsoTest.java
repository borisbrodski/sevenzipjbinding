package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileIsoTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileIsoTest() {
        super(ArchiveFormat.ISO, 0, 0, 0);
    }

    @Override
    protected boolean usingZippedTestArchive() {
        return true;
    }
}
