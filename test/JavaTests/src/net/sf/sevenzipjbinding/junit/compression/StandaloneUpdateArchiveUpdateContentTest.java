package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

public class StandaloneUpdateArchiveUpdateContentTest extends JUnitNativeTestBase {
    private static class UpdateItemContentArchiveUpdateCallback implements IOutUpdateCallback<IOutItemCallback7z> {
        private int itemToUpdate;
        private byte[] newContent;

        public UpdateItemContentArchiveUpdateCallback(int itemToUpdate, byte[] newContent) {
            this.itemToUpdate = itemToUpdate;
            this.newContent = newContent;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            assertEquals(itemToUpdate, index);
            return new ByteArrayStream(newContent, false);
        }

        public int getOldArchiveItemIndex(int index) {
            return index;
        }

        public boolean isNewData(int index) throws SevenZipException {
            return index == itemToUpdate;
        }

        public boolean isNewProperties(int index) throws SevenZipException {
            return false;
        }

        public IOutItemCallback7z getOutItemCallback(int index) throws SevenZipException {
            assertEquals(itemToUpdate, index);
            return new IOutItemCallback7z() {

                public boolean isDir() throws SevenZipException {
                    fail("Unexpected call");
                    return false;
                }

                public boolean isAnti() throws SevenZipException {
                    fail("Unexpected call");
                    return false;
                }

                public long getSize() throws SevenZipException {
                    return newContent.length;
                }

                public String getPath() throws SevenZipException {
                    fail("Unexpected call");
                    return null;
                }

                public Date getModificationTime() throws SevenZipException {
                    fail("Unexpected call");
                    return null;
                }

                public Integer getAttributes() throws SevenZipException {
                    fail("Unexpected call");
                    return null;
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

    @Test
    public void removeFileFromArchive() throws Exception {
        VirtualContent virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(10, 1, 1, 100, 50, null);


        ByteArrayStream byteArrayStream = compressVirtualContext(virtualContent);
        byteArrayStream.rewind();

        ByteArrayStream byteArrayStream2 = new ByteArrayStream(100000);

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));
        int itemToUpdate = virtualContent.getItemCount() / 2;
        byte[] newContent = new byte[random.get().nextInt(1024) + 1024];
        random.get().nextBytes(newContent);

        String itemToRemovePath = (String) inArchive.getProperty(itemToUpdate, PropID.PATH);

        IOutUpdateArchive<IOutItemCallback7z> outArchiveConnected = inArchive.getConnectedOutArchive();

        outArchiveConnected.updateItems(byteArrayStream2, inArchive.getNumberOfItems(),
                new UpdateItemContentArchiveUpdateCallback(itemToUpdate, newContent));

        byteArrayStream2.rewind();

        IInArchive modifiedInArchive = closeLater(SevenZip.openInArchive(null, byteArrayStream2));

        try {
            virtualContent.verifyInArchive(modifiedInArchive);
            fail("The content of the item with id " + itemToUpdate + " should differ");
        } catch (SevenZipException e) {
            AssertionError error = getExceptionCauseByClass(AssertionError.class, e);
            assertTrue(error.getMessage().contains("expected:<"));
            assertTrue(error.getMessage().contains("> but was:<"));
            // continue
        }

        virtualContent.updateItemContentByPath(itemToRemovePath, newContent);
        virtualContent.verifyInArchive(modifiedInArchive);
    }


    private ByteArrayStream compressVirtualContext(VirtualContent virtualContent) throws SevenZipException {
        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive<IOutItemCallback> outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP));
        virtualContent.createOutArchive(outArchive, byteArrayStream);
        return byteArrayStream;
    }
}
