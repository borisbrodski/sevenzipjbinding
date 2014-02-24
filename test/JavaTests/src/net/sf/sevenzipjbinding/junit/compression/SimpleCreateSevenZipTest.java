//package net.sf.sevenzipjbinding.junit.compression;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import net.sf.sevenzipjbinding.ArchiveFormat;
//import net.sf.sevenzipjbinding.IInArchive;
//import net.sf.sevenzipjbinding.IOutArchive;
//import net.sf.sevenzipjbinding.IOutCreateArchive7z;
//import net.sf.sevenzipjbinding.IOutCreateCallback;
//import net.sf.sevenzipjbinding.IOutItemCallback7z;
//import net.sf.sevenzipjbinding.ISequentialInStream;
//import net.sf.sevenzipjbinding.SevenZip;
//import net.sf.sevenzipjbinding.SevenZipException;
//import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
//import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
//import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
//import net.sf.sevenzipjbinding.util.ByteArrayStream;
//
//import org.junit.Test;
//
///**
// *
// * @author Boris Brodski
// * @version 4.65-1
// */
//public class SimpleCreateSevenZipTest extends JUnitNativeTestBase {
//    private class OutCreateArchive7z implements IOutCreateCallback<IOutItemCallback7z> {
//
//        public void setTotal(long total) throws SevenZipException {
//            System.out.println("setTotal(" + total + ")");
//        }
//
//        public void setCompleted(long complete) throws SevenZipException {
//            System.out.println("setCompleted(" + complete + ")");
//        }
//
//        public ISequentialInStream getStream(int index) {
//            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
//            byteArrayStream.rewind();
//            return byteArrayStream;
//        }
//
//        public void setOperationResult(boolean operationResultOk) {
//            assertTrue(operationResultOk);
//        }
//
//        public long getSize(int index) throws SevenZipException {
//            return virtualContent.getItemStream(index).getSize();
//        }
//
//        public IOutItemCallback7z getOutItemCallback() throws SevenZipException {
//            return new IOutItemCallback7z() {
//
//                public boolean isAnti(int index) throws SevenZipException {
//                    return false;
//                }
//
//                public long getSize(int index) throws SevenZipException {
//                    return virtualContent.getItemStream(index).getSize();
//                }
//
//                public String getPath(int index) throws SevenZipException {
//                    return virtualContent.getItemPath(index);
//                }
//            };
//        }
//
//    }
//
//    VirtualContent virtualContent;
//
//    @Test
//    public void testCompressionSevenZip() throws Exception {
//        virtualContent = new VirtualContent(new VirtualContentConfiguration());
//        virtualContent.fillRandomly(100, 3, 3, 100, 50, null);
//
//        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
//
//        IOutCreateArchive7z outNewArchiveSevenZip = closeLater(SevenZip.openOutArchive(IOutCreateArchive7z.class));
//
//        outNewArchiveSevenZip.setLevel(5);
//        outNewArchiveSevenZip.setSolid(true);
//        outNewArchiveSevenZip.setSolidExtension(true);
//        outNewArchiveSevenZip.setSolidFiles(5);
//        outNewArchiveSevenZip.setSolidSize(100);
//        outNewArchiveSevenZip.setThreadCount(2);
//
//        assertEquals(ArchiveFormat.SEVEN_ZIP, outNewArchiveSevenZip.getArchiveFormat());
//
//        outNewArchiveSevenZip.createArchive(byteArrayStream, virtualContent.getItemCount(),
//                new OutCreateArchive7z());
//
//        byteArrayStream.rewind();
//
//        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, byteArrayStream));
//        virtualContent.verifyInArchive(inArchive);
//    }
//
//    @Test
//    public void test() throws Exception {
//        virtualContent = new VirtualContent(new VirtualContentConfiguration());
//        virtualContent.fillRandomly(100, 3, 3, 100, 50, null);
//
//        ByteArrayStream byteArrayStream = new ByteArrayStream(100000);
//
//        IOutArchive outArchive = closeLater(SevenZip.openOutArchive(ArchiveFormat.TAR));
//
//        outArchive.createArchive(byteArrayStream, virtualContent.getItemCount(), new OutCreateArchive7z());
//
//        byteArrayStream.rewind();
//
//        IInArchive inArchive = closeLater(SevenZip.openInArchive(null, byteArrayStream));
//        virtualContent.verifyInArchive(inArchive);
//    }
//}
