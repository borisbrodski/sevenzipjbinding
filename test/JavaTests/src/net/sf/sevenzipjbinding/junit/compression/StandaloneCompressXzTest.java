package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchiveXz;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemXz;
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
 * Create Xz archive using specific {@link IOutCreateCallback}&lt;{@link IOutItemXz}&gt; interface.
 *
 * @author Boris Brodski
 * @since 23.01
 */
public class StandaloneCompressXzTest extends JUnitNativeTestBase<VoidContext>{
    private class OutCreateArchiveXz implements IOutCreateCallback<IOutItemXz> {


        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }


        public IOutItemXz getItemInformation(int index, OutItemFactory<IOutItemXz> outItemFactory)
                throws SevenZipException {
            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            byteArrayStream.rewind();

            IOutItemXz outItem = outItemFactory.createOutItem();

            outItem.setDataSize((long) byteArrayStream.getSize());

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return virtualContent.getItemStream(index);
        }
    }

    static final Date DATE = new Date();

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchiveXz> callbackTesterCreateArchive = new CallbackTester<OutCreateArchiveXz>(
            new OutCreateArchiveXz());

    @Test
    public void testCompressionXz() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(1, 0, 0, 100, 50, null, false);

        ByteArrayStream byteArrayStream = new ByteArrayStream(1000000);

        IOutCreateArchiveXz outNewArchiveXz = closeLater(SevenZip.openOutArchiveXz());

        outNewArchiveXz.setLevel(5);

        assertEquals(ArchiveFormat.XZ, outNewArchiveXz.getArchiveFormat());

        outNewArchiveXz.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getProxyInstance());

        assertEquals("Methods called: " + callbackTesterCreateArchive, 5,
                callbackTesterCreateArchive.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.XZ, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }

    Date substructDate(Date date, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}
