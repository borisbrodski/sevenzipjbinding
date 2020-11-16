package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRar5PassTest extends ExtractMultipleFileAbstractPassTest {

    public ExtractMultipleFileRar5PassTest() {
        super(ArchiveFormat.RAR5, "rar", 0, 2, 5);
    }

}
