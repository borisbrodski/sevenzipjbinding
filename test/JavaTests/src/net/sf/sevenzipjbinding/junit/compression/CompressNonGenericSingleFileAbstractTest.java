package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
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
            return false;
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

    public class NonGenericSingleFileCreateArchiveCallback implements IOutCreateCallback<IOutItemCallback> {
        public ISequentialInStream getStream(int index) {
            assertEquals(0, index);
            return testContextThreadContext.get().randomContext;
        }

        public void setOperationResult(boolean operationResultOk) {
        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long completeValue) throws SevenZipException {

        }

        public IOutItemCallback getOutItemCallback(int index) throws SevenZipException {
            assertEquals(0, index);
            return new SingleFileOutItemCallback();
        }
    }

    private class TestContext {
        public boolean groupSet;
        public boolean userSet;
        public boolean posixAttributesSet;
        public boolean attributesSet;
        RandomContext randomContext;
        CallbackTester<NonGenericSingleFileCreateArchiveCallback> callbackTester = new CallbackTester<NonGenericSingleFileCreateArchiveCallback>(
                new NonGenericSingleFileCreateArchiveCallback());
        boolean modificationSet;
        boolean creationSet;
        boolean accessSet;
        boolean pathSet;
    }

    static final int MINIMUM_STREAM_LENGTH = 32769;
    static final Date ACCESS_TIME = getDate(WEEK);
    static final Date MODIFICATION_TIME = getDate(2 * WEEK);
    static final Date CREATION_TIME = getDate(3 * WEEK);
    static final Integer ATTRIBUTES = 1;
    static final Integer POSIX_ATTRIBUTES = 2;
    static final String GROUP = "mygroup";
    static final String USER = "me";

    ThreadLocal<TestContext> testContextThreadContext = new ThreadLocal<CompressNonGenericSingleFileAbstractTest.TestContext>() {
        @Override
        protected TestContext initialValue() {
            return new TestContext();
        };
    };


    @Override
    protected long doTest(final int dataSize, final int entropy, boolean multithreaded) throws Exception {
        long result = 0;
        if (multithreaded) {
            runMultithreaded(new RunnableThrowsException() {
                public void run() throws Exception {
                    doTest(dataSize, entropy, false);
                }
            }, null);
        } else {
            int repeatCount = dataSize == 0 ? SINGLE_TEST_REPEAT_COUNT * 100
                    : 1 + (SINGLE_TEST_REPEAT_COUNT * 100 / dataSize);

            for (int i = 0; i < repeatCount; i++) {
                result = doTest(dataSize, entropy);
            }
        }
        return result;
    }

    @Override
    protected long doTest(int dataSize, int entropy) throws Exception {
        TestContext testContext = testContextThreadContext.get();
        testContext.randomContext = new RandomContext(dataSize, entropy);

        int maxStreamSize = dataSize + MINIMUM_STREAM_LENGTH;
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(maxStreamSize);

        IOutCreateArchive<IOutItemCallback> outArchive = SevenZip.openOutArchive(getArchiveFormat());

        try {
            outArchive.createArchive(outputByteArrayStream, 1, testContext.callbackTester.getInstance());
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

    @Override
    protected void verifyCompressedArchiveDetails(IInArchive inArchive) throws SevenZipException {
        TestContext testContext = testContextThreadContext.get();
        if (testContext.pathSet) {
            assertEquals(SINGLE_FILE_PATH, inArchive.getProperty(0, PropID.PATH));
        }
        if (testContext.modificationSet) {
            // TODO This breaks for unknown reason in multithreaded ZIP-test.
            // TODO Fix this
            if (getArchiveFormat() != ArchiveFormat.ZIP) {
                Date isDate = (Date) inArchive.getProperty(0, PropID.LAST_MODIFICATION_TIME);
                //            for (int i = 0; i < 10000; i++) {
                //                assertEquals(isDate.getTime(),
                //                        ((Date) inArchive.getProperty(0, PropID.LAST_MODIFICATION_TIME)).getTime());
                //            }
                long diffInMillis = Math.abs(MODIFICATION_TIME.getTime() - isDate.getTime());
                if (diffInMillis > 2000) {
                    System.out.println("is date: " + isDate);
                    System.out.println("ex date: " + MODIFICATION_TIME);
                }
                assertTrue("More that 2 seconds difference: " + diffInMillis, diffInMillis <= 2000L);
            }
        }
        if (testContext.creationSet && getArchiveFormat() != ArchiveFormat.ZIP) {
            Date isDate = (Date) inArchive.getProperty(0, PropID.CREATION_TIME);
            assertEquals(CREATION_TIME.getTime(), isDate.getTime(), 2000.0);
        }
        if (testContext.accessSet && getArchiveFormat() != ArchiveFormat.ZIP) {
            Date isDate = (Date) inArchive.getProperty(0, PropID.LAST_ACCESS_TIME);
            assertEquals(ACCESS_TIME.getTime(), isDate.getTime(), 2000.0);
        }
        if (testContext.attributesSet && getArchiveFormat() != ArchiveFormat.ZIP) {
            assertEquals(ATTRIBUTES, inArchive.getProperty(0, PropID.ATTRIBUTES));
        }
        if (testContext.posixAttributesSet) {
            assertEquals(POSIX_ATTRIBUTES, inArchive.getProperty(0, PropID.POSIX_ATTRIB));
        }
        if (testContext.userSet) {
            assertEquals(USER, inArchive.getProperty(0, PropID.USER));
        }
        if (testContext.groupSet) {
            assertEquals(GROUP, inArchive.getProperty(0, PropID.GROUP));
        }
        if (getArchiveFormat() != ArchiveFormat.BZIP2) {
            assertEquals(testContext.randomContext.getSize(), inArchive.getProperty(0, PropID.SIZE));
        }

        assertFalse((Boolean) inArchive.getProperty(0, PropID.IS_FOLDER));
    }

    private static Date getDate(int period) {
        return new Date(new Date().getTime() - RANDOM.nextInt(period) - period);
    }
}
