package net.sf.sevenzipjbinding.junit.bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.RandomAccessFile;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;

public class CallMethodsOnClosedInStreamTest extends JUnitNativeTestBase<VoidContext> {
    public interface InArchiveMethodCall {
        void callMethod(IInArchive inArchive) throws SevenZipException;
    }

    @Test
    public void testExtract() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.extract(null, true, null);
            }
        });
    }

    @Test
    public void testExtractSlow() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.extractSlow(0, null);
            }
        });
    }

    @Test
    public void testExtractSlowWithPassword() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.extractSlow(0, null, "");
            }
        });
    }

    @Test
    public void testGetArchiveProperty() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getArchiveProperty(null);
            }
        });
    }

    @Test
    public void testGetStringArchiveProperty() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getStringArchiveProperty(null);
            }
        });
    }

    @Test
    public void testGetArchivePropertyInfo() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getArchivePropertyInfo(0);
            }
        });
    }

    @Test
    public void testGetNumberOfArchiveProperties() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getNumberOfArchiveProperties();
            }
        });
    }

    @Test
    public void testGetNumberOfProperties() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getNumberOfProperties();
            }
        });
    }

    @Test
    public void testGetPropertyInfo() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getPropertyInfo(0);
            }
        });
    }

    @Test
    public void testGetNumberOfItems() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getNumberOfItems();
            }
        });
    }

    @Test
    public void testGetProperty() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getProperty(0, null);
            }
        });
    }

    @Test
    public void testGetStringProperty() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getStringProperty(0, null);
            }
        });
    }

    @Test
    public void testGetConnectedOutArchive() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getConnectedOutArchive();
            }
        });
    }

    @Test
    public void testGetConnectedOutArchive7z() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getConnectedOutArchive7z();
            }
        });
    }

    @Test
    public void testGetConnectedOutArchiveZip() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getConnectedOutArchiveZip();
            }
        });
    }

    @Test
    public void testGetConnectedOutArchiveTar() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getConnectedOutArchiveTar();
            }
        });
    }

    @Test
    public void testGetConnectedOutArchiveGZip() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getConnectedOutArchiveGZip();
            }
        });
    }

    @Test
    public void testGetConnectedOutArchiveBZip2() throws Throwable {
        testWithMethod(new InArchiveMethodCall() {
            public void callMethod(IInArchive inArchive) throws SevenZipException {
                inArchive.getConnectedOutArchiveBZip2();
            }
        });
    }

    private void testWithMethod(InArchiveMethodCall call) throws Exception {
        RandomAccessFile randomAccessFile = null;

        randomAccessFile = new RandomAccessFile("testdata/bug/TarArchiveWith7zInside.tar", "r");
        addCloseable(randomAccessFile);

        final IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
        addCloseable(inArchive);

        assertEquals(ArchiveFormat.TAR, inArchive.getArchiveFormat());
        assertEquals(1, inArchive.getNumberOfItems());

        inArchive.close();
        removeCloseable(inArchive);

        try {
            call.callMethod(inArchive);
            fail("Calling methods on closed InArchive should throw the SevenZipException");
        } catch (SevenZipException e) {
            assertEquals("InArchive closed", e.getMessage());
        }
    }
}
