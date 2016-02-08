package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.tools.CallbackTester;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests compression and extraction of a single file using generic interface.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class CompressGenericSingleFileAbstractTest extends CompressSingleFileAbstractTest<IOutItemAllFormats> {
    public class GenericSingleFileCreateArchiveCallback extends SingleFileCreateArchiveCallback {
        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            IOutItemAllFormats outItem = outItemFactory.createOutItem();

            setAllProperties(outItem, new TestContext());
            setPropertiesForArchiveFormat(outItem, getTestContext());

            return outItem;
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    protected long doTest(final int dataSize, final int entropy) throws Exception {
        GenericSingleFileCreateArchiveCallback createArchiveCallback = new GenericSingleFileCreateArchiveCallback();

        testContext.callbackTester = new CallbackTester<IOutCreateCallback<IOutItemAllFormats>>(createArchiveCallback);
        testContext.randomContext = new RandomContext(dataSize, entropy);

        int maxStreamSize = dataSize + MINIMUM_STREAM_LENGTH;
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(maxStreamSize);

        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(getArchiveFormat());

        try {
            outArchive.createArchive(outputByteArrayStream, 1,
                    (IOutCreateCallback<? extends IOutItemAllFormats>) testContext.callbackTester.getProxyInstance());
        } finally {
            outArchive.close();
        }

        //  System.out.println("Length: " + dataSize + ", entropy: " + entropy + ": compressed size: "
        //      + outputByteArrayStream.getSize());

        verifyCompressedArchive(testContext.randomContext, outputByteArrayStream);
        if (dataSize > 100000) {
            assertEquals(IOutCreateCallback.class.getMethods().length,
                    testContext.callbackTester.getDifferentMethodsCalled());
        }

        return outputByteArrayStream.getSize();
    }
}
