package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

/**
 * Tests exceptions during archive compression.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class CompressExceptionGetItemInformationTest extends CompressAbstractTest {
    public interface IOutItemModifier {
        public IOutItemAllFormats modify(IOutItemAllFormats outItem);

        public void verifyException(Exception e);
    }
    public static class CompressException7zTest extends CompressExceptionGetItemInformationTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.SEVEN_ZIP;
        }
    }

    public static class CompressExceptionZipTest extends CompressExceptionGetItemInformationTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.ZIP;
        }
    }

    public static class CompressExceptionGZipTest extends CompressExceptionGetItemInformationTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.GZIP;
        }
    }

    public static class CompressExceptionBZip2Test extends CompressExceptionGetItemInformationTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.BZIP2;
        }
    }

    public static class CompressExceptionTarTest extends CompressExceptionGetItemInformationTest {

        @Override
        protected ArchiveFormat getArchiveFormat() {
            return ArchiveFormat.TAR;
        }
    }

    @Test
    public void testCompressionOk() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionOk();
            }
        });
    }

    @Test
    public void testCompressionOkMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestCompressionOk();
            }
        });
    }

    @Test
    public void testUpdateOk() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestUpdateOk();
            }
        });
    }

    @Test
    public void testUpdateOkMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doTestUpdateOk();
            }
        });
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

    @Test
    public void testIsNewDataIsNullCompression() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doIsNewDataIsNullCompression();
            }
        });
    }

    @Test
    public void testIsNewDataIsNullCompressionMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doIsNewDataIsNullCompression();
            }
        });
    }

    @Test
    public void testIsNewDataIsNullUpdate() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doIsNewDataIsNullUpdate();
            }
        });
    }

    @Test
    public void testIsNewDataIsNullUpdateMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doIsNewDataIsNullUpdate();
            }
        });
    }

    @Test
    public void testIsNewPropertiesIsNullCompression() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doIsNewPropertiesIsNullCompression();
            }
        });
    }

    @Test
    public void testIsNewPropertiesIsNullCompressionMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doIsNewPropertiesIsNullCompression();
            }
        });
    }

    @Test
    public void testIsNewPropertiesIsNullUpdate() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doIsNewPropertiesIsNullUpdate();
            }
        });
    }

    @Test
    public void testIsNewPropertiesIsNullUpdateMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doIsNewPropertiesIsNullUpdate();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexIsNullCompression() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexIsNullCompression();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexIsNullCompressionMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexIsNullCompression();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexIsNullUpdate() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexIsNullUpdate();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexIsNullUpdateMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexIsNullUpdate();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexNotSetButIsNewDataIsFalseCompression() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexNotSetButIsNewDataIsFalseCompression();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexNotSetButIsNewDataIsFalseCompressionMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexNotSetButIsNewDataIsFalseCompression();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexNotSetButIsNewDataIsFalseUpdate() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexNotSetButIsNewDataIsFalseUpdate();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexNotSetButIsNewDataIsFalseUpdateMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexNotSetButIsNewDataIsFalseUpdate();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseCompression() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseCompression();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseCompressionMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseCompression();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseUpdate() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseUpdate();
            }
        });
    }

    @Test
    public void testOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseUpdateMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseUpdate();
            }
        });
    }

    @Test
    public void testNewDataWithoutNewPropretiesCompression() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doNewDataWithoutNewPropretiesCompression();
            }
        });
    }

    @Test
    public void testNewDataWithoutNewPropretiesCompressionMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doNewDataWithoutNewPropretiesCompression();
            }
        });
    }

    @Test
    public void testNewDataWithoutNewPropretiesUpdate() throws Exception {
        testSingleOrMultithreaded(false, new RunnableThrowsException() {
            public void run() throws Exception {
                doNewDataWithoutNewPropretiesUpdate();
            }
        });
    }

    @Test
    public void testNewDataWithoutNewPropretiesUpdateMultithreaded() throws Exception {
        testSingleOrMultithreaded(true, new RunnableThrowsException() {
            public void run() throws Exception {
                doNewDataWithoutNewPropretiesUpdate();
            }
        });
    }

    private void doTestCompressionOk() throws Exception {
        compress(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    fail("No exception expected");
                }
            }
        });
    }

    private void doTestUpdateOk() throws Exception {
        update(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    fail("No exception expected");
                }
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

                public ISequentialInStream getStream(int index) throws SevenZipException {
                    return null;
                }

                public IOutItemAllFormats getItemInformation(int index,
                        OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                    return null; // Return null here!
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

    private void doIsNewDataIsNullCompression() throws Exception {
        compress(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewData(null);
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    fail("No exception expected (not in update mode)");
                }
            }
        });
    }

    private void doIsNewDataIsNullUpdate() throws Exception {
        update(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewData(null);
                return outItem;
            }

            public void verifyException(Exception e) {
                assertSevenZipException(e, "updateIsNewData can't be null");
            }
        });
    }

    private void doIsNewPropertiesIsNullCompression() throws Exception {
        compress(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewProperties(null);
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    fail("No exception expected (not in update mode)");
                }
            }
        });
    }

    private void doIsNewPropertiesIsNullUpdate() throws Exception {
        update(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewProperties(null);
                return outItem;
            }

            public void verifyException(Exception e) {
                assertSevenZipException(e, "updateIsNewProperties can't be null");
            }
        });
    }

    private void doOldArchiveItemIndexIsNullCompression() throws Exception {
        compress(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateOldArchiveItemIndex(null);
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    fail("No exception expected (not in update mode)");
                }
            }
        });
    }

    private void doOldArchiveItemIndexIsNullUpdate() throws Exception {
        update(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateOldArchiveItemIndex(null);
                return outItem;
            }

            public void verifyException(Exception e) {
                assertSevenZipException(e, "updateOldArchiveItemIndex can't be null");
            }
        });
    }
    private void doOldArchiveItemIndexNotSetButIsNewDataIsFalseCompression() throws Exception {
        compress(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewData(false);
                outItem.setUpdateOldArchiveItemIndex(-1);
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    fail("No exception expected (not in update mode)");
                }
            }
        });
    }

    private void doOldArchiveItemIndexNotSetButIsNewDataIsFalseUpdate() throws Exception {
        update(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewData(false);
                outItem.setUpdateOldArchiveItemIndex(-1);
                return outItem;
            }

            public void verifyException(Exception e) {
                assertSevenZipException(e, "updateOldArchiveItemIndex must be provided (updateIsNewData is false)");
            }

        });
    }

    private void doOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseCompression() throws Exception {
        compress(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewProperties(false);
                outItem.setUpdateOldArchiveItemIndex(-1);
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    fail("No exception expected (not in update mode)");
                }
            }
        });
    }

    private void doOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseUpdate() throws Exception {
        update(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewProperties(false);
                outItem.setUpdateOldArchiveItemIndex(-1);
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e == null) {
                    fail("Exception expected (isNewProperties==false but oldIndex not set)");
                }
                assertTrue(e instanceof SevenZipException);
                assertTrue(e.getMessage().startsWith("HRESULT: 0x1 (FALSE). Error creating"));

                Throwable cause = ((SevenZipException) e).getCause();
                assertEquals("updateOldArchiveItemIndex must be provided (updateIsNewProperties is false)",
                        cause.getMessage());
            }
        });
    }
    private void doNewDataWithoutNewPropretiesCompression() throws Exception {
        compress(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewData(true);
                outItem.setUpdateIsNewProperties(false);
                outItem.setUpdateOldArchiveItemIndex(0);
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    fail("No exception expected (not in update mode)");
                }
            }
        });
    }

    private void doNewDataWithoutNewPropretiesUpdate() throws Exception {
        update(new IOutItemModifier() {
            public IOutItemAllFormats modify(IOutItemAllFormats outItem) {
                outItem.setUpdateIsNewData(true);
                outItem.setUpdateIsNewProperties(false);
                outItem.setUpdateOldArchiveItemIndex(0);
                return outItem;
            }

            public void verifyException(Exception e) {
                if (e == null) {
                    fail("Exception expected (isNewProperties==false but newData==true)");
                }
                assertTrue(e instanceof SevenZipException);
                assertTrue(e.getMessage().startsWith("HRESULT: 0x1 (FALSE). Error creating"));

                Throwable cause = ((SevenZipException) e).getCause();
                assertEquals("updateIsNewProperties must be set (updateIsNewData is true)", cause.getMessage());
            }
        });
    }

    private void assertSevenZipException(Exception e, String expected) {
        if (e == null) {
            fail("Exception expected (isNewData==false but oldIndex not set)");
        }
        assertTrue(e instanceof SevenZipException);
        assertTrue(e.getMessage().startsWith("HRESULT: 0x1 (FALSE). Error creating"));

        Throwable cause = ((SevenZipException) e).getCause();
        assertEquals(expected, cause.getMessage());
    }

    private void compress(final IOutItemModifier outItemModifier) throws Exception {
        Exception exception = null;
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

                public ISequentialInStream getStream(int index) throws SevenZipException {
                    return new ByteArrayStream("C".getBytes(), false);
                }

                public IOutItemAllFormats getItemInformation(int index,
                        OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                    IOutItemAllFormats outItem = outItemFactory.createOutItem();
                    outItem.setDataSize(1L);
                    outItem.setPropertyPath("A");
                    outItem.setPropertyLastModificationTime(new Date());
                    return outItemModifier.modify(outItem);
                }
            });
        } catch (SevenZipException sevenZipException) {
            exception = sevenZipException;
        }
        outItemModifier.verifyException(exception);
    }

    private void update(final IOutItemModifier outItemModifier) throws Exception {
        ByteArrayStream simpleArchiveByteArrayStream = createSimpleArchive();
        Exception exception = null;
        try {
            IInArchive inArchive = SevenZip.openInArchive(getArchiveFormat(), simpleArchiveByteArrayStream);
            addCloseable(inArchive);

            IOutUpdateArchive<IOutItemAllFormats> outArchive = inArchive
                    .getConnectedOutArchive();
            ByteArrayStream byteArrayStream = new ByteArrayStream(10000);
            outArchive.updateItems(byteArrayStream, 1, new IOutCreateCallback<IOutItemAllFormats>() {
                public void setOperationResult(boolean operationResultOk) throws SevenZipException {

                }

                public void setTotal(long total) throws SevenZipException {

                }

                public void setCompleted(long complete) throws SevenZipException {

                }

                public ISequentialInStream getStream(int index) throws SevenZipException {
                    return new ByteArrayStream("C".getBytes(), false);
                }

                public IOutItemAllFormats getItemInformation(int index,
                        OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                    IOutItemAllFormats outItem = outItemFactory.createOutItem();
                    outItem.setDataSize(1L);
                    outItem.setPropertyPath("A");
                    outItem.setPropertyLastModificationTime(new Date());
                    return outItemModifier.modify(outItem);
                }
            });
        } catch (SevenZipException sevenZipException) {
            exception = sevenZipException;
        }
        outItemModifier.verifyException(exception);
    }

    private ByteArrayStream createSimpleArchive() throws SevenZipException {
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

            public ISequentialInStream getStream(int index) throws SevenZipException {
                return new ByteArrayStream("C".getBytes(), false);
            }

            public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                    throws SevenZipException {
                IOutItemAllFormats outItem = outItemFactory.createOutItem();
                outItem.setDataSize(1L);
                outItem.setPropertyPath("A");
                outItem.setPropertyLastModificationTime(new Date());
                return outItem;
            }
        });
        byteArrayStream.rewind();
        return byteArrayStream;
    }
}

