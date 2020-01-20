package net.sf.sevenzipjbinding.junit.multiplefiles;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRarVolumeTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileRarVolumeTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
    }

    @Before
    public void initExtractMultipleFileRarVolumeTest() {
        usingVolumes(true);
    }
}
