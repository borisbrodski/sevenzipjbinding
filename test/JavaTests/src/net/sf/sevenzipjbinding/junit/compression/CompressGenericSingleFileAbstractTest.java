package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutFeatureSetEncryptHeader;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.AbstractTestContext;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileAbstractTest.CompressGenericSingleFileAbstractTestContext;
import net.sf.sevenzipjbinding.junit.tools.CallbackTester;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests compression and extraction of a single file using generic interface.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class CompressGenericSingleFileAbstractTest
        extends CompressSingleFileAbstractTest<CompressGenericSingleFileAbstractTestContext, IOutItemAllFormats> {
    public static class CompressGenericSingleFileAbstractTestContext extends AbstractTestContext {
        boolean useEncryption;
        boolean useEncryptionNoPassword;
        boolean useHeaderEncryption;
    }

    public static class GenericSingleFileCreateArchiveCallback
            extends SingleFileCreateArchiveCallback<IOutItemAllFormats> {
        protected GenericSingleFileCreateArchiveCallback(TestContext testContext) {
            super(testContext);
        }

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            IOutItemAllFormats outItem = outItemFactory.createOutItem();

            setAllProperties(outItem, new TestContext());
            setPropertiesForArchiveFormat(outItem, testContext);

            return outItem;
        }
    }

    public class GenericSingleFileCreateArchiveWithPasswordCallback extends GenericSingleFileCreateArchiveCallback
            implements ICryptoGetTextPassword {
        private String password;

        public GenericSingleFileCreateArchiveWithPasswordCallback(TestContext testContext, String password) {
            super(testContext);
            this.password = password;
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
        }

    }

    public static final String PASSWORD = "test-pass-321";


    protected CompressGenericSingleFileAbstractTest(int size, int entropy) {
        super(size, entropy);
    }

    public void setUseEncryptionNoPassword(boolean useEncryptionNoPassword) {
        context().useEncryptionNoPassword = useEncryptionNoPassword;
    }

    public void setUseEncryption(boolean useEncryption) {
        context().useEncryption = useEncryption;
    }

    public void setUseHeaderEncryption(boolean useHeaderEncryption) {
        context().useHeaderEncryption = useHeaderEncryption;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected long doTest(final int dataSize, final int entropy) throws Exception {
        GenericSingleFileCreateArchiveCallback createArchiveCallback;
        String password = null;
        TestContext testContext = getTestContext();
        CompressGenericSingleFileAbstractTestContext context = context();
        if (context.useEncryption) {
            if (!context.useEncryptionNoPassword) {
                password = PASSWORD;
            }
            createArchiveCallback = new GenericSingleFileCreateArchiveWithPasswordCallback(testContext, password);
        } else {
            createArchiveCallback = new GenericSingleFileCreateArchiveCallback(testContext);
        }

        testContext.callbackTester = new CallbackTester<IOutCreateCallback<IOutItemAllFormats>>(createArchiveCallback);
        testContext.randomContext = new RandomContext(dataSize, entropy);

        int maxStreamSize = dataSize + MINIMUM_STREAM_LENGTH;
        ByteArrayStream outputByteArrayStream = new ByteArrayStream(maxStreamSize);

        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(getArchiveFormat());
        addCloseable(outArchive);

        if (context.useHeaderEncryption) {
            assertTrue(outArchive instanceof IOutFeatureSetEncryptHeader);
            ((IOutFeatureSetEncryptHeader) outArchive).setHeaderEncryption(true);
        }

        outArchive.createArchive(outputByteArrayStream, 1,
                (IOutCreateCallback<? extends IOutItemAllFormats>) testContext.callbackTester.getProxyInstance());
        outArchive.close();
        removeCloseable(outArchive);

        // outputByteArrayStream.writeToOutputStream(new FileOutputStream("/tmp/x.7z"), true);

        //  System.out.println("Length: " + dataSize + ", entropy: " + entropy + ": compressed size: "
        //      + outputByteArrayStream.getSize());

        if (context.useEncryption && !context.useEncryptionNoPassword && dataSize > 0) {
            if (context.useHeaderEncryption) {
                try {
                    verifyCompressedArchive(testContext.randomContext, outputByteArrayStream, password, false);
                    fail("Archive shouldn't be extractable without the password");
                } catch (SevenZipException e) {

                }
            } else {
                boolean extracted = false;
                try {
                    verifyCompressedArchive(testContext.randomContext, outputByteArrayStream, null, false);
                    extracted = true;
                } catch (AssertionError e) {

                }
                assertFalse("Archive shouldn't be extractable without the password", extracted);
            }
        }
        verifyCompressedArchive(testContext.randomContext, outputByteArrayStream, password,
                context.useHeaderEncryption);
        if (dataSize > 100000) {
            int encryptionMethods = 0;
            if (context.useEncryption) {
                encryptionMethods += ICryptoGetTextPassword.class.getMethods().length;
            }
            assertEquals(IOutCreateCallback.class.getMethods().length + encryptionMethods,
                    testContext.callbackTester.getDifferentMethodsCalled());
        }

        return outputByteArrayStream.getSize();
    }
}
