package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.IOutItemCallback7z;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.IOutUpdateCallback;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

public class SimpleAddTest extends JUnitNativeTestBase {
    private static class AddItemArchiveUpdateCallback implements IOutUpdateCallback<IOutItemCallback7z> {
        private int itemToAdd;
        private String path;
        private byte[] blob;

        public AddItemArchiveUpdateCallback(int itemToAdd, String path, byte[] blob) {
            this.itemToAdd = itemToAdd;
            this.path = path;
            this.blob = blob;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            if (index == itemToAdd) {
                return new ByteArrayStream(blob, false);
            }
            return null;
        }

        public int getOldArchiveItemIndex(int index) {
            if (index == itemToAdd) {
                return -1; // New item
            }
            return index;
        }

        public boolean isNewData(int index) throws SevenZipException {
            return index == itemToAdd;
        }

        public boolean isNewProperties(int index) throws SevenZipException {
            return index == itemToAdd;
        }

        public IOutItemCallback7z getOutItemCallback(int index) throws SevenZipException {
            assertEquals(itemToAdd, index);
            return new IOutItemCallback7z() {

                public boolean isDir() throws SevenZipException {
                    return false;
                }

                public long getSize() throws SevenZipException {
                    return blob.length;
                }

                public String getPath() throws SevenZipException {
                    return path;
                }

                public Date getModificationTime() throws SevenZipException {
                    return new Date();
                }

                public Integer getAttributes() throws SevenZipException {
                    return 0;
                }

                public boolean isAnti() throws SevenZipException {
                    return false;
                }
            };
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {

        }
    }

    private static final byte[] NEW_FILE_BLOB = "Content".getBytes();
    private static final String NEW_FILE_PATH = "myNewFile";

    @Test
    public void addFileFromArchive() throws Exception {
        VirtualContent virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(3, 1, 1, 100, 50, null);

        ByteArrayStream byteArrayStream = compressVirtualContext(virtualContent);
        byteArrayStream.rewind();

        ByteArrayStream byteArrayStream2 = new ByteArrayStream(100000);

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));

        IOutUpdateArchive<IOutItemCallback7z> outArchiveConnected = inArchive.getConnectedOutArchive();

        outArchiveConnected.updateItems(byteArrayStream2, inArchive.getNumberOfItems() + 1,
                new AddItemArchiveUpdateCallback(inArchive.getNumberOfItems(), NEW_FILE_PATH, NEW_FILE_BLOB));

        byteArrayStream2.rewind();

        IInArchive modifiedInArchive = closeLater(SevenZip.openInArchive(null, byteArrayStream2));

        try {
            virtualContent.verifyInArchive(modifiedInArchive);
            fail("Archive should contain new unexpected item by now");
        } catch (SevenZipException e) {
            Throwable cause = e.getCause();
            while (cause != null && cause.getCause() != null) {
                cause = cause.getCause();
            }
            if (cause == null || !(cause instanceof AssertionError)) {
                e.printStackTrace();
                fail("Expected AssertionError exception");
            }
            assertEquals("Directory passed to extraction (or index for path not found: 'myNewFile')",
                    cause.getMessage());
            // continue
        }

        virtualContent.addItem(1, NEW_FILE_PATH, NEW_FILE_BLOB);
        virtualContent.verifyInArchive(modifiedInArchive);
    }

    private ByteArrayStream compressVirtualContext(VirtualContent virtualContent) throws SevenZipException {
        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive<IOutItemCallback> outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP));
        virtualContent.createOutArchive(outArchive, byteArrayStream);
        return byteArrayStream;
    }
}
