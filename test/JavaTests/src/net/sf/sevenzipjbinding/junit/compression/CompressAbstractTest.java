package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISeekableStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.AssertOutputStream;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Assert;

/**
 * Common class for all compress tests besides standalone tests like {@link StandaloneCompressSevenZipTest}.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class CompressAbstractTest extends JUnitNativeTestBase {

    protected abstract ArchiveFormat getArchiveFormat();

    protected final void verifyCompressedArchive(RandomContext randomContext, ByteArrayStream outputByteArrayStream)
            throws SevenZipException {
        randomContext.seek(0, ISeekableStream.SEEK_SET);
        outputByteArrayStream.rewind();

        IInArchive inArchive = null;
        boolean successfull = false;
        try {
            inArchive = SevenZip.openInArchive(null, outputByteArrayStream);
            Assert.assertEquals(getArchiveFormat(), inArchive.getArchiveFormat());
            inArchive.extractSlow(0, new AssertOutputStream(randomContext));

            assertEquals(1, inArchive.getNumberOfItems());

            verifyCompressedArchiveDetails(inArchive);

            successfull = true;
        } finally {
            try {
                if (inArchive != null) {
                    inArchive.close();
                }
            } catch (Throwable throwable) {
                if (successfull) {
                    throw new RuntimeException("Error closing InArchive", throwable);
                }
            }
        }
    }

    protected void verifyCompressedArchiveDetails(IInArchive inArchive) throws SevenZipException {

    }
}
