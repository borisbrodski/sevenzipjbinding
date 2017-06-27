package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Test head caching.
 *
 * @author Boris Brodski
 * @since 15.09
 */
public class ArchiveWithTwoPasswordsTest extends JUnitNativeTestBase<VoidContext> {
    private class ArchiveWithTwoPasswordsExtractCallback implements IArchiveExtractCallback, ICryptoGetTextPassword {
        Map<Integer, ByteArrayStream> outStreams = new HashMap<Integer, ByteArrayStream>();
        int passwordIndex;
        private int[] indexes;

        ArchiveWithTwoPasswordsExtractCallback(int[] sortedIndexes) {
            this.indexes = sortedIndexes;

        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return DATA_PASSWORDS[indexes[passwordIndex++]];
        }

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
            ByteArrayStream byteArrayStream = new ByteArrayStream(getMaxDataSize());
            outStreams.put(index, byteArrayStream);
            return byteArrayStream;
        }

        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
        }

        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
            assertEquals(ExtractOperationResult.OK, extractOperationResult);
        }

        Map<Integer, ByteArrayStream> getOutStreams() {
            return outStreams;
        }
    }
    private static final String TEST_DATA_PATH = "testdata/misc/password-protection/";
    private static final String[] DATA_FILES = { //
            TEST_DATA_PATH + "data1", //
            TEST_DATA_PATH + "data2" //
    };
    private static final String[] DATA_PASSWORDS = { "data1", "data2" };
    private static final String PASSWORD_PROTECTED_ARHCIVE = TEST_DATA_PATH + "two-passwords.7z";
    private static final ArchiveFormat ARCHIVE_FORMAT = ArchiveFormat.SEVEN_ZIP;

    @Test
    public void checkArchiveItemsPasswordProtected() throws Exception {
        RandomAccessFileInStream stream = null;
        stream = new RandomAccessFileInStream(new RandomAccessFile(PASSWORD_PROTECTED_ARHCIVE, "r"));
        addCloseable(stream);

        IInArchive archive = SevenZip.openInArchive(ARCHIVE_FORMAT, stream);
        addCloseable(archive);

        assertEquals(2, archive.getNumberOfItems());

        checkExtractFile(archive, 0, false);
        checkExtractFile(archive, 1, false);
    }

    @Test
    public void checkCanExtractOnlyOneWithASinglePassword() throws Exception {
        RandomAccessFileInStream stream = null;
        stream = new RandomAccessFileInStream(new RandomAccessFile(PASSWORD_PROTECTED_ARHCIVE, "r"));
        addCloseable(stream);

        IInArchive archive = SevenZip.openInArchive(ARCHIVE_FORMAT, stream);
        addCloseable(archive);

        assertEquals(2, archive.getNumberOfItems());

        for (int i = 0; i < DATA_FILES.length; i++) {
            for (int j = 0; j < DATA_PASSWORDS.length; j++) {
                String password = DATA_PASSWORDS[j];
                byte[] extractedBytes = checkExtractFile(archive, i, i == j, password);
                if (extractedBytes != null) {
                    compareFileContent(DATA_FILES[i], extractedBytes);
                }
            }
        }
    }

    @Test
    public void readBothFilesAtTheSameTime() throws Exception {
        RandomAccessFileInStream stream = null;
        stream = new RandomAccessFileInStream(new RandomAccessFile(PASSWORD_PROTECTED_ARHCIVE, "r"));
        addCloseable(stream);

        IInArchive archive = SevenZip.openInArchive(ARCHIVE_FORMAT, stream);
        addCloseable(archive);

        assertEquals(2, archive.getNumberOfItems());

        int[] indices = new int[] { 0, 1 };
        Arrays.sort(indices); // Works only for sorted indecies
        ArchiveWithTwoPasswordsExtractCallback callback = new ArchiveWithTwoPasswordsExtractCallback(indices);
        archive.extract(indices, false, callback);

        for (int i : indices) {
            byte[] readData = callback.getOutStreams().get(i).getBytes();
            compareFileContent(DATA_FILES[i], readData);
        }
    }

    private void compareFileContent(String filename, byte[] extractedBytes) throws IOException {
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);
        addCloseable(fileInputStream);

        byte[] buffer = new byte[(int) file.length()];
        int pos = 0;
        while (buffer.length > pos) {
            int read = fileInputStream.read(buffer, pos, buffer.length - pos);
            assertNotEquals(-1, read);
            pos += read;
        }
        assertArrayEquals(buffer, extractedBytes);

    }

    private void checkExtractFile(IInArchive archive, int itemIndex, boolean expectSuccess) throws SevenZipException {
        checkExtractFile(archive, itemIndex, expectSuccess, null);
    }

    private byte[] checkExtractFile(IInArchive archive, int itemIndex, boolean expectSuccess, String password)
            throws SevenZipException {
        ExtractOperationResult result;
        ByteArrayStream byteArrayStream = new ByteArrayStream(getMaxDataSize());
        if (password == null) {
            result = archive.extractSlow(itemIndex, byteArrayStream);
        } else {
            result = archive.extractSlow(itemIndex, byteArrayStream, password);
        }
        String pass = "";
        if (password != null) {
            pass = " (password: '" + password + "')";
        }

        if (expectSuccess) {
            assertEquals("Item " + itemIndex + " should be extractable" + pass, ExtractOperationResult.OK, result);
            return byteArrayStream.getBytes();
        }

        assertNotEquals("Item " + itemIndex + " should be password protected" + pass, ExtractOperationResult.OK,
                result);
        return null;
    }

    int getMaxDataSize() {
        long maxSize = 0;
        for (String file : DATA_FILES) {
            long size = new File(file).length();
            if (maxSize < size) {
                maxSize = size;
            }
        }
        return (int) maxSize;
    }
}
