package net.sf.sevenzipjbinding.junit.bug;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;

public class OpenMultipartCabWithNonVolumedCallbackTest extends JUnitNativeTestBase<VoidContext> {
    private class MyArchiveOpenCallback implements IArchiveOpenCallback, IArchiveOpenVolumeCallback {
        public Object getProperty(PropID propID) throws SevenZipException {
            return null;
        }

        public IInStream getStream(String filename) throws SevenZipException {
            RandomAccessFile randomAccessFile;
            try {
                randomAccessFile = new RandomAccessFile(PATH_TO_ARCHIVES + filename, "r");
                assertNotNull(randomAccessFile);
            } catch (FileNotFoundException e) {
                throw new SevenZipException(e);
            }
            return new RandomAccessFileInStream(randomAccessFile);
        }

        public void setTotal(Long files, Long bytes) throws SevenZipException {
        }

        public void setCompleted(Long files, Long bytes) throws SevenZipException {
        }
    }

    private static final String PATH_TO_ARCHIVES = "testdata/multiple-files/cab/";
    private static final String DISK1_FILE = "vol-archive1.zip.0.disk1.cab";

    @Test
    public void extractWithoutCallback() throws Throwable {
        extractMultivolumneCabArchive(ArchiveFormat.CAB, null, false);
    }

    @Test
    public void extractWithoutCallbackAutodetect() throws Throwable {
        extractMultivolumneCabArchive(null, null, false);
    }

    @Test
    public void extractWithProperCallback() throws Throwable {
        extractMultivolumneCabArchive(ArchiveFormat.CAB, new MyArchiveOpenCallback(), true);
    }

    @Test
    public void extractWithProperCallbackAutodetect() throws Throwable {
        extractMultivolumneCabArchive(null, new MyArchiveOpenCallback(), true);
    }

    private void extractMultivolumneCabArchive(ArchiveFormat archiveType, IArchiveOpenCallback archiveOpenCallback,
            boolean expectToOpen) throws Throwable {
        IInArchive inArchive = null;
        Throwable throwable = null;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(PATH_TO_ARCHIVES + DISK1_FILE, "r");
            assertNotNull(randomAccessFile);
            try {
                inArchive = SevenZip.openInArchive(archiveType, new RandomAccessFileInStream(randomAccessFile),
                        archiveOpenCallback);
            } catch (Exception exception) {
            }
        } catch (Throwable t) {
            throwable = t;
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (Throwable t) {
                    if (throwable == null) {
                        throwable = t;
                    } else {
                        t.printStackTrace();
                    }
                }
            }
        }
        if (throwable != null) {
            throw throwable;
        }
        if (expectToOpen) {
            if (inArchive == null) {
                fail("Can't open multivolume CAB file even providing proper implementation of the IArchiveOpenVolumeCallback interface");
            }
        } else {
            if (inArchive != null) {
                fail("Exception expected due to the missing IArchiveOpenCallback instance with implemented IArchiveOpenVolumeCallback");
            }
        }
    }
}
