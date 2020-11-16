package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarVolumeHeaderPassCallbackTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileRarVolumeHeaderPassCallbackTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
    }

    @Before
    public void initExtractSingleFileRarVolumeHeaderPassCallbackTest() {
        usingPasswordCallback();
        usingVolumes(true);
    }
}
