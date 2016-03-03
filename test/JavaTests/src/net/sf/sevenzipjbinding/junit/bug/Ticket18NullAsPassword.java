package net.sf.sevenzipjbinding.junit.bug;

import static org.junit.Assert.fail;

import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import org.junit.Test;

public class Ticket18NullAsPassword {
    final static class OutStream implements ISequentialOutStream {
        public int write(byte[] data) throws SevenZipException {
            return data.length;
        }
    }

    final static class ExtractCallback implements IArchiveExtractCallback, ICryptoGetTextPassword {

        private ExtractOperationResult extractOperationResult;
        private String password;

        ExtractCallback(String password) {
            this.password = password;
        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
            return new OutStream();
        }

        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
        }

        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
            this.extractOperationResult = extractOperationResult;
        }

        public ExtractOperationResult getExtractOperationResult() {
            return extractOperationResult;
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
        }
    }

    static final class ArchiveOpenCryptoCallback implements IArchiveOpenCallback, ICryptoGetTextPassword {
        private final String passwordForOpen;

        public ArchiveOpenCryptoCallback(String passwordForOpen) {
            this.passwordForOpen = passwordForOpen;
        }

        public void setCompleted(Long files, Long bytes) {
        }

        public void setTotal(Long files, Long bytes) {
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return passwordForOpen;
        }
    }

    @Test(expected = SevenZipException.class)
    public void openPasswordIsNull() throws Throwable {
        openArchiveWithPassword(null, false);
        fail("Can open header crypted RAR archive without the password");
    }

    @Test(expected = SevenZipException.class)
    public void openPasswordIsNullWithCallback() throws Throwable {
        openArchiveWithPassword(null, true);
        fail("Can open header crypted RAR archive without the password");
    }

    @Test
    public void openArchiveWithCorrectPassword() throws Throwable {
        openArchiveWithPassword("TestPass", false);
    }

    @Test
    public void openArchiveWithCorrectPasswordWithCallback() throws Throwable {
        openArchiveWithPassword("TestPass", true);
    }

    @Test(expected = SevenZipException.class)
    public void extractPasswordIsNull() throws Throwable {
        extractArchiveWithPassword(null, false);
        fail("Can extract password protected RAR archive without the password");
    }

    @Test(expected = SevenZipException.class)
    public void extractPasswordIsNullWithCallback() throws Throwable {
        extractArchiveWithPassword(null, true);
        fail("Can extract password protected RAR archive without the password");
    }

    @Test
    public void extractWithCorrectPassword() throws Throwable {
        extractArchiveWithPassword("TestPass", false);
    }

    @Test
    public void extractWithCorrectPasswordWithCallback() throws Throwable {
        extractArchiveWithPassword("TestPass", true);
    }

    private void openArchiveWithPassword(String password, boolean withCallback) throws Throwable {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;

        Throwable throwable = null;
        try {
            randomAccessFile = new RandomAccessFile("testdata/bug/Ticket19-passh.rar", "r");
            if (withCallback) {
                inArchive = SevenZip.openInArchive(ArchiveFormat.RAR, new RandomAccessFileInStream(randomAccessFile),
                        new ArchiveOpenCryptoCallback(password));
            } else {
                inArchive = SevenZip.openInArchive(ArchiveFormat.RAR, new RandomAccessFileInStream(randomAccessFile),
                        password);
            }
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

    private void extractArchiveWithPassword(String password, boolean withCallback) throws Throwable {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;

        Throwable throwable = null;
        try {
            randomAccessFile = new RandomAccessFile("testdata/bug/Ticket19-pass.rar", "r");
            inArchive = SevenZip.openInArchive(ArchiveFormat.RAR, new RandomAccessFileInStream(randomAccessFile));

            ExtractOperationResult extractOperationResult;
            if (withCallback) {
                ExtractCallback extractCallback = new ExtractCallback(password);
                inArchive.extract(new int[] { 0 }, false, extractCallback);
                extractOperationResult = extractCallback.getExtractOperationResult();
            } else {
                extractOperationResult = inArchive.extractSlow(0, new OutStream(), password);
            }
            if (extractOperationResult != ExtractOperationResult.OK) {
                throw new SevenZipException("Extraction failed: " + extractOperationResult);
            }
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
