package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarHeaderPassCallbackTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileRarHeaderPassCallbackTest() {
        super(ArchiveFormat.RAR, 0, 2, 5);
    }

    @Before
    public void initExtractSingleFileRarHeaderPassCallbackTest() {
        usingPasswordCallback();
    }
}
