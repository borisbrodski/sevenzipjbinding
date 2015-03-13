package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutCreateCallbackBase;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.tools.CallbackTester;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests compression and extraction of a single file interface.
 *
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public abstract class CompressNonGenericSingleFileAbstractTest extends CompressSingleFileAbstractTest {
    public class SingleFileOutItemCallback implements IOutItemCallback {

        public boolean isAnti() throws SevenZipException {
            testContextThreadContext.get().isAntiSet = true;
            return IS_ANTI;
        }

        public long getSize() throws SevenZipException {
            return testContextThreadContext.get().randomContext.getSize();
        }

        public String getPath() throws SevenZipException {
            testContextThreadContext.get().pathSet = true;
            return SINGLE_FILE_PATH;
        }

        public Date getModificationTime() throws SevenZipException {
            testContextThreadContext.get().modificationSet = true;
            return MODIFICATION_TIME;
        }

        public Date getLastAccessTime() throws SevenZipException {
            testContextThreadContext.get().accessSet = true;
            return ACCESS_TIME;
        }

        public Date getCreationTime() throws SevenZipException {
            testContextThreadContext.get().creationSet = true;
            return CREATION_TIME;
        }


        public boolean isDir() throws SevenZipException {
            return false;
        }

        public Integer getAttributes() throws SevenZipException {
            testContextThreadContext.get().attributesSet = true;
            return ATTRIBUTES;
        }

        public Integer getPosixAttributes() throws SevenZipException {
            testContextThreadContext.get().posixAttributesSet = true;
            return POSIX_ATTRIBUTES;
        }

        public String getUser() throws SevenZipException {
            testContextThreadContext.get().userSet = true;
            return USER;
        }

        public String getGroup() throws SevenZipException {
            testContextThreadContext.get().groupSet = true;
            return GROUP;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected long doTest(int dataSize, int entropy) throws Exception {
        SingleFileCreateArchiveCallback createArchiveCallback = new SingleFileCreateArchiveCallback();
        createArchiveCallback.setOutItemCallback(new SingleFileOutItemCallback());

        TestContext testContext = testContextThreadContext.get();
        testContext.callbackTester = new CallbackTester<IOutCreateCallbackBase>(createArchiveCallback);
        testContext.randomContext = new RandomContext(dataSize, entropy);

        int maxStreamSize = dataSize + MINIMUM_STREAM_LENGTH;
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(maxStreamSize);

        IOutCreateArchive<IOutItemCallback> outArchive = SevenZip.openOutArchive(getArchiveFormat());

        try {
            outArchive.createArchive(outputByteArrayStream, 1,
                    (IOutCreateCallback<? extends IOutItemCallback>) testContext.callbackTester.getProxyInstance());
        } finally {
            outArchive.close();
        }

        //        System.out.println("Length: " + dataSize + ", entropy: " + entropy + ": compressed size: "
        //                + outputByteArrayStream.getSize());

        verifyCompressedArchive(testContext.randomContext, outputByteArrayStream);
        if (dataSize > 100000) {
            assertEquals(IOutCreateCallback.class.getMethods().length,
                    testContext.callbackTester.getDifferentMethodsCalled());
        }

        return outputByteArrayStream.getSize();
    }


}
