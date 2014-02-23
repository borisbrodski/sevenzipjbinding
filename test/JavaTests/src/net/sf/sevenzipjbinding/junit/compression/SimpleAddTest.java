package net.sf.sevenzipjbinding.junit.compression;

import java.util.Date;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ArchiveUpdateCallbackAdapter;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

public class SimpleAddTest extends JUnitNativeTestBase {
    private static final byte[] NEW_FILE_BLOB = "Content".getBytes();
    private static final String NEW_FILE_PATH = "myNewFile";
    private static final boolean PRINT = !true;

    private static class AddItemArchiveUpdateCallback extends ArchiveUpdateCallbackAdapter {
        private int itemToAdd;
        private IInArchive inArchive;
        private String path;
        private byte[] blob;

        public AddItemArchiveUpdateCallback(IInArchive inArchive, int itemToAdd, String path, byte[] blob) {
            this.inArchive = inArchive;
            this.itemToAdd = itemToAdd;
            this.path = path;
            this.blob = blob;
        }

        @Override
        public Object getProperty(int index, PropID propID) throws SevenZipException {
            System.out.println(index + " -> " + propID);
            System.out.flush();
            if (index == itemToAdd) {
                switch (propID) {
                case PATH:
                    return path;
                case SIZE:
                    return Long.valueOf(blob.length);
                case LAST_MODIFICATION_TIME:
                    return new Date();
                case IS_FOLDER:
                    return false;
                default:
                }
                return null;
            }
            return inArchive.getProperty(getOldArchiveItemIndex(index), propID);
        }

        @Override
        public ISequentialInStream getStream(int index) throws SevenZipException {
            System.out.println("getStream(" + index + ")");
            System.out.flush();
            if (index == itemToAdd) {
                return new ByteArrayStream(blob, false);
            }
            return null;
        }

        @Override
        public int getOldArchiveItemIndex(int index) {
            System.out.print("getOldArchiveItemIndex(" + index + ")");
            if (index < itemToAdd) {
                System.out.println(" -> " + index);
                System.out.flush();
                return index;
            }
            if (index == itemToAdd) {
                System.out.println(" -> -1");
                System.out.flush();
                return -1;
            }
            System.out.println(" -> " + (index - 1));
            System.out.flush();
            return index - 1;
        }

        @Override
        public boolean isNewData(int index) throws SevenZipException {
            return index == itemToAdd;
        }

        @Override
        public boolean isNewProperties(int index) throws SevenZipException {
            return index == itemToAdd;
        }
    }

    @Test
    public void addFileFromArchive() throws Exception {
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

        outArchive2.updateItems(byteArrayStream2, inArchive.getNumberOfItems() + 1, new AddItemArchiveUpdateCallback(
                inArchive, 1, NEW_FILE_PATH, NEW_FILE_BLOB));

        byteArrayStream2.rewind();

        IInArchive inArchive2 = closeLater(SevenZip.openInArchive(null, byteArrayStream2));
        if (PRINT) {
            int numberOfItems = inArchive2.getNumberOfItems();
            System.out.println("------------: " + numberOfItems);
            for (int i = 0; i < numberOfItems; i++) {
                System.out.println(inArchive2.getProperty(i, PropID.PATH));
            }
        }
        virtualContent.addItem(1, NEW_FILE_PATH, NEW_FILE_BLOB);
        virtualContent.verifyInArchive(inArchive2);
    }
}
