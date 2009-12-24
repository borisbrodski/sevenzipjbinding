package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipVolumeHeaderPassTest extends ExtractSingleFileAbstractHeaderPassTest {

    public ExtractSingleFileSevenZipVolumeHeaderPassTest() {
        super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);

        usingVolumedSevenZip();
    }

}
