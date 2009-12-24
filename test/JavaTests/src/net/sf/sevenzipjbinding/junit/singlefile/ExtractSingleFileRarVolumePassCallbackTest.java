package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarVolumePassCallbackTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileRarVolumePassCallbackTest() {
        super(ArchiveFormat.RAR, "part1.rar", 0, 2, 5);
        usingPasswordCallback();
        usingVolumes(true);
    }

}
