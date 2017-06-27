package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipVolumePassTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileSevenZipVolumePassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
    }

    @Before
    public void initExtractSingleFileSevenZipVolumePassTest() {
        usingVolumedSevenZip();
    }
}
