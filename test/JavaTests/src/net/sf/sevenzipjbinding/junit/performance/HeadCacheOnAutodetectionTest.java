package net.sf.sevenzipjbinding.junit.performance;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

/**
 * Test head caching.
 *
 * @author Boris Brodski
 * @since 15.09
 */
public class HeadCacheOnAutodetectionTest extends JUnitNativeTestBase<VoidContext> {
    private static final int BAD_ARCHIVE_SIZE = 1 * 1024 * 1024;

    private static ThreadLocal<Long> headReadingCounter = new ThreadLocal<Long>();
    private static ThreadLocal<Long> bytesRead = new ThreadLocal<Long>();

    @Test
    @Repeat
    @Multithreaded
    public void openBadArchiveWithAutodetection() throws Exception {
        headReadingCounter.set(Long.valueOf(0));
        bytesRead.set(Long.valueOf(0));
        try {
            doOpenBadArchive(null);
            fail("Managed to open a broken archive");
        } catch (Exception e) {
        }
        log();
        log("Reads     : " + headReadingCounter.get());
        log("Read bytes: " + bytesRead.get() + " bytes");
        Assert.assertTrue(headReadingCounter.get() <= 4);
    }

    public void doOpenBadArchive(ArchiveFormat archiveFormat) throws Exception {
        final byte[] archive = new byte[BAD_ARCHIVE_SIZE];
        for (int i = 0; i < archive.length; i++) {
            archive[i] = (byte) i;
        }
        IInStream inStream = new IInStream() {
            int offset = 0;

            public int read(byte[] data) throws SevenZipException {
                if (offset < 4096) {
                    headReadingCounter.set(headReadingCounter.get() + 1);
                }
                int result = 0;
                for (int i = 0; i < data.length && offset < archive.length; i++) {
                    data[i] = archive[offset++];
                    result++;
                }
                bytesRead.set(bytesRead.get() + result);
                return result;
            }

            public long seek(long newOffset, int seekOrigin) throws SevenZipException {
                switch (seekOrigin) {
                case SEEK_SET:
                    offset = (int) newOffset;
                    break;
                case SEEK_CUR:
                    offset += newOffset;
                    break;
                case SEEK_END:
                    offset = (int) (archive.length + newOffset);
                }
                return offset >= archive.length ? archive.length : offset;
            }

            public void close() throws IOException {
            }
        };
        IInArchive openInArchive = SevenZip.openInArchive(archiveFormat, inStream);
        openInArchive.close();
    }
}
