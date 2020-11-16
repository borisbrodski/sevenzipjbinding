package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchiveTar;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.ISequentialInStream;
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
 * Create Tar archive using specific {@link IOutCreateCallback}&lt;{@link IOutItemCallbackTar}&gt; interface.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class StandaloneCompressTarTest extends JUnitNativeTestBase<VoidContext>{
    private class OutCreateArchiveTar implements IOutCreateCallback<IOutItemTar> {
        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItemTar getItemInformation(int index, OutItemFactory<IOutItemTar> outItemFactory)
                throws SevenZipException {
            IOutItemTar outItem = outItemFactory.createOutItem();

            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            if (byteArrayStream != null) {
                byteArrayStream.rewind();
                outItem.setDataSize((long) byteArrayStream.getSize());
            } else {
                outItem.setDataSize(0L);
            }

            outItem.setPropertyUser("me");
            outItem.setPropertyGroup("developers");
            outItem.setPropertyLastModificationTime(new Date());
            outItem.setPropertyPath(virtualContent.getItemPath(index));
            outItem.setPropertySymLink(virtualContent.getItemSymLink(index));
            outItem.setPropertyHardLink(virtualContent.getItemHardLink(index));

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return virtualContent.getItemStream(index);
        }

    }

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchiveTar> callbackTesterArchive = new CallbackTester<OutCreateArchiveTar>(
            new OutCreateArchiveTar());


    @Test
    public void testCompressionTar() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(100, 3, 3, 100, 50, null, true);

        ByteArrayStream byteArrayStream = new ByteArrayStream(1000000);

        IOutCreateArchiveTar outNewArchiveTar = closeLater(SevenZip.openOutArchiveTar());

        assertEquals(ArchiveFormat.TAR, outNewArchiveTar.getArchiveFormat());

        outNewArchiveTar.createArchive(byteArrayStream, virtualContent.getItemCount(), //new OutCreateArchiveTar()
                callbackTesterArchive.getProxyInstance());

        assertEquals(5, callbackTesterArchive.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.TAR, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }

    Date substructDate(Date date, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}
