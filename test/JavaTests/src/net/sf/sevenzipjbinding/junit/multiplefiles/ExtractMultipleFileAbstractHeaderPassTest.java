package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.Test;

/**
 * Abstract class for all extract multiple file archive tests using header pass.
 * 
 * @author Boris Brodski
 * @since 4.65-1
 */
public abstract class ExtractMultipleFileAbstractHeaderPassTest extends ExtractMultipleFileAbstractPassTest {

    public ExtractMultipleFileAbstractHeaderPassTest(ArchiveFormat archiveFormat, int compression1, int compression2,
            int compression3) {
        super(archiveFormat, compression1, compression2, compression3);
        init();
    }

    public ExtractMultipleFileAbstractHeaderPassTest(ArchiveFormat archiveFormat, String extention, int compression1,
            int compression2, int compression3) {
        super(archiveFormat, extention, compression1, compression2, compression3);
        init();
    }

    private void init() {
        usingHeaderPassword();
        setCryptedArchivePrefix("passh-");
    }

    @Test(expected = SevenZipException.class)
    public void test1Compression1WithoutHeaderPassword() throws Exception {
        expectException(SevenZipException.class);
        usingHeaderPassword(false);
        usingPassword();
        test1Compression1();
    }

    @Test(expected = SevenZipException.class)
    public void test1Compression1WithoutHeaderPasswordFormatAutodetect() throws Exception {
        expectException(SevenZipException.class);
        usingHeaderPassword(false);
        usingPassword();
        test1Compression1FormatAutodetect();
    }

    @Test(expected = SevenZipException.class)
    public void test1Compression1WithoutHeaderPasswordMultithreaded() throws Exception {
        expectException(SevenZipException.class);
        usingHeaderPassword(false);
        usingPassword();
        test1Compression1Multithreaded();
    }

    @Test(expected = SevenZipException.class)
    public void test1Compression1WithoutHeaderPasswordFormatAutodetectMultithreaded() throws Exception {
        expectException(SevenZipException.class);
        usingHeaderPassword(false);
        usingPassword();
        test1Compression1FormatAutodetectMultithreaded();
    }
}
