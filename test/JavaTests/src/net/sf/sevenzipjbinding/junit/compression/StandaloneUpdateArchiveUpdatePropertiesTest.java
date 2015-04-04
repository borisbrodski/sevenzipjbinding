package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.IOutItemCallback7z;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.IOutUpdateCallbackGeneric;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;

public class StandaloneUpdateArchiveUpdatePropertiesTest extends JUnitNativeTestBase {
    private static class UpdateItemContentArchiveUpdateCallback implements IOutUpdateCallbackGeneric {
        private int itemToUpdate;
        private Map<PropID, Object> newProperties;
        private IInArchive inArchive;

        public UpdateItemContentArchiveUpdateCallback(IInArchive inArchive, int itemToUpdate,
                Map<PropID, Object> newProperties) {
            this.inArchive = inArchive;
            this.itemToUpdate = itemToUpdate;
            this.newProperties = newProperties;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            fail("Unexpected call of the method");
            return null;
        }

        public int getOldArchiveItemIndex(int index) {
            return index;
        }

        public boolean isNewData(int index) throws SevenZipException {
            return false;
        }

        public boolean isNewProperties(int index) throws SevenZipException {
            return index == itemToUpdate;
        }

        public Object getProperty(int index, PropID propID) throws SevenZipException {
            assertEquals(itemToUpdate, index);
            if (propID == PropID.SIZE) {
                fail("Unexpected getProperty(index, PropID.SIZE) call");
            }

            if (newProperties.containsKey(propID)) {
                return newProperties.get(propID);
            }

            return inArchive.getProperty(index, propID);
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

        String itemToUpdatePath = (String) inArchive.getProperty(itemToUpdate, PropID.PATH);

        IOutUpdateArchive<IOutItemCallback7z> outArchiveConnected = inArchive.getConnectedOutArchive();

        Map<PropID, Object> newProperties = new HashMap<PropID, Object>();
        String newPath = inArchive.getProperty(itemToUpdate, PropID.PATH) + "_changed";
        Date newLastModificationTime = new Date(System.currentTimeMillis() + random.get().nextInt(100000) + 100000);
        newProperties.put(PropID.PATH, newPath);
        newProperties.put(PropID.LAST_MODIFICATION_TIME, newLastModificationTime);

        outArchiveConnected.updateItems(byteArrayStream2, inArchive.getNumberOfItems(),
                new UpdateItemContentArchiveUpdateCallback(inArchive, itemToUpdate, newProperties));

        byteArrayStream2.rewind();

        IInArchive modifiedInArchive = closeLater(SevenZip.openInArchive(null, byteArrayStream2));

        try {
            virtualContent.verifyInArchive(modifiedInArchive);
            fail("The content of the item with id " + itemToUpdate + " should differ");
        } catch (SevenZipException e) {
            AssertionError error = getExceptionCauseByClass(AssertionError.class, e);
            assertEquals("Directory passed to extraction (or index for path not found: '" + newPath + "')",
                    error.getMessage());
            // continue
        }

        virtualContent.updateItemLastModificationTimeByPath(itemToUpdatePath, newLastModificationTime);
        virtualContent.updateItemPathByPath(itemToUpdatePath, newPath);
        virtualContent.verifyInArchive(modifiedInArchive);
    }


    private ByteArrayStream compressVirtualContext(VirtualContent virtualContent) throws SevenZipException {
        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive<IOutItemCallback> outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP));
        virtualContent.createOutArchive(outArchive, byteArrayStream);
        return byteArrayStream;
    }
}
