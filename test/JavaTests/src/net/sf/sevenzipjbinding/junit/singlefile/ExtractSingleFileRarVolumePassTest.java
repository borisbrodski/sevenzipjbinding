package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarVolumePassTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileRarVolumePassTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
        usingVolumes(true);
    }

}
