package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRarTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileRarTest() {
        super(ArchiveFormat.RAR, 0, 2, 5);
    }

}
