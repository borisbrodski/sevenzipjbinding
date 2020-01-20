package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarVolumePassTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileRarVolumePassTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
    }

    @Before
    public void initExtractSingleFileRarVolumePassTest() {
        usingVolumes(true);
    }
}
