package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileSevenZipVolumeTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileSevenZipVolumeTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);

        usingVolumedSevenZip();
    }

}
