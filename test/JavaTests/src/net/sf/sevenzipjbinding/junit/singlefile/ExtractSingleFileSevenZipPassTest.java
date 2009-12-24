package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipPassTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileSevenZipPassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

}
