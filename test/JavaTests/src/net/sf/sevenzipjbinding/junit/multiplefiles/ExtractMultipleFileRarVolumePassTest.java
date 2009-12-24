package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRarVolumePassTest extends ExtractMultipleFileAbstractPassTest {

    public ExtractMultipleFileRarVolumePassTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
        usingVolumes(true);
    }

}
