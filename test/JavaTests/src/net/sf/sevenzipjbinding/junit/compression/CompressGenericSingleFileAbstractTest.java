package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutCreateCallbackBase;
import net.sf.sevenzipjbinding.IOutCreateCallbackGeneric;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.tools.CallbackTester;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests compression and extraction of a single file using generic interface.
 *
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public abstract class CompressGenericSingleFileAbstractTest extends CompressSingleFileAbstractTest {
    public class GenericSingleFileCreateArchiveCallback implements IOutCreateCallbackGeneric {
        public Object getProperty(int index, PropID propID) {
            TestContext testContext = testContextThreadContext.get();
            switch (propID) {
            case PATH:
                testContext.pathSet = true;
                return SINGLE_FILE_PATH;

            case IS_FOLDER:
                return Boolean.FALSE;

            case IS_ANTI:
                testContext.isAntiSet = true;
                return IS_ANTI;

            case SIZE:
                return Long.valueOf(testContext.randomContext.getSize());

            case CREATION_TIME:
                testContext.creationSet = true;
                return CREATION_TIME;

            case LAST_MODIFICATION_TIME:
                testContext.modificationSet = true;
                return MODIFICATION_TIME;

            case LAST_ACCESS_TIME:
                testContext.accessSet = true;
                return ACCESS_TIME;

            case GROUP:
                testContext.groupSet = true;
                return GROUP;

            case USER:
                testContext.userSet = true;
                return USER;

            case POSIX_ATTRIB:
                testContext.posixAttributesSet = true;
                return POSIX_ATTRIBUTES;

            case ATTRIBUTES:
                testContext.attributesSet = true;
                return ATTRIBUTES;
            }
            return null;
        }

        public ISequentialInStream getStream(int index) {
            return testContextThreadContext.get().randomContext;
        }

        public void setOperationResult(boolean operationResultOk) {

        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long completeValue) throws SevenZipException {

        }
    }


    @Override
    protected long doTest(final int dataSize, final int entropy) throws Exception {
        GenericSingleFileCreateArchiveCallback createArchiveCallback = new GenericSingleFileCreateArchiveCallback();

        TestContext testContext = testContextThreadContext.get();
        testContext.callbackTester = new CallbackTester<IOutCreateCallbackBase>(createArchiveCallback);
        testContext.randomContext = new RandomContext(dataSize, entropy);

        int maxStreamSize = dataSize + MINIMUM_STREAM_LENGTH;
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(maxStreamSize);

        IOutCreateArchive<IOutItemCallback> outArchive = SevenZip.openOutArchive(getArchiveFormat());

        try {
            outArchive.createArchive(outputByteArrayStream, 1,
                    (IOutCreateCallbackGeneric) testContext.callbackTester.getProxyInstance());
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
