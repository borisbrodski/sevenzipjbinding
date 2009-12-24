package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileSevenZipTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

}
