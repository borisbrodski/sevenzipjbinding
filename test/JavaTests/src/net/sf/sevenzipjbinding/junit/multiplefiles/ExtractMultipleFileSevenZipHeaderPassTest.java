package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileSevenZipHeaderPassTest extends ExtractMultipleFileAbstractHeaderPassTest {

    public ExtractMultipleFileSevenZipHeaderPassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

}
