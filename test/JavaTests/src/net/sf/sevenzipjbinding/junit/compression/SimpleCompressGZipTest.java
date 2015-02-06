package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchiveGZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallbackGZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.CallbackTester;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;


/**
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public class SimpleCompressGZipTest extends JUnitNativeTestBase {
    private class OutCreateArchiveGZip implements IOutCreateCallback<IOutItemCallbackGZip> {

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public ISequentialInStream getStream(int index) {
            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            byteArrayStream.rewind();
            return byteArrayStream;
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItemCallbackGZip getOutItemCallback(final int index) throws SevenZipException {
            return new IOutItemCallbackGZip() {

                public long getSize() throws SevenZipException {
                    return virtualContent.getItemStream(index).getSize();
                }
                public String getPath() throws SevenZipException {
                    return virtualContent.getItemPath(index);
                }
                public Date getModificationTime() throws SevenZipException {
                    return new Date();
                }
            };
        }
    }

    static final Date DATE = new Date();

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchiveGZip> callbackTesterCreateArchive = new CallbackTester<OutCreateArchiveGZip>(
            new OutCreateArchiveGZip());

    @Test
    public void testCompressionGZip() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(1, 0, 0, 100, 50, null);

        ByteArrayStream byteArrayStream = new ByteArrayStream(1000000);

        IOutCreateArchiveGZip outNewArchiveGZip = closeLater(SevenZip.openOutArchiveGZip());

        outNewArchiveGZip.setLevel(5);

        assertEquals(ArchiveFormat.GZIP, outNewArchiveGZip.getArchiveFormat());

        outNewArchiveGZip.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getInstance());

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
