package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileZipPassCallbackTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileZipPassCallbackTest() {
        super(ArchiveFormat.ZIP, 0, 5, 9);
    }

    @Before
    public void initExtractSingleFileZipPassCallbackTest() {
        usingPasswordCallback();
    }
}
