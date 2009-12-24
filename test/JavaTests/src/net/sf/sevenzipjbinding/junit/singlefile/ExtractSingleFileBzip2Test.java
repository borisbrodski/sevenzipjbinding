package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileBzip2Test extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileBzip2Test() {
        super(ArchiveFormat.BZIP2, "bz2", 1, 5, 9);
    }

}
