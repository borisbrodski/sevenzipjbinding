package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(ExtractToFolder) */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

/**
 * Extract a WHOLE archive to a directory the fast, correct way.
 *
 * The golden rule for 7z (and any solid archive): extract everything in ONE call to
 * {@code IInArchive.extract(indices, false, callback)}. That streams the solid block a single time
 * (O(n)). Extracting item-by-item with {@code extractSlow} re-decompresses the block up to each item,
 * which is O(n^2) and turns a 17-second job into an hour on a solid archive with many files.
 *
 * Callback rules that keep it fast AND correct:
 *   - open the output file ONCE, in getStream();
 *   - write() may be called MANY times per file - just keep appending, never re-open;
 *   - close the file in setOperationResult();
 *   - do no per-chunk work (no logging, no hashing) in the hot path.
 */
public class ExtractToFolder {
    public static class ExtractToFolderCallback implements IArchiveExtractCallback {
        private final IInArchive inArchive;
        private final File outputDir;

        private OutputStream currentOut; // opened once per file in getStream()
        private int currentIndex;
        private int currentHash;         // only to make this example's output verifiable
        private long currentSize;

        public ExtractToFolderCallback(IInArchive inArchive, File outputDir) {
            this.inArchive = inArchive;
            this.outputDir = outputDir;
        }

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode)
                throws SevenZipException {
            currentIndex = index;
            if (extractAskMode != ExtractAskMode.EXTRACT) {
                return null; // testing/skip pass - nothing to write
            }
            if (((Boolean) inArchive.getProperty(index, PropID.IS_FOLDER)).booleanValue()) {
                return null; // directories are created lazily below, from each file's path
            }

            String path = inArchive.getStringProperty(index, PropID.PATH);
            File outFile = new File(outputDir, path);
            File parent = outFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new SevenZipException("Cannot create directory: " + parent);
            }
            try {
                // OPEN ONCE. Buffered so the many small write() chunks do not hit the disk each time.
                currentOut = new BufferedOutputStream(new java.io.FileOutputStream(outFile));
            } catch (IOException e) {
                throw new SevenZipException("Cannot open output file: " + outFile, e);
            }
            currentHash = 0;
            currentSize = 0;

            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    try {
                        currentOut.write(data); // append - write() can fire many times per file
                    } catch (IOException e) {
                        throw new SevenZipException("Cannot write output file", e);
                    }
                    currentHash ^= Arrays.hashCode(data);
                    currentSize += data.length;
                    return data.length; // must return the number of bytes consumed
                }
            };
        }

        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
        }

        public void setOperationResult(ExtractOperationResult extractOperationResult)
                throws SevenZipException {
            if (currentOut != null) {
                try {
                    currentOut.close(); // CLOSE HERE - one open/close per file, not per chunk
                } catch (IOException e) {
                    throw new SevenZipException("Cannot close output file", e);
                }
                currentOut = null;
            }
            if (extractOperationResult != ExtractOperationResult.OK) {
                System.err.println("Extraction error: " + extractOperationResult);
                return;
            }
            System.out.println(String.format("%9X | %10s | %s", currentHash, currentSize,
                    inArchive.getStringProperty(currentIndex, PropID.PATH)));
        }

        public void setCompleted(long completeValue) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ExtractToFolder <archive> <output-directory>");
            return;
        }
        File outputDir = new File(args[1]);
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(args[0], "r");
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));

            System.out.println("   Hash   |    Size    | Filename");
            System.out.println("----------+------------+---------");

            // Extract EVERY file in a single pass (solid-safe, O(n)).
            int count = inArchive.getNumberOfItems();
            List<Integer> indices = new ArrayList<Integer>();
            for (int i = 0; i < count; i++) {
                if (!((Boolean) inArchive.getProperty(i, PropID.IS_FOLDER)).booleanValue()) {
                    indices.add(Integer.valueOf(i));
                }
            }
            int[] items = new int[indices.size()];
            for (int i = 0; i < items.length; i++) {
                items[i] = indices.get(i).intValue();
            }
            inArchive.extract(items, false, // non-test mode: actually extract
                    new ExtractToFolderCallback(inArchive, outputDir));
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
    }
}
/* END_SNIPPET */
