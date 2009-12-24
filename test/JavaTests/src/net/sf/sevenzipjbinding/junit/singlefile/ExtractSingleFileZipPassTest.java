package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileZipPassTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileZipPassTest() {
        super(ArchiveFormat.ZIP, 0, 5, 9);
    }

}
