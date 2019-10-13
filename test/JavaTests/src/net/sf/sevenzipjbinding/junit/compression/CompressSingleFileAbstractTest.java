package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemBZip2;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.IOutItemGZip;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.AbstractTestContext;
import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.ParameterIgnores;
import net.sf.sevenzipjbinding.junit.junittools.annotations.ParameterNames;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;
import net.sf.sevenzipjbinding.junit.tools.AssertOutputStream;
import net.sf.sevenzipjbinding.junit.tools.CallbackTester;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public abstract class CompressSingleFileAbstractTest<C extends AbstractTestContext, T extends IOutItemBase>
        extends CompressAbstractTest<C> {
    protected static abstract class SingleFileCreateArchiveCallback<T extends IOutItemBase>
            implements IOutCreateCallback<T> {
        protected TestContext testContext;

        protected SingleFileCreateArchiveCallback(TestContext testContext) {
            this.testContext = testContext;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            assertEquals(0, index);
            return testContext.randomContext;
        }

        public void setOperationResult(boolean operationResultOk) {
        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long completeValue) throws SevenZipException {

        }

        protected void setAllProperties(IOutItemAllFormats outItem, TestContext testContext) {
            setBaseProperties(outItem);
            setPropertiesFor7z(outItem, testContext);
            setPropertiesForZip(outItem, testContext);
            setPropertiesForGZip(outItem, testContext);
            setPropertiesForBZip2(outItem, testContext);
            setPropertiesForTar(outItem, testContext);
        }

        protected void setPropertiesForArchiveFormat(IOutItemAllFormats outItem, TestContext testContext) {
            switch (outItem.getArchiveFormat()) {
            case SEVEN_ZIP:
                setPropertiesFor7z(outItem, testContext);
                break;

            case ZIP:
                setPropertiesForZip(outItem, testContext);
                break;

            case GZIP:
                setPropertiesForGZip(outItem, testContext);
                break;

            case BZIP2:
                setPropertiesForBZip2(outItem, testContext);
                break;

            case TAR:
                setPropertiesForTar(outItem, testContext);
                break;
            default:
                throw new RuntimeException("Unknown ArchiveFormat: " + outItem.getArchiveFormat());
            }
        }

        protected void setBaseProperties(IOutItemBase outItem) {
            outItem.setDataSize(Long.valueOf(testContext.randomContext.getSize()));
        }

        protected void setPropertiesFor7z(IOutItem7z outItem, TestContext testContext) {
            outItem.setPropertyAttributes(ATTRIBUTES);
            testContext.attributesSet = true;

            outItem.setPropertyIsAnti(IS_ANTI);
            testContext.isAntiSet = true;

            outItem.setPropertyLastModificationTime(MODIFICATION_TIME);
            testContext.modificationSet = true;

            outItem.setPropertyPath(SINGLE_FILE_PATH);
            testContext.pathSet = true;
        }

        protected void setPropertiesForZip(IOutItemZip outItem, TestContext testContext) {
            outItem.setPropertyAttributes(ATTRIBUTES);
            testContext.attributesSet = true;

            outItem.setPropertyCreationTime(CREATION_TIME);
            testContext.creationSet = true;

            outItem.setPropertyLastModificationTime(MODIFICATION_TIME);
            testContext.modificationSet = true;

            outItem.setPropertyLastAccessTime(ACCESS_TIME);
            testContext.accessSet = true;

            outItem.setPropertyPath(SINGLE_FILE_PATH);
            testContext.pathSet = true;
        }

        protected void setPropertiesForGZip(IOutItemGZip outItem, TestContext testContext) {
            outItem.setPropertyLastModificationTime(MODIFICATION_TIME);
            testContext.modificationSet = true;

            outItem.setPropertyPath(SINGLE_FILE_PATH);
            testContext.pathSet = true;
        }

        protected void setPropertiesForBZip2(IOutItemBZip2 outItem, TestContext testContext) {
        }

        protected void setPropertiesForTar(IOutItemTar outItem, TestContext testContext) {
            outItem.setPropertyPosixAttributes(POSIX_ATTRIBUTES);
            testContext.posixAttributesSet = true;

            outItem.setPropertyLastModificationTime(MODIFICATION_TIME);
            testContext.modificationSet = true;

            outItem.setPropertyPath(SINGLE_FILE_PATH);
            testContext.pathSet = true;

            outItem.setPropertyGroup(GROUP);
            testContext.groupSet = true;

            outItem.setPropertyUser(USER);
            testContext.userSet = true;
        }
    }

    protected static class TestContext {
        RandomContext randomContext;
        CallbackTester<? extends IOutCreateCallback<?>> callbackTester;

        boolean groupSet;
        boolean userSet;
        boolean posixAttributesSet;
        boolean attributesSet;
        boolean modificationSet;
        boolean creationSet;
        boolean accessSet;
        boolean pathSet;
        boolean isAntiSet;
    }


    protected static final int MINIMUM_STREAM_LENGTH = 32769;

    protected static final String SINGLE_FILE_PATH = "test-content.bin";
    protected static final Boolean IS_ANTI = false;
    protected static final Date ACCESS_TIME = getDate(WEEK);
    protected static final Date MODIFICATION_TIME = getDate(2 * WEEK);
    protected static final Date CREATION_TIME = getDate(3 * WEEK);
    protected static final Integer ATTRIBUTES = 1;
    protected static final Integer POSIX_ATTRIBUTES = 2;
    protected static final String GROUP = "mygroup";
    protected static final String USER = "me";

    static ThreadLocal<TestContext> testContextTL = new ThreadLocal<TestContext>() {
        @Override
        protected TestContext initialValue() {
            return new TestContext();
        };
    };

    private final int size;
    private final int entropy;

    protected abstract long doTest(int dataSize, int entropy) throws Exception;

    protected final void verifyCompressedArchive(RandomContext randomContext, ByteArrayStream outputByteArrayStream,
            String password, boolean useHeaderEncryption)
            throws SevenZipException {
        randomContext.rewind();
        outputByteArrayStream.rewind();

        IInArchive inArchive = null;
        boolean successfull = false;
        try {
            if (useHeaderEncryption) {
                inArchive = SevenZip.openInArchive(null, outputByteArrayStream, password);
            } else {
                inArchive = SevenZip.openInArchive(null, outputByteArrayStream);
            }
            Assert.assertEquals(getArchiveFormat(), inArchive.getArchiveFormat());
            assertEquals(1, inArchive.getNumberOfItems());
            ExtractOperationResult result;
            AssertOutputStream assertOutputStream = new AssertOutputStream(randomContext);
            if (password != null) {
                result = inArchive.extractSlow(0, assertOutputStream, password);
            } else {
                result = inArchive.extractSlow(0, assertOutputStream);
            }
            assertEquals(ExtractOperationResult.OK, result);
            assertTrue(assertOutputStream.readEnitireStream());

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
        if (getTestContext().pathSet) {
            assertEquals(SINGLE_FILE_PATH, inArchive.getProperty(0, PropID.PATH));
        }
        TestContext testContext = getTestContext();
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
        if (testContext.isAntiSet) {
            assertEquals(IS_ANTI, inArchive.getProperty(0, PropID.IS_ANTI));
        }
        if (getArchiveFormat() != ArchiveFormat.BZIP2) {
            assertEquals(testContext.randomContext.getSize(), inArchive.getProperty(0, PropID.SIZE));
        }

        assertFalse((Boolean) inArchive.getProperty(0, PropID.IS_FOLDER));
    }

    protected TestContext getTestContext() {
        return testContextTL.get();
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] { { 0, 0 }, //
                { 1, 0 }, //
                { 2, 0 }, //
                { 3, 0 }, //
                { 4, 0 }, //
                { 5, 0 }, //
                { 10, 0 }, //
                { 10, 10 }, //
                { 11, 0 }, //
                { 11, 2 }, //
                { 11, 11 }, //
                { 57, 0 }, //
                { 57, 2 }, //
                { 57, 5 }, //
                { 57, 10 }, //
                { 57, 30 }, //
                { 57, 57 }, //
                { 277, 0 }, //
                { 277, 5 }, //
                { 277, 8 }, //
                { 277, 20 }, //
                { 277, 100 }, //
                { 277, 277 }, //
                { 1000, 0 }, //
                { 1000, 1 }, //
                { 1000, 2 }, //
                { 1000, 5 }, //
                { 1000, 20 }, //
                { 1000, 50 }, //
                { 1000, 200 }, //
                { 1000, 600 }, //
                { 1000, 1000 }, //
                { 5000, 0 }, //
                { 5000, 100 }, //
                { 5000, 5000 }, //
                { 20000, 0 }, //
                { 20000, 200 }, //
                { 20000, 20000 }, //
                { 400000, 0 }, //
                { 400000, 300 }, //
                { 400000, 400000 }, //
                { 3000000, 0 }, //
                { 3000000, 8000 }, //
                { 3000000, 3000000 }, //
                { 20000000, 50 }, //
                { 500000000, 50 } //
        });
    }

    @ParameterNames
    public static Collection<String> getParameterNames() {
        return Arrays.asList("size", "entropy");
    }

    @ParameterIgnores
    public static boolean isParameterIgnores(int size, int entropy) {
        if (TestConfiguration.getCurrent().isOnLowMemory()) {
            return size >= 100000;
        }
        if (size >= 20000000) {
            return !TestConfiguration.getCurrent().isLongRunning();
        }
        return false;
    }

    protected CompressSingleFileAbstractTest(int size, int entropy) {
        this.size = size;
        this.entropy = entropy;
    }

    @Test
    @Multithreaded
    @Repeat
    public void testWithEntropy() throws Exception {
        doTest(size, entropy);
    }

}
