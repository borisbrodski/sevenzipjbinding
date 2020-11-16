package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarPassCallbackTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileRarPassCallbackTest() {
        super(ArchiveFormat.RAR, 0, 2, 5);
    }

    @Before
    public void initExtractSingleFileRarPassCallbackTest() {
        usingPasswordCallback();
    }

}
