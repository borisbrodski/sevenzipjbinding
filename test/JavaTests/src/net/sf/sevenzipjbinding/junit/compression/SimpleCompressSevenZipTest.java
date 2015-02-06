package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallback7z;
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
public class SimpleCompressSevenZipTest extends JUnitNativeTestBase {
    private class OutCreateArchive7z implements IOutCreateCallback<IOutItemCallback7z> {

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

        public IOutItemCallback7z getOutItemCallback(final int index) throws SevenZipException {
            return new IOutItemCallback7z() {

                public boolean isAnti() throws SevenZipException {
                    return false;
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

                public Date getModificationTime() throws SevenZipException {
                    return new Date();
                }

                public boolean isDir() throws SevenZipException {
                    return false;
                }
            };
        }
    }

    static final Date DATE = new Date();

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchive7z> callbackTesterCreateArchive = new CallbackTester<OutCreateArchive7z>(
            new OutCreateArchive7z());

    @Test
    public void testCompression7z() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(100, 3, 3, 100, 50, null);

        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);

        IOutCreateArchive7z outNewArchive7z = closeLater(SevenZip.openOutArchive7z());

        outNewArchive7z.setLevel(5);
        outNewArchive7z.setSolid(true);
        outNewArchive7z.setSolidExtension(true);
        outNewArchive7z.setSolidFiles(3);
        outNewArchive7z.setSolidSize(1024);
        outNewArchive7z.setThreadCount(4);

        assertEquals(ArchiveFormat.SEVEN_ZIP, outNewArchive7z.getArchiveFormat());

        outNewArchive7z.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getInstance());

        // No setCompleted call
        assertEquals(4, callbackTesterCreateArchive.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }

    Date substructDate(Date date, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}
