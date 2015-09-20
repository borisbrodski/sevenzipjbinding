package net.sf.sevenzipjbinding.junit.badarchive;

import static org.junit.Assert.fail;

import java.io.IOException;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

/**
 * Tests behavior of SevenZipJBinding trying opening random stream of bytes as an archive.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public class GarbageArchiveFileTest extends JUnitNativeTestBase {
    private static final int SINGLE_TEST_THREAD_COUNT = 10;
    private static final int SINGLE_TEST_TIMEOUT = 1000 * 60 * SINGLE_TEST_THREAD_COUNT;

    @Test
    public void openBadArchiveAutodetect() throws Throwable {
        openBadArchive(ArchiveFormat.SEVEN_ZIP);
    }

    @Test
    public void openBadArchiveAutodetectMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.SEVEN_ZIP);
    }

    @Test
    public void openBadArchiveSevenZip() throws Throwable {
        openBadArchive(ArchiveFormat.SEVEN_ZIP);
    }

    @Test
    public void openBadArchiveSevenZipMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.SEVEN_ZIP);
    }

    @Test
    public void openBadArchiveZip() throws Throwable {
        openBadArchive(ArchiveFormat.ZIP);
    }

    @Test
    public void openBadArchiveZipMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.ZIP);
    }

    @Test
    public void openBadArchiveTar() throws Throwable {
        openBadArchive(ArchiveFormat.TAR);
    }

    @Test
    public void openBadArchiveTarMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.TAR);
    }

    @Test
    public void openBadArchiveSplit() throws Throwable {
        openBadArchive(ArchiveFormat.SPLIT);
    }

    @Test
    public void openBadArchiveSplitMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.SPLIT);
    }

    @Test
    public void openBadArchiveRar() throws Throwable {
        openBadArchive(ArchiveFormat.RAR);
    }

    @Test
    public void openBadArchiveRarMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.RAR);
    }

    @Test
    public void openBadArchiveLzma() throws Throwable {
        openBadArchive(ArchiveFormat.LZMA);
    }

    @Test
    public void openBadArchiveLzmaMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.LZMA);
    }

    @Test
    public void openBadArchiveIso() throws Throwable {
        openBadArchive(ArchiveFormat.ISO);
    }

    @Test
    public void openBadArchiveIsoMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.ISO);
    }

    @Test
    public void openBadArchiveHFS() throws Throwable {
        openBadArchive(ArchiveFormat.HFS);
    }

    @Test
    public void openBadArchiveHFSMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.HFS);
    }

    @Test
    public void openBadArchiveGZip() throws Throwable {
        openBadArchive(ArchiveFormat.GZIP);
    }

    @Test
    public void openBadArchiveGZipMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.GZIP);
    }

    @Test
    public void openBadArchiveCpio() throws Throwable {
        openBadArchive(ArchiveFormat.CPIO);
    }

    @Test
    public void openBadArchiveCpioMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.CPIO);
    }

    @Test
    public void openBadArchiveBZip2() throws Throwable {
        openBadArchive(ArchiveFormat.BZIP2);
    }

    @Test
    public void openBadArchiveBZip2Multithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.BZIP2);
    }

    @Test
    public void openBadArchiveZ() throws Throwable {
        openBadArchive(ArchiveFormat.Z);
    }

    @Test
    public void openBadArchiveZMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.Z);
    }

    @Test
    public void openBadArchiveArj() throws Throwable {
        openBadArchive(ArchiveFormat.ARJ);
    }

    @Test
    public void openBadArchiveArjMultithreaded() throws Throwable {
        openBadArchiveMultithreaded(ArchiveFormat.ARJ);
    }

    // TODO write a test (using @After), that all archive formats was tested above

    public void openBadArchive(ArchiveFormat archiveFormat) throws Throwable {
        int pass = 0;
        int count = 0;
        for (int i = 0; i < 900; i++) { // iteration 959 recognized as GZip archive :-)
            try {
                count++;
                doOpenBadArchive(archiveFormat);
                pass++;
                // Ones in a while a valid archive header will be generated
            } catch (SevenZipException e) {
                assert true;
            } catch (Throwable throwable) {
                System.out.println("Iteration: " + i);
                throw throwable;
            }
        }
        if (pass > count / 50) {
            fail("Expected exception wasn't thrown oft enough. ");
        }
    }

    public void openBadArchiveMultithreaded(final ArchiveFormat archiveFormat) throws Throwable {
        final int[] threadsFinished = new int[] { SINGLE_TEST_THREAD_COUNT };
        final Throwable[] firstThrowable = new Throwable[] { null };
        for (int i = 0; i < SINGLE_TEST_THREAD_COUNT; i++) {
            new Thread(new Runnable() {

                public void run() {
                    try {
                        openBadArchive(archiveFormat);
                    } catch (Throwable e) {
                        synchronized (firstThrowable) {
                            if (firstThrowable[0] == null) {
                                firstThrowable[0] = e;
                            }
                        }
                    } finally {
                        synchronized (GarbageArchiveFileTest.this) {
                            try {
                                threadsFinished[0]--;
                                GarbageArchiveFileTest.this.notify();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
        long start = System.currentTimeMillis();
        synchronized (this) {
            while (true) {
                try {
                    if (threadsFinished[0] == 0) {
                        break;
                    }
                    wait(SINGLE_TEST_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (System.currentTimeMillis() - start > SINGLE_TEST_TIMEOUT) {
                    fail("Time out");
                }
            }
        }
        if (firstThrowable[0] != null) {
            if (firstThrowable[0] instanceof SevenZipException) {
                throw (SevenZipException) firstThrowable[0];
            }
            throw new RuntimeException("Exception in underlying thread", firstThrowable[0]);
        }
    }

    public void doOpenBadArchive(ArchiveFormat archiveFormat) throws Exception {
        final byte[] archive = new byte[random.get().nextInt(1000) + 1000];
        random.get().nextBytes(archive);
        IInStream inStream = new IInStream() {
            int offset = 0;

            public int read(byte[] data) throws SevenZipException {
                int result = 0;
                for (int i = 0; i < data.length && offset < archive.length; i++) {
                    data[i] = archive[offset++];
                    result++;
                }
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
