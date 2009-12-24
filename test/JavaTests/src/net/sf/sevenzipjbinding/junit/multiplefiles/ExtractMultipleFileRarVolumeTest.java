package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRarVolumeTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileRarVolumeTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);

        usingVolumes(true);
    }

}
