package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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

public class StandaloneUpdateArchiveRemoveTest extends JUnitNativeTestBase {
    private static class RemoveItemArchiveUpdateCallback implements IOutUpdateCallback<IOutItemCallback7z> {
        private int itemToRemove;

        public RemoveItemArchiveUpdateCallback(int itemToRemove) {
            this.itemToRemove = itemToRemove;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return null;
        }

        public int getOldArchiveItemIndex(int index) {
            if (index < itemToRemove) {
                return index;
            }
            return index + 1;
        }

        public boolean isNewData(int index) throws SevenZipException {
            return false;
        }

        public boolean isNewProperties(int index) throws SevenZipException {
            return false;
        }

        public IOutItemCallback7z getOutItemCallback(int index) throws SevenZipException {
            return null;
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
        int itemToRemove = virtualContent.getItemCount() / 2;
        String itemToRemovePath = (String) inArchive.getProperty(itemToRemove, PropID.PATH);

        IOutUpdateArchive<IOutItemCallback7z> outArchiveConnected = inArchive.getConnectedOutArchive();

        outArchiveConnected.updateItems(byteArrayStream2, inArchive.getNumberOfItems() - 1,
                new RemoveItemArchiveUpdateCallback(itemToRemove));

        byteArrayStream2.rewind();

        IInArchive modifiedInArchive = closeLater(SevenZip.openInArchive(null, byteArrayStream2));

        // for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
        //     System.out.println(i + ": " + inArchive.getProperty(i, PropID.PATH));
        // }
        // for (int i = 0; i < modifiedInArchive.getNumberOfItems(); i++) {
        //     System.out.println(i + ": " + modifiedInArchive.getProperty(i, PropID.PATH));
        // }
        // virtualContent.print();

        try {
            virtualContent.verifyInArchive(modifiedInArchive);
            fail("Archive shouldn't contain item with id " + itemToRemove);
        } catch (AssertionError e) {
            e.printStackTrace();
            assertEquals("Item '" + itemToRemovePath + "' wasn't extracted", e.getMessage());
            // continue
        }

        virtualContent.deleteItemByPath(itemToRemovePath);
        virtualContent.verifyInArchive(modifiedInArchive);
    }

    private ByteArrayStream compressVirtualContext(VirtualContent virtualContent) throws SevenZipException {
        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive<IOutItemCallback> outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP));
        virtualContent.createOutArchive(outArchive, byteArrayStream);
        return byteArrayStream;
    }
}
