package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchiveGZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemGZip;
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
 * Create GZip archive using specific {@link IOutCreateCallback}&lt;{@link IOutItemCallbackGZip}&gt; interface.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class StandaloneCompressGZipTest extends JUnitNativeTestBase<VoidContext> {
    private class OutCreateArchiveGZip implements IOutCreateCallback<IOutItemGZip> {


        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItemGZip getItemInformation(int index, OutItemFactory<IOutItemGZip> outItemFactory)
                throws SevenZipException {
            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            byteArrayStream.rewind();

            IOutItemGZip outItem = outItemFactory.createOutItem();

            outItem.setDataSize((long) byteArrayStream.getSize());
            outItem.setPropertyPath(virtualContent.getItemPath(index));
            outItem.setPropertyLastModificationTime(new Date());

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return virtualContent.getItemStream(index);
        }
    }

    static final Date DATE = new Date();

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchiveGZip> callbackTesterCreateArchive = new CallbackTester<OutCreateArchiveGZip>(
            new OutCreateArchiveGZip());

    @Test
    public void testCompressionGZip() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(1, 0, 0, 100, 50, null, false);

        ByteArrayStream byteArrayStream = new ByteArrayStream(1000000);

        IOutCreateArchiveGZip outNewArchiveGZip = closeLater(SevenZip.openOutArchiveGZip());

        outNewArchiveGZip.setLevel(5);

        assertEquals(ArchiveFormat.GZIP, outNewArchiveGZip.getArchiveFormat());

        outNewArchiveGZip.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getProxyInstance());

        assertEquals(5, callbackTesterCreateArchive.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.GZIP, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }

    Date substructDate(Date date, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}
