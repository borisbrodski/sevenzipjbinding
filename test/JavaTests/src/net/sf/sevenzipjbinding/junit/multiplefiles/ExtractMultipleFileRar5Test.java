package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRar5Test extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileRar5Test() {
        super(ArchiveFormat.RAR5, "rar", 0, 2, 5);
    }

}
