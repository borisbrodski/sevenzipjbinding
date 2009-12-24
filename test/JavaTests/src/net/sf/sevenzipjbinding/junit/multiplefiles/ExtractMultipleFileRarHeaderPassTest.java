package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRarHeaderPassTest extends ExtractMultipleFileAbstractHeaderPassTest {

    public ExtractMultipleFileRarHeaderPassTest() {
        super(ArchiveFormat.RAR, 0, 2, 5);
    }

}
