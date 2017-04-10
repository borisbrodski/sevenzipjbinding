package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.PropertyInfo;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.VolumedArchiveInStream;
import net.sf.sevenzipjbinding.junit.tools.ZipInStream;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract class for all mass archive extraction tests.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public abstract class ExtractFileAbstractTest extends JUnitNativeTestBase {
    private static final String DEFAULT_PASSWORD = "TestPass";

    protected final ArchiveFormat archiveFormat;
    protected final int compression1;
    protected final int compression2;
    protected final int compression3;
    protected final String extention;
    protected String passwordToUse;
    protected boolean usingPassword = false;
    protected String generalPrefix = "";
    protected String cryptedArchivePrefix = "";
    protected String volumedArchivePrefix = "";
    protected String volumeArchivePostfix = "";
    protected boolean usingHeaderPassword = false;
    protected boolean usingPasswordCallback = false;
    protected boolean usingVolumes = false;
    protected Class<? extends Exception> exceptionToBeExpected = null;

    public ExtractFileAbstractTest(ArchiveFormat archiveFormat, String extention, int compression1, int compression2,
            int compression3) {
        this.archiveFormat = archiveFormat;
        this.extention = extention;
        this.compression1 = compression1;
        this.compression2 = compression2;
        this.compression3 = compression3;
    }

    public ExtractFileAbstractTest(ArchiveFormat archiveFormat, int compression1, int compression2, int compression3) {
        this(archiveFormat, archiveFormat.toString().toLowerCase(), compression1, compression2, compression3);
    }

    protected void usingPassword(boolean using) {
        if (using) {
            usingPassword();
        } else {
            usingPassword = false;
        }
    }

    protected void expectException(Class<? extends Exception> exceptionClass) {
        exceptionToBeExpected = exceptionClass;
    }

    protected void setGeneralPrefix(String generalPrefix) {
        this.generalPrefix = generalPrefix;
    }

    protected void setCryptedArchivePrefix(String cryptedArchivePrefix) {
        this.cryptedArchivePrefix = cryptedArchivePrefix;
    }

    protected void usingPassword() {
        usingPassword = true;
    }

    protected void usingPasswordCallback() {
        usingPasswordCallback = true;
    }

    protected void setPasswordToUse(String password) {
        passwordToUse = password;
    }

    protected void usingHeaderPassword() {
        usingHeaderPassword(true);
    }

    protected void usingHeaderPassword(boolean using) {
        usingHeaderPassword = using;
    }

    protected void usingVolumes(boolean usingVolumes) {
        this.usingVolumes = usingVolumes;
        if (usingVolumes) {
            setVolumedArchivePrefix("vol-");
        } else {
            setVolumedArchivePrefix("");
        }

    }

    public void setVolumedArchivePrefix(String volumedArchivePrefix) {
        this.volumedArchivePrefix = volumedArchivePrefix;
    }

    public void setVolumeArchivePostfix(String volumeArchivePostfix) {
        this.volumeArchivePostfix = volumeArchivePostfix;
    }

    final protected void usingVolumedSevenZip() {
        usingVolumes(true);
        setVolumeArchivePostfix(".001");
    }

    @Before
    public void initPasswordToUse() {
        passwordToUse = DEFAULT_PASSWORD;
    }

    @Test
    public void test1Compression1() throws Exception {
        testArchiveExtraction(1, compression1, false, false);
    }

    @Test
    public void test1Compression1Multithreaded() throws Exception {
        testArchiveExtraction(1, compression1, false, true);
    }

    @Test
    public void test1Compression1FormatAutodetect() throws Exception {
        testArchiveExtraction(1, compression1, true, false);
    }

    @Test
    public void test1Compression1FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(1, compression1, true, true);
    }

    @Test
    public void test1Compression2() throws Exception {
        testArchiveExtraction(1, compression2, false, false);
    }

    @Test
    public void test1Compression2Multithreaded() throws Exception {
        testArchiveExtraction(1, compression2, false, true);
    }

    @Test
    public void test1Compression2FormatAutodetect() throws Exception {
        testArchiveExtraction(1, compression2, true, false);
    }

    @Test
    public void test1Compression2FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(1, compression2, true, true);
    }

    @Test
    public void test1Compression3() throws Exception {
        testArchiveExtraction(1, compression3, false, false);
    }

    @Test
    public void test1Compression3Multithreaded() throws Exception {
        testArchiveExtraction(1, compression3, false, true);
    }

    @Test
    public void test1Compression3FormatAutodetect() throws Exception {
        testArchiveExtraction(1, compression3, true, false);
    }

    @Test
    public void test1Compression3FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(1, compression3, true, true);
    }

    @Test
    public void test2Compression1() throws Exception {
        testArchiveExtraction(2, compression1, false, false);
    }

    @Test
    public void test2Compression1Multithreaded() throws Exception {
        testArchiveExtraction(2, compression1, false, true);
    }

    @Test
    public void test2Compression1FormatAutodetect() throws Exception {
        testArchiveExtraction(2, compression1, true, false);
    }

    @Test
    public void test2Compression1FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(2, compression1, true, true);
    }

    @Test
    public void test2Compression2() throws Exception {
        testArchiveExtraction(2, compression2, false, false);
    }

    @Test
    public void test2Compression2Multithreaded() throws Exception {
        testArchiveExtraction(2, compression2, false, true);
    }

    @Test
    public void test2Compression2FormatAutodetect() throws Exception {
        testArchiveExtraction(2, compression2, true, false);
    }

    @Test
    public void test2Compression2FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(2, compression2, true, true);
    }

    @Test
    public void test2Compression3() throws Exception {
        testArchiveExtraction(2, compression3, false, false);
    }

    @Test
    public void test2Compression3Multithreaded() throws Exception {
        testArchiveExtraction(2, compression3, false, true);
    }

    @Test
    public void test2Compression3FormatAutodetect() throws Exception {
        testArchiveExtraction(2, compression3, true, false);
    }

    @Test
    public void test2Compression3FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(2, compression3, true, true);
    }

    @Test
    public void test3Compression1() throws Exception {
        testArchiveExtraction(3, compression1, false, false);
    }

    @Test
    public void test3Compression1Multithreaded() throws Exception {
        testArchiveExtraction(3, compression1, false, true);
    }

    @Test
    public void test3Compression1FormatAutodetect() throws Exception {
        testArchiveExtraction(3, compression1, true, false);
    }

    @Test
    public void test3Compression1FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(3, compression1, true, true);
    }

    @Test
    public void test3Compression2() throws Exception {
        testArchiveExtraction(3, compression2, false, false);
    }

    @Test
    public void test3Compression2Multithreaded() throws Exception {
        testArchiveExtraction(3, compression2, false, true);
    }

    @Test
    public void test3Compression2FormatAutodetect() throws Exception {
        testArchiveExtraction(3, compression2, true, false);
    }

    @Test
    public void test3Compression2FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(3, compression2, true, true);
    }

    @Test
    public void test3Compression3() throws Exception {
        testArchiveExtraction(3, compression3, false, false);
    }

    @Test
    public void test3Compression3Multithreaded() throws Exception {
        testArchiveExtraction(3, compression3, false, true);
    }

    @Test
    public void test3Compression3FormatAutodetect() throws Exception {
        testArchiveExtraction(3, compression3, true, false);
    }

    @Test
    public void test3Compression3FormatAutodetectMultithreaded() throws Exception {
        testArchiveExtraction(3, compression3, true, true);
    }

    protected void testArchiveExtraction(final int fileIndex, final int compressionIndex,
            final boolean autodetectFormat, boolean multithreaded) throws Exception {
        if (multithreaded) {
            runMultithreaded(new RunnableThrowsException() {
                public void run() throws Exception {
                    testArchiveExtraction(fileIndex, compressionIndex, autodetectFormat, false);
                }
            }, exceptionToBeExpected);
        } else {
            for (int i = 0; i < SINGLE_TEST_REPEAT_COUNT; i++) {
                doTestArchiveExtraction(fileIndex, compressionIndex, autodetectFormat);
            }
        }
    }

    protected abstract void doTestArchiveExtraction(int fileIndex, int compressionIndex, boolean autodetectFormat)
            throws Exception;

    protected boolean usingZippedTestArchive() {
        return false;
    }

    protected abstract String getTestDataPath();

    protected String getTestSubdir() {
        return archiveFormat.toString().toLowerCase();
    }

    protected class ExtractionInArchiveTestHelper {
        private IInStream randomAccessFileInStream;
        private VolumeArchiveOpenCallback volumeArchiveOpenCallback;

        public ExtractionInArchiveTestHelper() {

        }

        public IInArchive openArchiveFileWithSevenZip(int fileIndex, int compressionIndex,
                boolean autodetectFormat, String testFileNameWE, String testFileExt) throws SevenZipException {
            String archiveFilename = getTestDataPath() + File.separatorChar + getTestSubdir() + File.separatorChar
                    + generalPrefix + volumedArchivePrefix + cryptedArchivePrefix + testFileNameWE + fileIndex + "."
                    + testFileExt
                    + "." + compressionIndex + "." + extention + volumeArchivePostfix;

            if (!new File(archiveFilename).exists() && extention.contains("part1.rar")) {
                archiveFilename = archiveFilename.replace("part1.rar", "part01.rar");
                if (!new File(archiveFilename).exists()) {
                    archiveFilename = archiveFilename.replace("part01.rar", "rar");
                }
            }

            return openArchiveFileWithSevenZip(archiveFilename, autodetectFormat);
        }

        private IInArchive openArchiveFileWithSevenZip(String archiveFilename, boolean autodetectFormat)
                throws SevenZipException {
            randomAccessFileInStream = null;
            IInArchive inArchive = null;
            RandomAccessFile randomAccessFile = null;
            try {
                if (usingZippedTestArchive()) {
                    ZipFile zipFile = new ZipFile(new File(archiveFilename + ".zip"));
                    randomAccessFileInStream = new ZipInStream(zipFile, zipFile.entries().nextElement());
                } else {
                    randomAccessFile = new RandomAccessFile(archiveFilename, "r");
                    randomAccessFileInStream = new RandomAccessFileInStream(randomAccessFile);
                }
                volumeArchiveOpenCallback = null;
                VolumedArchiveInStream volumedArchiveInStream;
                IInStream inStreamToUse = randomAccessFileInStream;
                IArchiveOpenCallback archiveOpenCallbackToUse = null;

                if (usingVolumes) {
					volumeArchiveOpenCallback = new VolumeArchiveOpenCallback(archiveFilename, getTestDataPath()
							+ File.separatorChar + getTestSubdir());
                    if (archiveFormat == ArchiveFormat.SEVEN_ZIP) {
                        volumedArchiveInStream = new VolumedArchiveInStream(archiveFilename, volumeArchiveOpenCallback);
                        inStreamToUse = volumedArchiveInStream;
                    } else {
                        archiveOpenCallbackToUse = volumeArchiveOpenCallback;
                    }
                }

                if (usingHeaderPassword) {
                    if (usingPasswordCallback) {
                        if (usingVolumes) {
                            inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse,
                                    new CombinedArchiveOpenCallback(archiveOpenCallbackToUse,
                                            new PasswordArchiveOpenCallback(), volumeArchiveOpenCallback));
                        } else {
                            inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse,
                                    new PasswordArchiveOpenCallback());

                        }
                    } else {
                        inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse,
                                passwordToUse);
                    }
                } else {
                    if (archiveOpenCallbackToUse == null) {
                        inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse);
                    } else {
                        inArchive = SevenZip.openInArchive(autodetectFormat ? null : archiveFormat, inStreamToUse,
                                archiveOpenCallbackToUse);
                    }
                }
            } catch (Throwable exception) {
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException e) {
                    }
                    randomAccessFileInStream = null;
                }

                if (randomAccessFileInStream instanceof ZipInStream) {
                    try {
                        ((ZipInStream) randomAccessFileInStream).close();
                    } catch (IOException e) {
                    }
                    randomAccessFileInStream = null;
                }
                if (exception instanceof SevenZipException) {
                    throw (SevenZipException) exception;
                }
                throw new RuntimeException(exception);
            }

            assertEquals(archiveFormat, inArchive.getArchiveFormat());
            return inArchive;
        }

        public void closeAllStreams() {
            if (randomAccessFileInStream != null) {
                try {
                    if (randomAccessFileInStream instanceof RandomAccessFileInStream) {
                        ((RandomAccessFileInStream) randomAccessFileInStream).close();
                    } else if (randomAccessFileInStream instanceof ZipInStream) {
                        ((ZipInStream) randomAccessFileInStream).close();
                    } else {
                        throw new IllegalStateException("Unknown IInStream implementation: "
                                + randomAccessFileInStream.getClass().getCanonicalName());
                    }
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
            if (volumeArchiveOpenCallback != null) {
                try {
                    volumeArchiveOpenCallback.close();
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }
    }

    public class CombinedArchiveOpenCallback implements IArchiveOpenCallback, ICryptoGetTextPassword,
            IArchiveOpenVolumeCallback {
        private final ICryptoGetTextPassword cryptoGetTextPassword;
        private final IArchiveOpenVolumeCallback archiveOpenVolumeCallback;
        private final IArchiveOpenCallback archiveOpenCallback;

        public CombinedArchiveOpenCallback(IArchiveOpenCallback archiveOpenCallback,
                ICryptoGetTextPassword cryptoGetTextPassword, IArchiveOpenVolumeCallback archiveOpenVolumeCallback) {
            this.archiveOpenCallback = archiveOpenCallback;
            this.cryptoGetTextPassword = cryptoGetTextPassword;
            this.archiveOpenVolumeCallback = archiveOpenVolumeCallback;

        }

        public void setCompleted(Long files, Long bytes) throws SevenZipException {
            archiveOpenCallback.setCompleted(files, bytes);
        }

        public void setTotal(Long files, Long bytes) throws SevenZipException {
            archiveOpenCallback.setTotal(files, bytes);
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return cryptoGetTextPassword.cryptoGetTextPassword();
        }

        public Object getProperty(PropID propID) throws SevenZipException {
            return archiveOpenVolumeCallback.getProperty(propID);
        }

        public IInStream getStream(String filename) throws SevenZipException {
            return archiveOpenVolumeCallback.getStream(filename);
        }

    }

    public class PasswordArchiveOpenCallback implements IArchiveOpenCallback, ICryptoGetTextPassword {

        /**
         * {@inheritDoc}
         */

        public void setCompleted(Long files, Long bytes) {
        }

        /**
         * {@inheritDoc}
         */

        public void setTotal(Long files, Long bytes) {
        }

        /**
         * {@inheritDoc}
         */

        public String cryptoGetTextPassword() throws SevenZipException {
            return passwordToUse;
        }

    }

    public class PasswordArchiveExtractCallback implements IArchiveExtractCallback, ICryptoGetTextPassword {

        private final ISequentialOutStream outputStream;
        private ExtractOperationResult extractOperationResult;

        public PasswordArchiveExtractCallback(ISequentialOutStream outputStream) {
            this.outputStream = outputStream;
        }

        /**
         * {@inheritDoc}
         */

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
            return outputStream;
        }

        /**
         * {@inheritDoc}
         */

        public void prepareOperation(ExtractAskMode extractAskMode) {
        }

        /**
         * {@inheritDoc}
         */

        public void setOperationResult(ExtractOperationResult extractOperationResult) {
            this.extractOperationResult = extractOperationResult;
        }

        /**
         * {@inheritDoc}
         */

        public void setCompleted(long completeValue) {
        }

        /**
         * {@inheritDoc}
         */

        public void setTotal(long total) {
        }

        /**
         * {@inheritDoc}
         */

        public String cryptoGetTextPassword() throws SevenZipException {
            return passwordToUse;
        }

        public ExtractOperationResult getExtractOperationResult() {
            return extractOperationResult;
        }
    }

    public static class ExtractOperationResultException extends SevenZipException {
        private static final long serialVersionUID = 42L;

        public ExtractOperationResultException(ExtractOperationResult extractOperationResult) {
            super("Extract operation returns error: " + extractOperationResult);
        }
    }

    public static class VolumeArchiveOpenCallback implements IArchiveOpenCallback, IArchiveOpenVolumeCallback,
            ICryptoGetTextPassword {

        private RandomAccessFile randomAccessFile;
        private String currentFilename;
        private Map<String, RandomAccessFile> randomAccessFileMap = new HashMap<String, RandomAccessFile>();
		private final String currentDir;

		public VolumeArchiveOpenCallback(String firstFilename, String currentDir) {
            currentFilename = firstFilename;
			this.currentDir = currentDir;
        }

        /**
         * ${@inheritDoc}
         */
        public Object getProperty(PropID propID) {
            switch (propID) {
            case NAME:
                return currentFilename;
            }
            return null;
        }

        /**
         * ${@inheritDoc}
         */

        public IInStream getStream(String filename) {
            try {
				String fullfilename = new File(currentDir, new File(filename).getName()).getCanonicalPath();
				currentFilename = fullfilename;
				randomAccessFile = randomAccessFileMap.get(fullfilename);
                if (randomAccessFile != null) {
                    randomAccessFile.seek(0);
                    return new RandomAccessFileInStream(randomAccessFile);
                }
				randomAccessFile = new RandomAccessFile(fullfilename, "r");
				randomAccessFileMap.put(fullfilename, randomAccessFile);
                return new RandomAccessFileInStream(randomAccessFile);
            } catch (FileNotFoundException fileNotFoundException) {
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void close() throws IOException {
            for (RandomAccessFile file : randomAccessFileMap.values()) {
                file.close();
            }
        }

        /**
         * ${@inheritDoc}
         */

        public void setCompleted(Long files, Long bytes) {
        }

        /**
         * ${@inheritDoc}
         */

        public void setTotal(Long files, Long bytes) {
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return "a";
        }
    }

    protected final void checkArchiveGeneric(IInArchive inArchive) throws SevenZipException {
        int numberOfArchiveProperties = inArchive.getNumberOfArchiveProperties();
        for (int propertyIndex = 0; propertyIndex < numberOfArchiveProperties; propertyIndex++) {
            PropertyInfo propertyInfo = inArchive.getArchivePropertyInfo(propertyIndex);
            assertNotNull(propertyInfo);
            assertNotNull(propertyInfo.propID);
            inArchive.getArchiveProperty(propertyInfo.propID);

            // System.out.println(propertyInfo + ",  Value: " + inArchive.getArchiveProperty(propertyInfo.propID));
        }
        int numberOfProperties = inArchive.getNumberOfProperties();
        int numberOfItems = inArchive.getNumberOfItems();
        for (int itemIndex = 0; itemIndex < numberOfItems; itemIndex++) {
            for (int propertyIndex = 0; propertyIndex < numberOfProperties; propertyIndex++) {
                PropertyInfo propertyInfo = inArchive.getPropertyInfo(propertyIndex);
                assertNotNull(propertyInfo);
                assertNotNull(propertyInfo.propID);
                inArchive.getProperty(itemIndex, propertyInfo.propID);

                // System.out.println(propertyInfo + ",  Value: " + inArchive.getProperty(index, propertyInfo.propID));
            }
        }
    }

}
