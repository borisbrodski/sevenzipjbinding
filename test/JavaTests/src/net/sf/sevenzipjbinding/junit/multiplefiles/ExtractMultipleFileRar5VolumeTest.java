package net.sf.sevenzipjbinding.junit.multiplefiles;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileRar5VolumeTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileRar5VolumeTest() {
        super(ArchiveFormat.RAR5, "part001.rar", 0, 2, 5);
    }

    @Before
    public void initExtractMultipleFileRar5VolumeTest() {
        usingVolumes(true);
    }

    @Ignore
    @Test
    @Override
    public void test2Compression1() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test2Compression1FormatAutodetect() throws Exception {
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
    public void test3Compression1() throws Exception {
    }

    @Ignore
    @Test
    @Override
    public void test3Compression1FormatAutodetect() throws Exception {
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
