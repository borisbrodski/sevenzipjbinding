package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.AbstractTestContext;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Common class for all compress tests besides standalone tests like {@link StandaloneCompressSevenZipTest}.
 *
 * @param <C>
 *            TestContext class
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class CompressAbstractTest<C extends AbstractTestContext> extends JUnitNativeTestBase<C> {

    protected abstract ArchiveFormat getArchiveFormat();

    protected void assertHeaderCrypted(ByteArrayStream byteArrayStream, ArchiveFormat archiveFormat) {
        try {
            IInArchive inArchive = SevenZip.openInArchive(archiveFormat, byteArrayStream);
            inArchive.close();
            fail("Archive without an encrypted header");
        } catch (SevenZipException e) {
        }
        byteArrayStream.rewind();
    }

    protected void assertAllItemsCrypted(IInArchive inArchive) throws SevenZipException {
        final AtomicInteger atomicI = new AtomicInteger();
        List<ExtractOperationResult> expectedResults = Arrays.asList(//
                ExtractOperationResult.DATAERROR, //
                ExtractOperationResult.WRONG_PASSWORD, //
                ExtractOperationResult.UNSUPPORTEDMETHOD, //
                ExtractOperationResult.CRCERROR);

        for (int i = 0, count = inArchive.getNumberOfItems(); i < count; i++) {
            if ((Boolean) inArchive.getProperty(i, PropID.IS_FOLDER)) {
                continue;
            }
            if (((Long) inArchive.getProperty(i, PropID.SIZE)).longValue() == 0) {
                continue;
            }
            atomicI.set(i);
            ExtractOperationResult result = inArchive.extractSlow(i, new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    return data.length; // Consume the junk, in case CRC accidently matchs
                }
            });
            assertTrue("Unexpected result: " + result, expectedResults.contains(result));
        }
    }
}
