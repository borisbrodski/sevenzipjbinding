package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarHeaderPassTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileRarHeaderPassTest() {
        super(ArchiveFormat.RAR, 0, 2, 5);
    }

}
