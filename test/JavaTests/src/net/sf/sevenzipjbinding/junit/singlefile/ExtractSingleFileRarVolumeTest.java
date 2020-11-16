package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarVolumeTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileRarVolumeTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
    }

    @Before
    public void initExtractSingleFileRarVolumeTest() {
        usingVolumes(true);
    }
}
