package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRpmTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileRpmTest() {
        super(ArchiveFormat.RPM, 0, 0, 0);
    }

    @Override
    protected boolean skipSizeCheck() {
        return true;
    }

    @Override
    protected String getUncompressedFilename(int fileIndex) {
        return "rpm-simple" + fileIndex + ".cpio.gz";
    };

}
