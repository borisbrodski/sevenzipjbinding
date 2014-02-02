package net.sf.sevenzipjbinding.junit.bug;

import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import org.junit.Test;

public class RarPasswordToLongCrash {
    private static final String PASSWORD = "123456789012345678901234567890";

    @Test
    public void openArchiveWithCorrectPassword() throws Throwable {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;

        Throwable throwable = null;
        try {
            randomAccessFile = new RandomAccessFile("testdata/bug/RAR archive Crash (over 30 char password).rar", "r");
            inArchive = SevenZip.openInArchive(ArchiveFormat.RAR, new RandomAccessFileInStream(randomAccessFile),
                    PASSWORD);
        } catch (Throwable e) {
            throwable = e;
        }
        if (inArchive != null) {
            try {
                inArchive.close();
            } catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                }
            }
        }
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                }
            }
        }
        if (throwable != null) {
            throw throwable;
        }
    }
}
