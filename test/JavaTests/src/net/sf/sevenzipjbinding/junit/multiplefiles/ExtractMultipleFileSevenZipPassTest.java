package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileSevenZipPassTest extends ExtractMultipleFileAbstractPassTest {

    public ExtractMultipleFileSevenZipPassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

}
