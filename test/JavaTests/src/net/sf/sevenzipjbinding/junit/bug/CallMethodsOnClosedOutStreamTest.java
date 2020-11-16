package net.sf.sevenzipjbinding.junit.bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.RandomAccessFile;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;

public class CallMethodsOnClosedOutStreamTest extends JUnitNativeTestBase<VoidContext> {
    public interface OutArchiveMethodCall {
        void callMethod(IOutCreateArchive<IOutItemAllFormats> outArchive) throws SevenZipException;
    }

    public interface OutUpdateArchiveMethodCall {
        void callMethod(IOutUpdateArchive<IOutItemAllFormats> outArchive) throws SevenZipException;
    }

    @Test
    public void testCreateArchive() throws Throwable {
        testCreateWithMethod(ArchiveFormat.SEVEN_ZIP, new OutArchiveMethodCall() {
            public void callMethod(IOutCreateArchive<IOutItemAllFormats> outArchive) throws SevenZipException {
                outArchive.createArchive(null, 0, null);
            }
        });
    }

    @Test
    public void testUpdateArchive() throws Throwable {
        testUpdateWithMethod(new OutUpdateArchiveMethodCall() {
            public void callMethod(IOutUpdateArchive<IOutItemAllFormats> outArchive) throws SevenZipException {
                outArchive.updateItems(null, 0, null);
            }
        });
    }

    private void testCreateWithMethod(ArchiveFormat archiveFormat, OutArchiveMethodCall call) throws Exception {
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(archiveFormat);
        outArchive.close();

        try {
            call.callMethod(outArchive);
            fail("Calling methods on closed OutArchive should throw the SevenZipException");
        } catch (SevenZipException e) {
            assertEquals("OutArchive closed", e.getMessage());
        }
    }

    private void testUpdateWithMethod(OutUpdateArchiveMethodCall call) throws Exception {
        RandomAccessFile randomAccessFile = null;

        randomAccessFile = new RandomAccessFile("testdata/bug/TarArchiveWith7zInside.tar", "r");
        addCloseable(randomAccessFile);

        final IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
        addCloseable(inArchive);

        IOutUpdateArchive<IOutItemAllFormats> connectedOutArchive = inArchive.getConnectedOutArchive();

        inArchive.close();
        removeCloseable(inArchive);

        try {
            call.callMethod(connectedOutArchive);
            fail("Calling methods on closed InArchive should throw the SevenZipException");
        } catch (SevenZipException e) {
            assertEquals("InArchive closed", e.getMessage());
        }
    }
}
