package net.sf.sevenzipjbinding.junit.bug;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * A split archive (<code>x.7z.001</code>, <code>x.7z.002</code>, ...) opened natively with an
 * {@link IArchiveOpenVolumeCallback}: the split handler doesn't know the number of volumes and asks for the next one
 * until the callback returns <code>null</code>. In 23.01-2.2 the native code turned that <code>null</code> into an
 * error ("returns stream=null for volume 'x.7z.005'"), so no split archive could be opened this way.
 */
public class OpenSplitArchiveWithVolumeCallbackTest extends JUnitNativeTestBase<VoidContext> {
    private static final String DIRECTORY = "testdata/snippets";
    private static final String FIRST_VOLUME = "multipart-7z.7z.001";
    private static final int VOLUME_COUNT = 4;

    private static class VolumeCallback implements IArchiveOpenCallback, IArchiveOpenVolumeCallback {
        private final String missingVolume;
        private final List<String> requested = new ArrayList<String>();
        private final List<RandomAccessFile> opened = new ArrayList<RandomAccessFile>();
        private String currentName = FIRST_VOLUME;

        VolumeCallback(String missingVolume) {
            this.missingVolume = missingVolume;
        }

        public Object getProperty(PropID propID) {
            return propID == PropID.NAME ? currentName : null;
        }

        public IInStream getStream(String filename) throws SevenZipException {
            String name = new File(filename).getName();
            requested.add(name);
            if (name.equals(missingVolume)) {
                return null;
            }
            try {
                RandomAccessFile file = new RandomAccessFile(new File(DIRECTORY, name), "r");
                opened.add(file);
                currentName = name;
                return new RandomAccessFileInStream(file);
            } catch (FileNotFoundException e) {
                return null;
            }
        }

        public void setTotal(Long files, Long bytes) {
        }

        public void setCompleted(Long files, Long bytes) {
        }

        void close() throws IOException {
            for (RandomAccessFile file : opened) {
                file.close();
            }
        }
    }

    private static long volumeSizes(int count) {
        long size = 0;
        for (int i = 1; i <= count; i++) {
            size += new File(DIRECTORY, String.format("multipart-7z.7z.%03d", Integer.valueOf(i))).length();
        }
        return size;
    }

    private static byte[] openAndExtract(VolumeCallback callback) throws Exception {
        RandomAccessFile firstVolume = new RandomAccessFile(new File(DIRECTORY, FIRST_VOLUME), "r");
        try {
            IInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.SPLIT, new RandomAccessFileInStream(
                    firstVolume), callback);
            try {
                assertEquals(1, inArchive.getNumberOfItems());
                final ByteArrayStream joined = new ByteArrayStream(1024 * 1024);
                ExtractOperationResult result = inArchive.extractSlow(0, new ISequentialOutStream() {
                    public int write(byte[] data) throws SevenZipException {
                        return joined.write(data);
                    }
                });
                assertEquals(ExtractOperationResult.OK, result);
                assertEquals(inArchive.getProperty(0, PropID.SIZE), Long.valueOf(joined.getSize()));
                byte[] bytes = new byte[(int) joined.getSize()];
                joined.rewind();
                joined.read(bytes);
                return bytes;
            } finally {
                inArchive.close();
            }
        } finally {
            firstVolume.close();
            callback.close();
        }
    }

    @Test
    public void allVolumesAreJoined() throws Exception {
        VolumeCallback callback = new VolumeCallback(null);
        byte[] joined = openAndExtract(callback);
        assertEquals(volumeSizes(VOLUME_COUNT), joined.length);
        assertEquals("[multipart-7z.7z.002, multipart-7z.7z.003, multipart-7z.7z.004, multipart-7z.7z.005]",
                callback.requested.toString());

        // The joined volumes are the complete 7z archive
        IInArchive sevenZip = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new ByteArrayStream(joined, false));
        try {
            assertEquals(ExtractOperationResult.OK, sevenZip.extractSlow(0, new ISequentialOutStream() {
                public int write(byte[] data) {
                    return data.length;
                }
            }));
        } finally {
            sevenZip.close();
        }
    }

    @Test
    public void joiningStopsAtTheFirstMissingVolume() throws Exception {
        VolumeCallback callback = new VolumeCallback("multipart-7z.7z.003");
        byte[] joined = openAndExtract(callback);
        assertEquals(volumeSizes(2), joined.length);
        assertEquals("[multipart-7z.7z.002, multipart-7z.7z.003]", callback.requested.toString());
    }
}
