package net.sf.sevenzipjbinding.junit.multiplefiles;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileSevenZipVolumePassTest extends ExtractMultipleFileAbstractPassTest {

    public ExtractMultipleFileSevenZipVolumePassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

    @Before
    public void initExtractMultipleFileSevenZipVolumePassTest() {
        usingVolumedSevenZip();
    }
}
