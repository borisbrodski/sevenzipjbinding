package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(ExtractToFolder) */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
 *   - close the file when the item is done, in setOperationResult();
 *   - do no per-chunk work (no logging, no hashing) in the hot path.
 *
 * Error handling - the part that is easy to get wrong (see the notes inline):
 *   - When an item fails (e.g. the disk fills up mid-write), 7-Zip-JBinding SAVES that exception and
 *     re-throws it from {@code extract()} itself - so the REAL cause surfaces at the call site. Do not
 *     hide it: on a failed item, close the (already-broken) file QUIETLY, never throw a "cannot close"
 *     that would mask "no space left on device".
 *   - On a SUCCESSFUL item, close() is where the final buffered block is flushed - a failure there is
 *     genuine data loss, so let it propagate.
 *   - setOperationResult() is reliably called per item (even for a failed one, with a non-OK result),
 *     and extraction CONTINUES to the next item - but the callback contract does not promise it in
 *     every abort path, so main() also closes any still-open file in a finally. Belt and braces.
 */
public class ExtractToFolder {
    public static class ExtractToFolderCallback implements IArchiveExtractCallback {
        private final IInArchive inArchive;
        private final File outputDir;

        private OutputStream currentOut; // opened once per file in getStream(); closed once per file
        private File currentFile;        // remembered only for clear error messages
        private int currentIndex;
        private int currentHash;          // only to make this example's output verifiable
        private long currentSize;

        public ExtractToFolderCallback(IInArchive inArchive, File outputDir) {
            this.inArchive = inArchive;
            this.outputDir = outputDir;
        }

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode)
                throws SevenZipException {
            // Defensive: if a previous item's stream was somehow left open, release it before we
            // overwrite the field (a leak here would otherwise be invisible). It is already done or
            // broken, so closing quietly is correct.
            closeQuietly();

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
                currentOut = new BufferedOutputStream(new FileOutputStream(outFile));
            } catch (IOException e) {
                throw new SevenZipException("Cannot open output file: " + outFile, e);
            }
            currentFile = outFile;
            currentHash = 0;
            currentSize = 0;

            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    try {
                        currentOut.write(data); // append - write() can fire many times per file
                    } catch (IOException e) {
                        // The engine saves this and re-throws it from extract(); keep the OS message.
                        throw new SevenZipException("Cannot write " + currentFile + ": " + e.getMessage(), e);
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
            if (extractOperationResult != ExtractOperationResult.OK) {
                // This item failed. The underlying cause (e.g. the write error) is already recorded and
                // WILL surface from extract(); do not throw a close error on top of it and mask it.
                closeQuietly();
                System.err.println("Extraction failed for "
                        + inArchive.getStringProperty(currentIndex, PropID.PATH) + ": " + extractOperationResult);
                return;
            }
            // Success: close() flushes the last buffered block. A failure HERE is real data loss
            // (the file we just "succeeded" on is truncated) - so let it propagate.
            OutputStream out = currentOut;
            currentOut = null;
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new SevenZipException("Cannot flush/close " + currentFile + ": " + e.getMessage(), e);
                }
            }
            System.out.println(String.format("%9X | %10s | %s", currentHash, currentSize,
                    inArchive.getStringProperty(currentIndex, PropID.PATH)));
        }

        /**
         * Close the current output file WITHOUT masking a prior error (best effort). Used on a failed
         * item and as main()'s finally-time safety net. Safe to call when nothing is open.
         */
        public void closeQuietly() {
            OutputStream out = currentOut;
            currentOut = null;
            if (out == null) {
                return;
            }
            try {
                out.close();
            } catch (IOException e) {
                // The real cause (the write/space error) already propagates via extract(); a close
                // failure on an already-broken file is noise - swallow it, but don't leak the handle.
            }
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
        ExtractToFolderCallback callback = null;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(args[0], "r");
                IInArchive inArchive = SevenZip.openInArchive(null, // autodetect archive type
                        new RandomAccessFileInStream(randomAccessFile))) {

            callback = new ExtractToFolderCallback(inArchive, outputDir);

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
            inArchive.extract(items, false, callback); // non-test mode: actually extract
        } catch (SevenZipException e) {
            // The true root cause (e.g. "no space left on device") surfaces HERE, from extract().
            System.err.println("Extraction error: " + e.getMessage());
            e.printStackTraceExtended(); // prints every saved cause, in order - the real diagnosis
        } catch (IOException e) {
            System.err.println("Cannot open archive file: " + e.getMessage());
        } finally {
            // Guarantee no output file is left open, even on an abort path that skipped setOperationResult.
            if (callback != null) {
                callback.closeQuietly();
            }
        }
    }
}
/* END_SNIPPET */
