package net.sf.sevenzipjbinding.junit.encoding;

import static org.junit.Assert.assertEquals;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class UnicodeFilenamesInArchive extends JUnitNativeTestBase<VoidContext> {

    private final ArchiveFormat archiveFormat;

    public UnicodeFilenamesInArchive(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    @Parameters
    public static Collection<Object> data() {
        return Arrays.asList(new Object[] { ArchiveFormat.SEVEN_ZIP, ArchiveFormat.ZIP });
    }

    @Test
    public void testUpperLowerCaseUnicode() throws Throwable {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        Throwable throwable = null;

        log("Testing for " + archiveFormat);
        try {
            randomAccessFile = new RandomAccessFile("testdata/encoding/unicode_file_names."
                    + archiveFormat.getMethodName().toLowerCase(), "r");
            inArchive = SevenZip.openInArchive(archiveFormat, new RandomAccessFileInStream(randomAccessFile));
            ISimpleInArchive simpleInterface = inArchive.getSimpleInterface();
            assertEquals(2, simpleInterface.getNumberOfItems());

            ISimpleInArchiveItem[] archiveItems = simpleInterface.getArchiveItems();
            assertEquals("öüäÖÄß.txt", archiveItems[0].getPath());
            assertEquals("Тест.txt", archiveItems[1].getPath());

            assertEquals("Umlaute: öüäÖÜÄß", extractUTF8ToString(archiveItems[0]));
            assertEquals("Русский текст", extractUTF8ToString(archiveItems[1]));
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

    private String extractUTF8ToString(ISimpleInArchiveItem iSimpleInArchiveItem) throws SevenZipException {
        final StringBuilder stringBuilder = new StringBuilder();
        iSimpleInArchiveItem.extractSlow(new ISequentialOutStream() {
            public int write(byte[] data) throws SevenZipException {
                try {
                    stringBuilder.append(new String(data, "UTF8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                return data.length;
            }
        });
        return stringBuilder.toString();
    }
}
