package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileLzhTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileLzhTest() {
        super(ArchiveFormat.LZH, 5, 6, 7);
    }

    @Override
    @Test
    @Ignore
    public void test3Compression2() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void test3Compression2FormatAutodetect() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void test3Compression3() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void test3Compression3FormatAutodetect() throws Exception {
    }
}
