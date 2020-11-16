package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.util.Date;

import org.junit.Assert;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.AbstractTestContext;
import net.sf.sevenzipjbinding.junit.tools.AssertOutputStream;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests setting compression level.
 *
 * @param <C>
 *            TestContext class
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class CompressFeatureAbstractSingleFile<C extends AbstractTestContext> extends CompressAbstractTest<C> {

    public static class FeatureSingleFileCreateArchiveCallback implements IOutCreateCallback<IOutItemAllFormats> {

        private RandomContext randomContext;

        public FeatureSingleFileCreateArchiveCallback(RandomContext randomContext) {
            this.randomContext = randomContext;
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {

        }

        public void setTotal(long total) throws SevenZipException {

        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            IOutItemAllFormats outItem = outItemFactory.createOutItem();

            outItem.setDataSize(randomContext.getSize());
            outItem.setPropertyLastModificationTime(new Date());
            outItem.setPropertyPath("content");
            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return randomContext;
        }
    }

    protected IOutCreateArchive<IOutItemAllFormats> createArchive() throws Exception {
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(getArchiveFormat());
        addCloseable(outArchive);
        return outArchive;
    }

    protected void closeArchive(Closeable outArchive) throws Exception {
        removeCloseable(outArchive);
        outArchive.close();
    }

    protected void verifySingleFileArchive(RandomContext randomContext, ByteArrayStream outputByteArrayStream)
            throws Exception {
        outputByteArrayStream.rewind();
        randomContext.rewind();
        IInArchive inArchive = SevenZip.openInArchive(getArchiveFormat(), outputByteArrayStream);
        addCloseable(inArchive);

        Assert.assertEquals(getArchiveFormat(), inArchive.getArchiveFormat());
        AssertOutputStream assertOutputStream = new AssertOutputStream(randomContext);
        inArchive.extractSlow(0, assertOutputStream);
        closeArchive(inArchive);

        assertTrue(assertOutputStream.readEnitireStream());
    }
}
