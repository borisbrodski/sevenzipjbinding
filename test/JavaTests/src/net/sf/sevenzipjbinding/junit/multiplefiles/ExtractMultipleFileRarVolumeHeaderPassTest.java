package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRarVolumeHeaderPassTest extends ExtractMultipleFileAbstractHeaderPassTest {
    public ExtractMultipleFileRarVolumeHeaderPassTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
        usingVolumes(true);
        usingPasswordCallback();
    }
}
