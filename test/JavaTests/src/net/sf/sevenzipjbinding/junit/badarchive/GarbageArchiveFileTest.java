package net.sf.sevenzipjbinding.junit.badarchive;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

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
 * Tests behavior of SevenZipJBinding trying opening random stream of bytes as an archive.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public class GarbageArchiveFileTest extends JUnitNativeTestBase<VoidContext> {
    private final ArchiveFormat archiveFormat;

    @Parameters
    public static Collection<?> getParameters() {
        return Arrays.asList(ArchiveFormat.values());
    }

    public GarbageArchiveFileTest(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    @Test
    @Repeat
    @Multithreaded
    public void openBadArchive() throws Throwable {
        openBadArchive(archiveFormat);
    }

    public void openBadArchive(ArchiveFormat archiveFormat) throws Throwable {
        int pass = 0;
        int count = 0;
        for (int i = 0; i < 900; i++) {
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
        } else {
            if (pass > 0) {
                log("Got some false positives: " + pass + " of " + count + " (expected, no error)");
            }
        }
    }

    public void doOpenBadArchive(ArchiveFormat archiveFormat) throws Exception {
        final byte[] archive = new byte[getRandom().nextInt(1000) + 1000];
        getRandom().nextBytes(archive);
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
