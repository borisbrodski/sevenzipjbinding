package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

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

public class StandaloneUpdateArchiveUpdatePropertiesTest extends JUnitNativeTestBase<VoidContext>{
    private String newPath;
    private Date newModificationTime;

    private class UpdateItemContentArchiveUpdateCallback implements IOutCreateCallback<IOutItemAllFormats> {
        private int itemToUpdate;

        public UpdateItemContentArchiveUpdateCallback(int itemToUpdate) {
            this.itemToUpdate = itemToUpdate;
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {

            if (index == itemToUpdate) {
                IOutItemAllFormats outItem = outItemFactory.createOutItemAndCloneProperties(index);

                outItem.setUpdateIsNewProperties(Boolean.TRUE);

                outItem.setPropertyPath(newPath);
                outItem.setPropertyLastModificationTime(newModificationTime);

                return outItem;
            }

            return outItemFactory.createOutItem(index);
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return null;
        }
    }

    @Test
    public void updatePropreties() throws Exception {
        VirtualContent virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(10, 1, 1, 100, 50, null, false);


        ByteArrayStream byteArrayStream = compressVirtualContext(virtualContent);
        byteArrayStream.rewind();

        ByteArrayStream byteArrayStream2 = new ByteArrayStream(100000);

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));
        int itemToUpdate = virtualContent.getItemCount() / 2;

        String itemToUpdatePath = (String) inArchive.getProperty(itemToUpdate, PropID.PATH);

        IOutUpdateArchive<IOutItemAllFormats> outArchiveConnected = inArchive.getConnectedOutArchive();

        newPath = inArchive.getProperty(itemToUpdate, PropID.PATH) + "_changed";
        newModificationTime = new Date(System.currentTimeMillis() + getRandom().nextInt(100000) + 100000);

        outArchiveConnected.updateItems(byteArrayStream2, inArchive.getNumberOfItems(),
                new UpdateItemContentArchiveUpdateCallback(itemToUpdate));

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

        virtualContent.updateItemLastModificationTimeByPath(itemToUpdatePath, newModificationTime);
        virtualContent.updateItemPathByPath(itemToUpdatePath, newPath);
        virtualContent.verifyInArchive(modifiedInArchive);
    }


    private ByteArrayStream compressVirtualContext(VirtualContent virtualContent) throws SevenZipException {
        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
        IOutCreateArchive<IOutItemAllFormats> outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP));
        virtualContent.createOutArchive(outArchive, byteArrayStream);
        return byteArrayStream;
    }
}
