package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileLzmaTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileLzmaTest() {
        super(ArchiveFormat.LZMA, 1, 5, 9);
    }

}
