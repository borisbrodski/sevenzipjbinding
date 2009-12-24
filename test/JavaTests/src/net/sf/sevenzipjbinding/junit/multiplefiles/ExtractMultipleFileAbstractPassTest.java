package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

import org.junit.Test;

public abstract class ExtractMultipleFileAbstractPassTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileAbstractPassTest(ArchiveFormat archiveFormat, int compression1, int compression2,
            int compression3) {
        super(archiveFormat, compression1, compression2, compression3);
        init();
    }

    private void init() {
        usingPassword();
        setCryptedArchivePrefix("pass-");
    }

    public ExtractMultipleFileAbstractPassTest(ArchiveFormat archiveFormat, String extention, int compression1,
            int compression2, int compression3) {
        super(archiveFormat, extention, compression1, compression2, compression3);
        init();
    }

    @Test(expected = Exception.class)
    public void test1Compression1WithoutPassword() throws Exception {
        expectException(Exception.class);
        usingPassword(false);
        test1Compression1();
    }

    @Test(expected = Exception.class)
    public void test1Compression1WithoutPasswordFormatAutodetect() throws Exception {
        expectException(Exception.class);
        usingPassword(false);
        test1Compression1FormatAutodetect();
    }

    @Test(expected = Exception.class)
    public void test1Compression1WithoutPasswordMultithreaded() throws Exception {
        expectException(Exception.class);
        usingPassword(false);
        test1Compression1Multithreaded();
    }

    @Test(expected = Exception.class)
    public void test1Compression1WithoutPasswordFormatAutodetectMultithreaded() throws Exception {
        expectException(Exception.class);
        usingPassword(false);
        test1Compression1FormatAutodetectMultithreaded();
    }

    @Test(expected = Exception.class)
    public void test1Compression1WithWrongPassword() throws Exception {
        expectException(Exception.class);
        setPasswordToUse("12345");
        test1Compression1();
    }

    @Test(expected = Exception.class)
    public void test1Compression1WithWrongPasswordFormatAutodetect() throws Exception {
        expectException(Exception.class);
        setPasswordToUse("12345");
        test1Compression1FormatAutodetect();
    }

    @Test(expected = Exception.class)
    public void test1Compression1WithWrongPasswordMultithreaded() throws Exception {
        expectException(Exception.class);
        setPasswordToUse("12345");
        test1Compression1Multithreaded();
    }

    @Test(expected = Exception.class)
    public void test1Compression1WithWrongPasswordFormatAutodetectMultithreaded() throws Exception {
        expectException(Exception.class);
        setPasswordToUse("12345");
        test1Compression1FormatAutodetectMultithreaded();
    }
}
