package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarVolumePassCallbackTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileRarVolumePassCallbackTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
    }

    @Before
    public void initExtractSingleFileRarVolumePassCallbackTest() {
        usingPasswordCallback();
        usingVolumes(true);
    }
}
