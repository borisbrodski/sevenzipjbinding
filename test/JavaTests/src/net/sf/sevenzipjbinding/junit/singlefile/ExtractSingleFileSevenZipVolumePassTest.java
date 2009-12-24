package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipVolumePassTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileSevenZipVolumePassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);

        usingVolumedSevenZip();
    }

}
