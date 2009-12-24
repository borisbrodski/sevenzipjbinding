package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRarPassTest extends ExtractMultipleFileAbstractPassTest {

    public ExtractMultipleFileRarPassTest() {
        super(ArchiveFormat.RAR, 0, 2, 5);
    }

}
