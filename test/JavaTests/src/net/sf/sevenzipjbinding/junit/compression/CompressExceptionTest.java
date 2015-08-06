package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

/**
 * Tests exceptions during archive compression.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class CompressExceptionTest extends CompressAbstractTest {
    public static class CompressException7zTest extends CompressExceptionTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.SEVEN_ZIP;
        }
    }

    public static class CompressExceptionZipTest extends CompressExceptionTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.ZIP;
        }
    }

    public static class CompressExceptionGZipTest extends CompressExceptionTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.GZIP;
        }
    }

    public static class CompressExceptionBZip2Test extends CompressExceptionTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.BZIP2;
        }
    }

    public static class CompressExceptionTarTest extends CompressExceptionTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.TAR;
        }
    }

    @Test
    public void testNullAs() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestNullAs();
            }
        });
    }

    @Test
    public void testNullAsMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestNullAs();
            }
        });
    }

    private void doTestNullAs() throws Exception {
        try {
            IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(getArchiveFormat());
            addCloseable(outArchive);
            ByteArrayStream byteArrayStream = new ByteArrayStream(10000);
            outArchive.createArchive(byteArrayStream, 1, new IOutCreateCallback<IOutItemAllFormats>() {
                public void setOperationResult(boolean operationResultOk) throws SevenZipException {

                }

                public void setTotal(long total) throws SevenZipException {

                }

                public void setCompleted(long complete) throws SevenZipException {

                }

                public IOutItemAllFormats getItemInformation(int index,
                        OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                    return null; // Return null here!
                }

                public void freeResources(int index, IOutItemAllFormats outItem) throws SevenZipException {
                }
            });
            fail("Exception expected");
        } catch (SevenZipException sevenZipException) {
            assertEquals(IOutCreateCallback.class.getSimpleName() + ".getItemInformation() should return "
                    + "a non-null reference to an item information object. Use outItemFactory "
                    + "to create an instance. Fill the new object with all necessary information about "
                    + "the archive item being processed.",
                    sevenZipException.getMessage());
        }
    }
}

