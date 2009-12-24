package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileSevenZipTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileSevenZipTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

}
