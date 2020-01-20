package net.sf.sevenzipjbinding.junit.multiplefiles;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileHfsTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileHfsTest() {
        super(ArchiveFormat.HFS, 1, -1, -1);
    }

    @Override
    public String getRemoveFilenamePrefix() {
        return "7-zip-test/";
    }

    @Override
    protected boolean usingZippedTestArchive() {
        return true;
    }

    @Ignore
    @Test
    @Override
    public void test1Compression2() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test1Compression2FormatAutodetect() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test1Compression3() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test1Compression3FormatAutodetect() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test2Compression2() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test2Compression2FormatAutodetect() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test2Compression3() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test2Compression3FormatAutodetect() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test3Compression2() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test3Compression2FormatAutodetect() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test3Compression3() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test3Compression3FormatAutodetect() throws Exception {
    }

}
