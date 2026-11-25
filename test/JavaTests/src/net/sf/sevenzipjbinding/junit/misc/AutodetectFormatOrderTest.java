package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Order of the archive format autodetection (issue #55). A hybrid ISO-9660/UDF image (genisoimage -udf, the usual
 * way to put files of 4 GiB and more on a DVD image) opens with the ISO handler and with the UDF handler. The ISO-9660
 * directory record stores a 32-bit size only, the UDF tree the real one, so 7-Zip tries UDF before ISO. Up to
 * 26.06-2.3 the binding tried the formats in registration order and reported such images as ISO with sizes modulo 4
 * GiB (a 4.00 GB file became 8 MB).
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public class AutodetectFormatOrderTest extends JUnitNativeTestBase<VoidContext> {
    private static final String TEST_DATA_PATH = "testdata/misc/autodetect/";
    private static final String HYBRID_IMAGE = TEST_DATA_PATH + "hybrid-iso-udf.iso";
    private static final String PLAIN_IMAGE = TEST_DATA_PATH + "plain-iso.iso";
    private static final String README = "readme.txt";

    @Test
    public void hybridIsoUdfImageIsDetectedAsUdf() throws Exception {
        IInArchive inArchive = open(HYBRID_IMAGE, null);
        assertEquals(ArchiveFormat.UDF, inArchive.getArchiveFormat());
        assertReadmeExtracts(inArchive);
    }

    @Test
    public void plainIsoImageIsDetectedAsIso() throws Exception {
        IInArchive inArchive = open(PLAIN_IMAGE, null);
        assertEquals(ArchiveFormat.ISO, inArchive.getArchiveFormat());
        assertReadmeExtracts(inArchive);
    }

    @Test
    public void hybridImageStillOpensAsIsoOnRequest() throws Exception {
        IInArchive inArchive = open(HYBRID_IMAGE, ArchiveFormat.ISO);
        assertEquals(ArchiveFormat.ISO, inArchive.getArchiveFormat());
        assertReadmeExtracts(inArchive);
    }

    private IInArchive open(String path, ArchiveFormat format) throws Exception {
        RandomAccessFileInStream inStream = new RandomAccessFileInStream(new RandomAccessFile(path, "r"));
        addCloseable(inStream);
        IInArchive inArchive = SevenZip.openInArchive(format, inStream);
        addCloseable(inArchive);
        return inArchive;
    }

    private void assertReadmeExtracts(IInArchive inArchive) throws Exception {
        int readmeIndex = -1;
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            if (README.equals(inArchive.getStringProperty(i, PropID.PATH))) {
                readmeIndex = i;
            }
        }
        assertTrue(README + " not found in the image", readmeIndex >= 0);
        ByteArrayStream outStream = new ByteArrayStream(1000);
        assertEquals(ExtractOperationResult.OK, inArchive.extractSlow(readmeIndex, outStream));
        assertArrayEquals(readFile(new File(TEST_DATA_PATH + README)),
                Arrays.copyOf(outStream.getBytes(), (int) outStream.getSize()));
    }

    private static byte[] readFile(File file) throws Exception {
        byte[] data = new byte[(int) file.length()];
        FileInputStream in = new FileInputStream(file);
        try {
            int read = 0;
            while (read < data.length) {
                int count = in.read(data, read, data.length - read);
                if (count < 0) {
                    break;
                }
                read += count;
            }
        } finally {
            in.close();
        }
        return data;
    }
}
