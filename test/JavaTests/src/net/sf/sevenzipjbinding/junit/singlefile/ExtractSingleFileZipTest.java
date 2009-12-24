package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileZipTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileZipTest() {
        super(ArchiveFormat.ZIP, 0, 5, 9);
    }

}
