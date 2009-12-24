package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipHeaderPassTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileSevenZipHeaderPassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

}
