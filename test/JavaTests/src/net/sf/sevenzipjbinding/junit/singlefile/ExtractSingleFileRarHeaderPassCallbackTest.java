package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarHeaderPassCallbackTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileRarHeaderPassCallbackTest() {
        super(ArchiveFormat.RAR, 0, 2, 5);
        usingPasswordCallback();
    }

}
