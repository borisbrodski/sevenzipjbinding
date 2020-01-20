package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchiveBZip2;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemBZip2;
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
 * Create BZip2 archive using specific {@link IOutCreateCallback}&lt;{@link IOutItemCallbackBZip2}&gt; interface.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class StandaloneCompressBZip2Test extends JUnitNativeTestBase<VoidContext>{
    private class OutCreateArchiveBZip2 implements IOutCreateCallback<IOutItemBZip2> {


        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }


        public IOutItemBZip2 getItemInformation(int index, OutItemFactory<IOutItemBZip2> outItemFactory)
                throws SevenZipException {
            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            byteArrayStream.rewind();

            IOutItemBZip2 outItem = outItemFactory.createOutItem();

            outItem.setDataSize((long) byteArrayStream.getSize());

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return virtualContent.getItemStream(index);
        }
    }

    static final Date DATE = new Date();

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchiveBZip2> callbackTesterCreateArchive = new CallbackTester<OutCreateArchiveBZip2>(
            new OutCreateArchiveBZip2());

    @Test
    public void testCompressionBZip2() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(1, 0, 0, 100, 50, null, false);

        ByteArrayStream byteArrayStream = new ByteArrayStream(1000000);

        IOutCreateArchiveBZip2 outNewArchiveBZip2 = closeLater(SevenZip.openOutArchiveBZip2());

        outNewArchiveBZip2.setLevel(5);

        assertEquals(ArchiveFormat.BZIP2, outNewArchiveBZip2.getArchiveFormat());

        outNewArchiveBZip2.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getProxyInstance());

        assertEquals("Methods called: " + callbackTesterCreateArchive, 5,
                callbackTesterCreateArchive.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.BZIP2, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }

    Date substructDate(Date date, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}
