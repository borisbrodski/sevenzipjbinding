package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRar5HeaderPassTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileRar5HeaderPassTest() {
        super(ArchiveFormat.RAR5, "rar", 0, 2, 5);
    }

}
