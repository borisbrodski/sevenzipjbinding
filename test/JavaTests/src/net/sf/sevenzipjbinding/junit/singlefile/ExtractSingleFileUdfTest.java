package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileUdfTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileUdfTest() {
        super(ArchiveFormat.UDF, 102, 150, 150);
    }

    @Override
    protected boolean usingZippedTestArchive() {
        return true;
    }
}
