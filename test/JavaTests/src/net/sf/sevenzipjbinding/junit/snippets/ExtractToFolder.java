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
 * Extract a WHOLE archive to a directory the fast, correct way, with production-grade error handling.
 *
 * Speed - the golden rule for 7z (and any solid archive): extract everything in ONE call to
 * {@code IInArchive.extract(indices, false, callback)}. That streams the solid block a single time
 * (O(n)). Extracting item-by-item with {@code extractSlow} re-decompresses the block up to each item,
 * which is O(n^2) and turns a 17-second job into an hour on a solid archive with many files.
 *
 * Callback rules that keep it fast: open the output file ONCE in getStream(); write() fires MANY times
 * per file, just keep appending; close it in setOperationResult(); do no per-chunk work in the hot path.
 *
 * Correctness - errors can arrive TWO different ways, and robust code handles BOTH:
 *   1. As a thrown exception (e.g. our own {@code write()} fails - disk full). 7-Zip-JBinding saves it
 *      and re-throws it from {@code extract()} - a FATAL error that ends the run.
 *   2. As a per-item {@code ExtractOperationResult} that is NOT {@code OK} (CRC error, data error, ...),
 *      decided by the 7-Zip engine WITH NO EXCEPTION AT ALL - {@code extract()} returns normally and
 *      extraction CONTINUES to the healthy items. If you only catch exceptions, these slip silently.
 * So: record every problem (per item, and a close() failure separately), keep extracting the healthy
 * items, and at the end report all of them plus one representative stack trace. Which of the two ways a
 * given failure takes can even change between 7-Zip engine versions - handling both is what keeps this
 * correct across upgrades.
 */
public class ExtractToFolder {

    /** One thing that went wrong with one item. Non-fatal: other items still extract. */
    public static final class ItemError {
        public final int index;
        public final String path;
        public final String kind;    // human-readable: what went wrong
        public final Throwable cause; // may be null (engine-reported result with no exception)

        ItemError(int index, String path, String kind, Throwable cause) {
            this.index = index;
            this.path = path;
            this.kind = kind;
            this.cause = cause;
        }

        public String toString() {
            return "[#" + index + " " + path + "] " + kind + (cause != null ? ": " + cause.getMessage() : "");
        }
    }

    /** Outcome of an extraction: every per-item problem, plus the fatal error that ended the run (if any). */
    public static final class Report {
        public final List<ItemError> itemErrors;
        public final SevenZipException fatalError; // non-null if extract() itself threw

        Report(List<ItemError> itemErrors, SevenZipException fatalError) {
            this.itemErrors = itemErrors;
            this.fatalError = fatalError;
        }

        public boolean isClean() {
            return itemErrors.isEmpty() && fatalError == null;
        }
    }

    public static class ExtractToFolderCallback implements IArchiveExtractCallback {
        private final IInArchive inArchive;
        private final File outputDir;
        private final List<ItemError> itemErrors = new ArrayList<ItemError>();

        private OutputStream currentOut; // opened once per file in getStream(), closed in setOperationResult()
        private File currentFile;        // non-null only while an output file is open for the current item
        private int currentIndex;
        private int currentHash;          // only to make this example's output verifiable
        private long currentSize;
        private boolean recordedForCurrent; // already logged a problem for the current item?

        public ExtractToFolderCallback(IInArchive inArchive, File outputDir) {
            this.inArchive = inArchive;
            this.outputDir = outputDir;
        }

        public List<ItemError> getItemErrors() {
            return itemErrors;
        }

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode)
                throws SevenZipException {
            closeQuietly(); // release any leftover stream before we move to a new item
            currentIndex = index;
            currentFile = null;
            recordedForCurrent = false;
            if (extractAskMode != ExtractAskMode.EXTRACT) {
                return null; // testing/skip pass - nothing to write
            }
            if (((Boolean) inArchive.getProperty(index, PropID.IS_FOLDER)).booleanValue()) {
                return null; // directories are created lazily below, from each file's path
            }

            String path = inArchive.getStringProperty(index, PropID.PATH);
            File outFile = new File(outputDir, path);
            try {
                File parent = outFile.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new IOException("cannot create directory " + parent);
                }
                // OPEN ONCE. Buffered so the many small write() chunks do not hit the disk each time.
                currentOut = new BufferedOutputStream(new FileOutputStream(outFile));
                currentFile = outFile;
            } catch (IOException e) {
                // Record and SKIP this item (return null) so the rest of the archive still extracts.
                // Throwing here would abort the WHOLE run for one bad output path.
                record(index, "cannot open output file", e);
                return null;
            }
            currentHash = 0;
            currentSize = 0;

            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    try {
                        currentOut.write(data); // append - write() can fire many times per file
                    } catch (IOException e) {
                        // Record the REAL cause, then throw: we must not tell the engine "wrote N bytes"
                        // while nothing was written (that is silent corruption). The engine saves this
                        // and re-throws it from extract() as the fatal error.
                        record(currentIndex, "cannot write output file", e);
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
            // Close first (releases the handle and, on success, flushes the final block). Keep any
            // close error aside - it must be RECORDED, but must not overwrite the primary cause.
            OutputStream out = currentOut;
            currentOut = null;
            IOException closeError = null;
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    closeError = e;
                }
            }

            if (extractOperationResult != ExtractOperationResult.OK) {
                // The engine decided this item failed - possibly with NO exception (CRC/data error).
                // Capture it, or it slips silently. (If write() already recorded the real cause for
                // this item, don't duplicate it.)
                if (!recordedForCurrent) {
                    record(currentIndex, "engine reported " + extractOperationResult, null);
                }
                if (closeError != null) {
                    record(currentIndex, "cannot close output file", closeError);
                }
                return;
            }

            if (closeError != null) {
                // Result was OK, but the final flush failed - the "successful" file is truncated.
                record(currentIndex, "cannot flush/close output file", closeError);
                return;
            }

            if (currentFile != null && !recordedForCurrent) {
                System.out.println(String.format("%9X | %10s | %s", currentHash, currentSize,
                        inArchive.getStringProperty(currentIndex, PropID.PATH)));
            }
        }

        /** Close the current output file WITHOUT masking a prior error (best effort). Safe when nothing is open. */
        public void closeQuietly() {
            OutputStream out = currentOut;
            currentOut = null;
            if (out == null) {
                return;
            }
            try {
                out.close();
            } catch (IOException e) {
                // The primary cause is already recorded/propagating; a close failure on an already-broken
                // file is noise - swallow it, but never leak the handle.
            }
        }

        private void record(int index, String kind, Throwable cause) {
            recordedForCurrent = true;
            String path;
            try {
                path = inArchive.getStringProperty(index, PropID.PATH);
            } catch (Exception e) {
                path = "#" + index; // property lookup can itself fail on a broken archive
            }
            itemErrors.add(new ItemError(index, path, kind, cause));
        }

        public void setCompleted(long completeValue) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }
    }

    /**
     * Extract every file of an open archive into {@code outputDir}, printing one row per successfully
     * extracted file. Never throws: a run-aborting error is captured as {@link Report#fatalError},
     * per-item errors as {@link Report#itemErrors}. The caller decides how to react.
     */
    public static Report extractAll(IInArchive inArchive, File outputDir) {
        ExtractToFolderCallback callback = new ExtractToFolderCallback(inArchive, outputDir);
        SevenZipException fatal = null;
        try {
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
            // ONE call, non-test mode: solid-safe, O(n).
            inArchive.extract(items, false, callback);
        } catch (SevenZipException e) {
            fatal = e; // a run-aborting error (or an item exception the engine re-raised)
        } finally {
            callback.closeQuietly(); // guarantee no output file is left open, even on an abort path
        }
        return new Report(callback.getItemErrors(), fatal);
    }

    /** Print a human report: every item problem, the fatal error if any, and ONE representative trace. */
    public static void printReport(Report report) {
        if (report.isClean()) {
            return; // all good - the per-file success rows are the whole story
        }
        System.err.println("=== extraction problems ===");
        for (ItemError e : report.itemErrors) {
            System.err.println("  " + e);
        }
        if (report.fatalError != null) {
            System.err.println("  FATAL (aborted the run): " + report.fatalError.getMessage());
        }
        // The single most useful stack trace: the fatal error if there was one, otherwise the first
        // item error that actually carries a cause (an engine-reported result may have none).
        Throwable primary = report.fatalError;
        if (primary == null) {
            for (ItemError e : report.itemErrors) {
                if (e.cause != null) {
                    primary = e.cause;
                    break;
                }
            }
        }
        if (primary instanceof SevenZipException) {
            ((SevenZipException) primary).printStackTraceExtended(); // shows the whole cause chain
        } else if (primary != null) {
            primary.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ExtractToFolder <archive> <output-directory>");
            return;
        }
        File outputDir = new File(args[1]);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(args[0], "r");
                IInArchive inArchive = SevenZip.openInArchive(null, // autodetect archive type
                        new RandomAccessFileInStream(randomAccessFile))) {

            System.out.println("   Hash   |    Size    | Filename");
            System.out.println("----------+------------+---------");

            Report report = extractAll(inArchive, outputDir);
            printReport(report);
        } catch (SevenZipException e) {
            System.err.println("Cannot open archive: " + e.getMessage());
            e.printStackTraceExtended();
        } catch (IOException e) {
            System.err.println("Cannot open archive file: " + e.getMessage());
        }
    }
}
/* END_SNIPPET */
