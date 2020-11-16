package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

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
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests exceptions during archive compression.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressExceptionGetItemInformationTest extends CompressAbstractTest<VoidContext> {
    public interface IOutItemModifier {
        public IOutItemAllFormats modify(IOutItemAllFormats outItem);
        public void verifyException(Exception e);
    }

    private final ArchiveFormat archiveFormat;

    @Parameters
    public static Collection<ArchiveFormat> getLevels() {
        return Arrays.asList(//
                ArchiveFormat.SEVEN_ZIP, //
                ArchiveFormat.ZIP, //
                ArchiveFormat.BZIP2, //
                ArchiveFormat.GZIP,
                ArchiveFormat.TAR);
    }

    public CompressExceptionGetItemInformationTest(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    @Test
    @Multithreaded
    @Repeat
    public void testCompressionOk() throws Exception {
        doTestCompressionOk();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateOk() throws Exception {
        doTestUpdateOk();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testNullAs() throws Exception {
        doTestNullAs();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testIsNewDataIsNullCompression() throws Exception {
        doIsNewDataIsNullCompression();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testIsNewDataIsNullUpdate() throws Exception {
        doIsNewDataIsNullUpdate();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testIsNewPropertiesIsNullCompression() throws Exception {
        doIsNewPropertiesIsNullCompression();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testIsNewPropertiesIsNullUpdate() throws Exception {
        doIsNewPropertiesIsNullUpdate();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testOldArchiveItemIndexIsNullCompression() throws Exception {
        doOldArchiveItemIndexIsNullCompression();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testOldArchiveItemIndexIsNullUpdate() throws Exception {
        doOldArchiveItemIndexIsNullUpdate();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testOldArchiveItemIndexNotSetButIsNewDataIsFalseCompression() throws Exception {
        doOldArchiveItemIndexNotSetButIsNewDataIsFalseCompression();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testOldArchiveItemIndexNotSetButIsNewDataIsFalseUpdate() throws Exception {
        doOldArchiveItemIndexNotSetButIsNewDataIsFalseUpdate();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseCompression() throws Exception {
        doOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseCompression();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseUpdate() throws Exception {
        doOldArchiveItemIndexNotSetButIsNewPropertiesIsFalseUpdate();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testNewDataWithoutNewPropretiesCompression() throws Exception {
        doNewDataWithoutNewPropretiesCompression();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testNewDataWithoutNewPropretiesUpdate() throws Exception {
        doNewDataWithoutNewPropretiesUpdate();
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
                    + "the archive item being processed.", sevenZipException.getMessage());
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

            IOutUpdateArchive<IOutItemAllFormats> outArchive = inArchive.getConnectedOutArchive();
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
