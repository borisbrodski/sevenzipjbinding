package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRar5PassCallbackTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileRar5PassCallbackTest() {
        super(ArchiveFormat.RAR5, "rar", 0, 2, 5);
        usingPasswordCallback();
    }

}
