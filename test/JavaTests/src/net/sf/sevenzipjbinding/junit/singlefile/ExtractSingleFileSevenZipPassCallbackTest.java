package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipPassCallbackTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileSevenZipPassCallbackTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
        usingPasswordCallback();
    }
}
