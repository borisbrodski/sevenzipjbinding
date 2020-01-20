package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipPassCallbackTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileSevenZipPassCallbackTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

    @Before
    public void initExtractSingleFileSevenZipPassCallbackTest() {
        usingPasswordCallback();
    }
}
