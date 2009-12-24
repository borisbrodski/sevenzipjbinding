package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileSevenZipVolumeHeaderPassTest extends ExtractMultipleFileAbstractHeaderPassTest {

    public ExtractMultipleFileSevenZipVolumeHeaderPassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);

        usingVolumedSevenZip();
    }

}
