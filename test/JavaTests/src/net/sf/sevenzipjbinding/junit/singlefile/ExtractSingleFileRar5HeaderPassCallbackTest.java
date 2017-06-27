package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRar5HeaderPassCallbackTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileRar5HeaderPassCallbackTest() {
        super(ArchiveFormat.RAR5, "rar", 0, 2, 5);
    }

    @Before
    public void initExtractSingleFileRar5HeaderPassCallbackTest() {
        usingPasswordCallback();
    }

}
