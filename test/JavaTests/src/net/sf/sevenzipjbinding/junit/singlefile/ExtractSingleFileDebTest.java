package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileDebTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileDebTest() {
        super(ArchiveFormat.AR, "deb", 1, 2, 3);
    }

}
