package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileNsisTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileNsisTest() {
        super(ArchiveFormat.NSIS, "exe", 1, 2, 3);
    }

    @Override
    protected boolean skipSizeCheck() {
        return true;
    }

    @Override
    @Ignore
    @Test
    public void test1Compression2() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test1Compression2FormatAutodetect() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test2Compression2() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test2Compression2FormatAutodetect() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test3Compression2() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test3Compression2FormatAutodetect() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test4Compression2() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test4Compression2FormatAutodetect() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test5Compression2() throws Exception {
    }

    @Override
    @Ignore
    @Test
    public void test5Compression2FormatAutodetect() throws Exception {
    }

}
