package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class StandaloneUpdateArchiveRemoveTest extends JUnitNativeTestBase<VoidContext>{
    private static class RemoveItemArchiveUpdateCallback implements IOutCreateCallback<IOutItemAllFormats> {
        private int itemToRemove;

        public RemoveItemArchiveUpdateCallback(int itemToRemove) {
            this.itemToRemove = itemToRemove;
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            if (index < itemToRemove) {
                return outItemFactory.createOutItem(index);
            }
            return outItemFactory.createOutItem(index + 1);
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return null;
        }
    }

    @Test
    public void removeFileFromArchive() throws Exception {
        VirtualContent virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(10, 1, 1, 100, 50, null, false);


        ByteArrayStream byteArrayStream = compressVirtualContext(virtualContent);
        byteArrayStream.rewind();

        ByteArrayStream byteArrayStream2 = new ByteArrayStream(100000);

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));
        int itemToRemove = virtualContent.getItemCount() / 2;
        String itemToRemovePath = (String) inArchive.getProperty(itemToRemove, PropID.PATH);

        IOutUpdateArchive<IOutItemAllFormats> outArchiveConnected = inArchive.getConnectedOutArchive();

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
            assertEquals("Item '" + itemToRemovePath + "' wasn't extracted", e.getMessage());
            // continue
        }

        virtualContent.deleteItemByPath(itemToRemovePath);
        virtualContent.verifyInArchive(modifiedInArchive);
    }

    private ByteArrayStream compressVirtualContext(VirtualContent virtualContent) throws SevenZipException {
        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive<IOutItemAllFormats> outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP));
        virtualContent.createOutArchive(outArchive, byteArrayStream);
        return byteArrayStream;
    }
}
