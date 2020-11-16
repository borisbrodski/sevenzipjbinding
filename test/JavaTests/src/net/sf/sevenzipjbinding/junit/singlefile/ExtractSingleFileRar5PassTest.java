package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRar5PassTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileRar5PassTest() {
        super(ArchiveFormat.RAR5, "rar", 0, 2, 5);
    }

}
