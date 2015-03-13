package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallbackZip;
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
 * Create Zip archive using specific {@link IOutCreateCallback}&lt;{@link IOutItemCallbackZip}&gt; interface.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public class StandaloneCompressZipTest extends JUnitNativeTestBase {
    private final class OutItemCallbackZip implements IOutItemCallbackZip {
        private int index;

        public void setIndex(int index) {
            this.index = index;
        }

        public long getSize() throws SevenZipException {
            return virtualContent.getItemStream(index).getSize();
        }

        public String getPath() throws SevenZipException {
            return virtualContent.getItemPath(index);
        }

        public Integer getAttributes() throws SevenZipException {
            return null;
        }

        public boolean isDir() throws SevenZipException {
            return false;
        }

        public boolean isNtfsTime() throws SevenZipException {
            return true;
        }

        public Date getModificationTime() throws SevenZipException {
            return substructDate(DATE, 1);
        }

        public Date getLastAccessTime() throws SevenZipException {
            return DATE;
        }

        public Date getCreationTime() throws SevenZipException {
            return substructDate(DATE, 2);
        }
    }
    private class OutCreateArchiveZip implements IOutCreateCallback<IOutItemCallbackZip> {


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

        public IOutItemCallbackZip getOutItemCallback(final int index) throws SevenZipException {
            outItemCallbackTar.setIndex(index);
            return callbackTesterItem.getProxyInstance();
        }
    }

    static final Date DATE = new Date();

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchiveZip> callbackTesterCreateArchive = new CallbackTester<OutCreateArchiveZip>(
            new OutCreateArchiveZip());

    OutItemCallbackZip outItemCallbackTar = new OutItemCallbackZip();
    CallbackTester<OutItemCallbackZip> callbackTesterItem = new CallbackTester<OutItemCallbackZip>(outItemCallbackTar);

    @Test
    public void testCompressionZip() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(100, 3, 3, 100, 50, null);

        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);

        IOutCreateArchiveZip outNewArchiveZip = closeLater(SevenZip.openOutArchiveZip());

        outNewArchiveZip.setLevel(5);

        assertEquals(ArchiveFormat.ZIP, outNewArchiveZip.getArchiveFormat());

        outNewArchiveZip.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getProxyInstance());

        assertEquals(5, callbackTesterCreateArchive.getDifferentMethodsCalled());

        assertEquals(IOutItemCallbackZip.class.getDeclaredMethods().length,
                callbackTesterItem.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.ZIP, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }

    Date substructDate(Date date, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}
