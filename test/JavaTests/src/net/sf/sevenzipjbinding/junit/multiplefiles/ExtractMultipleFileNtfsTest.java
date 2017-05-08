package net.sf.sevenzipjbinding.junit.multiplefiles;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileNtfsTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileNtfsTest() {
        super(ArchiveFormat.NTFS, 1, -1, -1);
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
