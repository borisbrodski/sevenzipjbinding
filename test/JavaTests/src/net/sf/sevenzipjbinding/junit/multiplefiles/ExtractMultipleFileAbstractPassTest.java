package net.sf.sevenzipjbinding.junit.multiplefiles;

import org.junit.Before;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

public abstract class ExtractMultipleFileAbstractPassTest extends ExtractMultipleFileAbstractTest {

    public ExtractMultipleFileAbstractPassTest(ArchiveFormat archiveFormat, int compression1, int compression2,
            int compression3) {
        super(archiveFormat, compression1, compression2, compression3);
    }

    public ExtractMultipleFileAbstractPassTest(ArchiveFormat archiveFormat, String extention, int compression1,
            int compression2, int compression3) {
        super(archiveFormat, extention, compression1, compression2, compression3);
    }

    @Before
    public void initExtractMultipleFileAbstractPassTest() {
        usingPassword();
        setCryptedArchivePrefix("pass-");
    }

    @Test(expected = Exception.class)
    @Multithreaded
    @Repeat
    public void test1Compression1WithoutPassword() throws Exception {
        expectException(Exception.class);
        usingPassword(false);
        test1Compression1();
    }

    @Test(expected = Exception.class)
    @Multithreaded
    @Repeat
    public void test1Compression1WithoutPasswordFormatAutodetect() throws Exception {
        expectException(Exception.class);
        usingPassword(false);
        test1Compression1FormatAutodetect();
    }

    @Test(expected = Exception.class)
    @Multithreaded
    @Repeat
    public void test1Compression1WithWrongPassword() throws Exception {
        expectException(Exception.class);
        setPasswordToUse("12345");
        test1Compression1();
    }

    @Test(expected = Exception.class)
    @Multithreaded
    @Repeat
    public void test1Compression1WithWrongPasswordFormatAutodetect() throws Exception {
        expectException(Exception.class);
        setPasswordToUse("12345");
        test1Compression1FormatAutodetect();
    }

}