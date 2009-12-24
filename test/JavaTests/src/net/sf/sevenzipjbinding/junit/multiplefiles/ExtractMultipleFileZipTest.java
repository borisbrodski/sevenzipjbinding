package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileZipTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileZipTest() {
        super(ArchiveFormat.ZIP, 0, 5, 9);
    }

}
