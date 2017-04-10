package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRar5HeaderPassTest extends ExtractMultipleFileAbstractHeaderPassTest {

    public ExtractMultipleFileRar5HeaderPassTest() {
        super(ArchiveFormat.RAR5, "rar", 0, 2, 5);
    }

}
