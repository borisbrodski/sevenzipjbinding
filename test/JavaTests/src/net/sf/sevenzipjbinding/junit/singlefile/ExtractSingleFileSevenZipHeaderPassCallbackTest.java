package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipHeaderPassCallbackTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileSevenZipHeaderPassCallbackTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

    @Before
    public void initExtractSingleFileSevenZipHeaderPassCallbackTest() {
        usingPasswordCallback();
    }
}
