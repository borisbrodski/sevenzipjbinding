package net.sf.sevenzipjbinding.junit.multiplefiles;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRarVolumeHeaderPassTest extends ExtractMultipleFileAbstractHeaderPassTest {
    public ExtractMultipleFileRarVolumeHeaderPassTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
    }

    @Before
    public void initExtractMultipleFileRarVolumeHeaderPassTest() {
        usingVolumes(true);
        usingPasswordCallback();
    }
}
