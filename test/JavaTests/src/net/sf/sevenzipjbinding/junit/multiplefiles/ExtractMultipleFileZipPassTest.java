package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileZipPassTest extends ExtractMultipleFileAbstractPassTest {

    public ExtractMultipleFileZipPassTest() {
        super(ArchiveFormat.ZIP, 0, 5, 9);
    }

}
