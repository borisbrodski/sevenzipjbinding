package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

public abstract class ExtractSingleFileAbstractHeaderPassTest extends ExtractSingleFileAbstractPassTest {

    public ExtractSingleFileAbstractHeaderPassTest(ArchiveFormat archiveFormat, int compression1, int compression2,
            int compression3) {
        super(archiveFormat, compression1, compression2, compression3);
    }

    public ExtractSingleFileAbstractHeaderPassTest(ArchiveFormat archiveFormat, String extention, int compression1,
            int compression2, int compression3) {
        super(archiveFormat, extention, compression1, compression2, compression3);
    }

    @Before
    public void initExtractSingleFileAbstractHeaderPassTest() {
        usingHeaderPassword();
        setCryptedArchivePrefix("passh-");
    }

    @Test(expected = SevenZipException.class)
    @Multithreaded
    @Repeat
    public void test1Compression1WithoutHeaderPassword() throws Exception {
        expectException(SevenZipException.class);
        usingHeaderPassword(false);
        usingPassword();
        test1Compression1();
    }

    @Test(expected = SevenZipException.class)
    @Multithreaded
    @Repeat
    public void test1Compression1WithoutHeaderPasswordFormatAutodetect() throws Exception {
        expectException(SevenZipException.class);
        usingHeaderPassword(false);
        usingPassword();
        test1Compression1FormatAutodetect();
    }


}
