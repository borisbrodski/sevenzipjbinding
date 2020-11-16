package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.IOutUpdateArchiveTar;
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

public class StandaloneUpdateNonGenericTarTest extends JUnitNativeTestBase<VoidContext>{
    private static class UpdateItemContentArchiveUpdateCallback implements IOutCreateCallback<IOutItemTar> {
        private int itemToUpdate;
        private byte[] newContent;

        public UpdateItemContentArchiveUpdateCallback(int itemToUpdate, byte[] newContent) {
            this.itemToUpdate = itemToUpdate;
            this.newContent = newContent;
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public IOutItemTar getItemInformation(int index, OutItemFactory<IOutItemTar> outItemFactory)
                throws SevenZipException {

            IOutItemTar outItem = outItemFactory.createOutItemAndCloneProperties(index);
            if (itemToUpdate == index) {
                outItem.setUpdateIsNewData(true);
                outItem.setDataSize((long) newContent.length);
            }

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            assertEquals(itemToUpdate, index);
            return new ByteArrayStream(newContent, false);
        }
    }

    @Test
    public void updateContent() throws Exception {
        // No SymLink/HardLink, since we only update content in this test
        VirtualContent virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(10, 1, 1, 100, 50, null, false);

        ByteArrayStream byteArrayStream = compressVirtualContext(virtualContent);
        byteArrayStream.rewind();

        ByteArrayStream byteArrayStream2 = new ByteArrayStream(100000);

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.TAR, byteArrayStream));
        int itemToUpdate = virtualContent.getItemCount() / 2;
        byte[] newContent = new byte[getRandom().nextInt(1024) + 1024];
        getRandom().nextBytes(newContent);

        String itemToRemovePath = (String) inArchive.getProperty(itemToUpdate, PropID.PATH);

        IOutUpdateArchiveTar outArchiveConnected = inArchive.getConnectedOutArchiveTar();

        //        outArchiveConnected.setTrace(true);

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
        IOutCreateArchive<IOutItemAllFormats> outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.TAR));
        virtualContent.createOutArchive(outArchive, byteArrayStream);
        return byteArrayStream;
    }
}
