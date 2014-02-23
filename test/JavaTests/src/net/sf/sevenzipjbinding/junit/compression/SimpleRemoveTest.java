package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ArchiveUpdateCallbackAdapter;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

public class SimpleRemoveTest extends JUnitNativeTestBase {
    private static final boolean PRINT = !true;

    private static class DeleteItemArchiveUpdateCallback extends ArchiveUpdateCallbackAdapter {
        private int itemToDelete;
        private IInArchive inArchive;

        public DeleteItemArchiveUpdateCallback(IInArchive inArchive, int itemToDelete) {
            this.inArchive = inArchive;
            this.itemToDelete = itemToDelete;
        }

        @Override
        public Object getProperty(int index, PropID propID) throws SevenZipException {
            return inArchive.getProperty(getOldArchiveItemIndex(index), propID);
        }

        @Override
        public int getOldArchiveItemIndex(int index) {
            if (index < itemToDelete) {
                return index;
            }
            return index + 1;
        }
    }

    @Test
    public void removeFileFromArchive() throws Exception {
        VirtualContent virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(3, 1, 1, 100, 50, null);
        if (PRINT) {
            virtualContent.print();
        }

        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        ByteArrayStream byteArrayStream2 = new ByteArrayStream(100000);

        IOutArchive outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP));
        virtualContent.createOutArchive(outArchive, byteArrayStream);

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));

        IOutArchive outArchive2 = inArchive.getOutArchive();

        outArchive2.updateItems(byteArrayStream2, inArchive.getNumberOfItems() - 1,
                new DeleteItemArchiveUpdateCallback(inArchive, 1));

        byteArrayStream2.rewind();

        IInArchive inArchive2 = closeLater(SevenZip.openInArchive(null, byteArrayStream2));
        if (PRINT) {
            int numberOfItems = inArchive2.getNumberOfItems();
            System.out.println("------------: " + numberOfItems);
            for (int i = 0; i < numberOfItems; i++) {
                System.out.println(inArchive2.getProperty(i, PropID.PATH));
            }
        }
        virtualContent.deleteItem(1);
        virtualContent.verifyInArchive(inArchive2);
    }
}
