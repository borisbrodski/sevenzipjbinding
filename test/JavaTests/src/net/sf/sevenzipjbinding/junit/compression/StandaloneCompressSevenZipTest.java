package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.IOutUpdateArchive7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.tools.CallbackTester;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;


/**
 * Create 7-Zip archive using specific {@link IOutCreateCallback}&lt;{@link IOutItemCallback7z}&gt; interface.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class StandaloneCompressSevenZipTest extends JUnitNativeTestBase<VoidContext>{
    private static final String PASSWORD = "test-pass-321";

    private class OutCreateArchive7z implements IOutCreateCallback<IOutItem7z> {
        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory)
                throws SevenZipException {
            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            byteArrayStream.rewind();

            IOutItem7z outItem = outItemFactory.createOutItem();

            outItem.setDataSize((long) byteArrayStream.getSize());
            outItem.setPropertyPath(virtualContent.getItemPath(index));

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return virtualContent.getItemStream(index);
        }
    }

    private class OutCreateArchive7zWithPassword extends OutCreateArchive7z implements ICryptoGetTextPassword {
        public String cryptoGetTextPassword() throws SevenZipException {
            return PASSWORD;
        }
    }

    static final Date DATE = new Date();

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchive7z> callbackTesterCreateArchive = new CallbackTester<OutCreateArchive7z>(
            new OutCreateArchive7z());

    @Test
    public void testCompression7z() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(100, 3, 3, 100, 50, null, false);

        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);

        IOutCreateArchive7z outNewArchive7z = closeLater(SevenZip.openOutArchive7z());

        outNewArchive7z.setLevel(5);
        outNewArchive7z.setSolid(true);
        outNewArchive7z.setSolidExtension(true);
        outNewArchive7z.setSolidFiles(3);
        outNewArchive7z.setSolidSize(1024);
        outNewArchive7z.setThreadCount(4);

        assertEquals(ArchiveFormat.SEVEN_ZIP, outNewArchive7z.getArchiveFormat());

        outNewArchive7z.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getProxyInstance());

        assertEquals(5, callbackTesterCreateArchive.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }

    @Test
    public void testHeaderEncryptionWithOtherFeatures() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(10, 2, 2, 100, 50, null, false);

        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());

        // Regression test for issue #77: every one of these properties must be
        // retained when header encryption is enabled.
        outArchive.setLevel(5);
        outArchive.setSolid(true);
        outArchive.setSolidSize(8192);
        outArchive.setThreadCount(2);
        outArchive.setHeaderEncryption(true);

        outArchive.createArchive(byteArrayStream, virtualContent.getItemCount(), new OutCreateArchive7zWithPassword());
        outArchive.close();

        byteArrayStream.rewind();
        try {
            IInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream);
            inArchive.close();
            fail("Archive header should require a password");
        } catch (SevenZipException expected) {
        }

        byteArrayStream.rewind();
        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream, PASSWORD));
        virtualContent.verifyInArchive(inArchive, PASSWORD);
    }

    @Test
    public void testDictionarySizeWithOtherFeatures() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        // Every file is larger than the dictionary, so 7-Zip has no reason to use a smaller one.
        virtualContent.fillRandomly(4, 1, 1, 100000, 50000, null, false);

        ByteArrayStream byteArrayStream = new ByteArrayStream(1000000);
        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());

        // The dictionary size must be retained together with all other properties: 7-Zip resets every
        // property on each ISetProperties.SetProperties() call.
        outArchive.setLevel(5);
        outArchive.setDictionarySize(1 << 16);
        outArchive.setSolid(false);
        outArchive.setThreadCount(2);

        IInArchive inArchive = createAndOpen(outArchive, byteArrayStream);

        assertEquals(Boolean.FALSE, inArchive.getArchiveProperty(PropID.SOLID));
        // 7-Zip prints a power-of-two dictionary size as its exponent: 2^16 bytes.
        int countOfFiles = assertMethodOfEachFile(inArchive, "LZMA2:16");
        assertEquals(Integer.valueOf(countOfFiles), inArchive.getArchiveProperty(PropID.NUM_BLOCKS));
    }

    @Test
    public void testDictionarySizeInBytes() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        // Every file is larger than the dictionary, so 7-Zip has no reason to use a smaller one.
        virtualContent.fillRandomly(3, 1, 1, 300000, 50000, null, false);

        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());

        // The only setting, and not a power of two: 7-Zip must read the value as bytes, not as an exponent.
        outArchive.setDictionarySize(3 << 16);

        IInArchive inArchive = createAndOpen(outArchive, new ByteArrayStream(2 * 1024 * 1024));
        assertMethodOfEachFile(inArchive, "LZMA2:192k");
    }

    @Test
    public void testDictionarySizeIgnoredWithoutCompression() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(2, 1, 1, 1000, 500, null, false);

        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());
        outArchive.setDictionarySize(1 << 16);
        outArchive.setLevel(0);

        IInArchive inArchive = createAndOpen(outArchive, new ByteArrayStream(100000));
        assertMethodOfEachFile(inArchive, "Copy");
    }

    @Test
    public void testDictionarySizeOnUpdate() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(2, 1, 1, 300000, 50000, null, false);

        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());
        outArchive.setDictionarySize(1 << 16);
        outArchive.setSolid(false);
        IInArchive inArchive = createAndOpen(outArchive, new ByteArrayStream(2 * 1024 * 1024));

        // Add a file with another dictionary size. The blocks of the existing files are copied as they are.
        final int oldItemCount = inArchive.getNumberOfItems();
        final byte[] newFileContent = new byte[300000];
        Random random = new Random(0);
        for (int i = 0; i < newFileContent.length; i++) {
            newFileContent[i] = (byte) ('a' + random.nextInt(26));
        }
        IOutUpdateArchive7z outUpdateArchive = inArchive.getConnectedOutArchive7z();
        outUpdateArchive.setDictionarySize(3 << 16);
        outUpdateArchive.setSolid(false);
        ByteArrayStream updatedArchiveStream = new ByteArrayStream(2 * 1024 * 1024);
        outUpdateArchive.updateItems(updatedArchiveStream, oldItemCount + 1, new OutCreateArchive7z() {
            @Override
            public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory)
                    throws SevenZipException {
                if (index < oldItemCount) {
                    return outItemFactory.createOutItem(index);
                }
                IOutItem7z outItem = outItemFactory.createOutItem();
                outItem.setDataSize((long) newFileContent.length);
                outItem.setPropertyPath("new-file");
                return outItem;
            }

            @Override
            public ISequentialInStream getStream(int index) throws SevenZipException {
                return new ByteArrayStream(newFileContent, false);
            }
        });

        updatedArchiveStream.rewind();
        IInArchive updatedArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, updatedArchiveStream));
        assertEquals(Integer.valueOf(3), updatedArchive.getArchiveProperty(PropID.NUM_BLOCKS));
        int countOfNewFiles = 0;
        for (int i = 0; i < updatedArchive.getNumberOfItems(); i++) {
            if (Boolean.TRUE.equals(updatedArchive.getProperty(i, PropID.IS_FOLDER))) {
                continue;
            }
            boolean newFile = "new-file".equals(updatedArchive.getProperty(i, PropID.PATH));
            if (newFile) {
                countOfNewFiles++;
            }
            assertEquals(newFile ? "LZMA2:192k" : "LZMA2:16", updatedArchive.getProperty(i, PropID.METHOD));
        }
        assertEquals(1, countOfNewFiles);
    }

    @Test
    public void testDictionarySizeMustBePositiveOrDefault() throws Exception {
        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());
        for (long dictionarySize : new long[] { 0, -2, Long.MIN_VALUE }) {
            try {
                outArchive.setDictionarySize(dictionarySize);
                fail("Dictionary size " + dictionarySize + " should be rejected");
            } catch (IllegalArgumentException expected) {
            }
        }
        outArchive.setDictionarySize(-1);
    }

    @Test
    public void testUnsupportedDictionarySizeIsReported() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(2, 1, 1, 100, 50, null, false);

        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());
        outArchive.setLevel(5);
        outArchive.setDictionarySize(1L << 40); // 1 TiB: more than LZMA2 supports
        try {
            outArchive.createArchive(new ByteArrayStream(100000), virtualContent.getItemCount(),
                    callbackTesterCreateArchive.getProxyInstance());
            fail("Unsupported dictionary size accepted");
        } catch (SevenZipException expected) {
            // 7-Zip accepts the property and rejects the value once the encoder is set up
        }
    }

    @Test
    public void testDefaultsAfterReusingArchiveObject() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(3, 1, 1, 100000, 50000, null, false);

        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());
        outArchive.setDictionarySize(1 << 16);
        outArchive.setSolid(false);
        IInArchive firstArchive = createAndOpen(outArchive, new ByteArrayStream(1000000));
        assertEquals(Integer.valueOf(3), firstArchive.getArchiveProperty(PropID.NUM_BLOCKS));

        // Back to the defaults: the second archive must not get the properties of the first one
        outArchive.setDictionarySize(-1);
        outArchive.setSolid(true);
        IInArchive secondArchive = createAndOpen(outArchive, new ByteArrayStream(1000000));
        assertEquals(Boolean.TRUE, secondArchive.getArchiveProperty(PropID.SOLID));
        assertEquals(Integer.valueOf(1), secondArchive.getArchiveProperty(PropID.NUM_BLOCKS));
    }

    @Test
    public void testDefaultsAfterRejectedProperties() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(2, 1, 1, 1000, 500, null, false);

        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());
        outArchive.setLevel(0);
        outArchive.setSolidFiles(-2); // "-2F": rejected by 7-Zip after it has applied the level
        try {
            outArchive.createArchive(new ByteArrayStream(100000), virtualContent.getItemCount(),
                    callbackTesterCreateArchive.getProxyInstance());
            fail("Invalid solid spec accepted");
        } catch (SevenZipException expected) {
        }

        // Back to the defaults: compressed with LZMA2, not stored as with level 0
        outArchive.setLevel(-1);
        outArchive.setSolidFiles(-1);
        IInArchive inArchive = createAndOpen(outArchive, new ByteArrayStream(100000));
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            if (!Boolean.TRUE.equals(inArchive.getProperty(i, PropID.IS_FOLDER))) {
                String method = (String) inArchive.getProperty(i, PropID.METHOD);
                assertTrue(method, method.startsWith("LZMA2"));
            }
        }
    }

    private IInArchive createAndOpen(IOutCreateArchive7z outArchive, ByteArrayStream byteArrayStream)
            throws Exception {
        outArchive.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getProxyInstance());

        byteArrayStream.rewind();
        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
        return inArchive;
    }

    /**
     * @return count of the files (not folders) in the archive
     */
    private int assertMethodOfEachFile(IInArchive inArchive, String expectedMethod) throws SevenZipException {
        int countOfFiles = 0;
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            if (Boolean.TRUE.equals(inArchive.getProperty(i, PropID.IS_FOLDER))) {
                continue;
            }
            countOfFiles++;
            assertEquals(expectedMethod, inArchive.getProperty(i, PropID.METHOD));
        }
        assertTrue(countOfFiles > 0);
        return countOfFiles;
    }

    Date substructDate(Date date, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}
