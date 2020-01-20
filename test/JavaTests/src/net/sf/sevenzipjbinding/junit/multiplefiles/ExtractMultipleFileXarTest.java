package net.sf.sevenzipjbinding.junit.multiplefiles;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileXarTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileXarTest() {
        super(ArchiveFormat.XAR, 1, 2, 3);
    }

    @Override
    @Test
    @Ignore
    public void test1Compression3() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void test1Compression3FormatAutodetect() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void test2Compression3() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void test2Compression3FormatAutodetect() throws Exception {
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
