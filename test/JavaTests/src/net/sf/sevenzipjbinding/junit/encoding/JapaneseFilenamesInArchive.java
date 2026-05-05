package net.sf.sevenzipjbinding.junit.encoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class JapaneseFilenamesInArchive extends JUnitNativeTestBase<VoidContext> {

    private final ArchiveFormat archiveFormat;

    public JapaneseFilenamesInArchive(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    @Parameters
    public static Collection<Object> data() {
        return Arrays.asList(new Object[] { ArchiveFormat.ZIP });
    }

    @Test
    public void testUpperLowerCaseUnicode() throws Throwable {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        Throwable throwable = null;

        log("Testing for " + archiveFormat);
        try {
            randomAccessFile = new RandomAccessFile("testdata/encoding/jpn_file_names."
                    + archiveFormat.getMethodName().toLowerCase(), "r");
            inArchive = SevenZip.openInArchive(archiveFormat, new RandomAccessFileInStream(randomAccessFile));
            ISimpleInArchive simpleInterface = inArchive.getSimpleInterface();

            int numItems = simpleInterface.getNumberOfItems();
            assertEquals(2, numItems);

            ISimpleInArchiveItem[] archiveItems = simpleInterface.getArchiveItems();

            String[] truth = {"123アアパート.txt", "テスト日本語.txt"};


            for (int i = 0; i < numItems; i++) {
                String t = truth[i];
                String path = archiveItems[i].getPath();
                assertNotEquals(t, path);

                byte[] pathBytes = (byte[])inArchive.getProperty(i, PropID.PATH_BYTES);
                String encodedPath = new String(pathBytes, StandardCharsets.UTF_8);

                assertEquals(t, encodedPath);
            }
        } catch (Throwable e) {
            throwable = e;
        } finally {
            try {
                if (inArchive != null) {
                    inArchive.close();
                }
            } catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
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
        }

        if (throwable != null) {
            throw throwable;
        }
    }
}
