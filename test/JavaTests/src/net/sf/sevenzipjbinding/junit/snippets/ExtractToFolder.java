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
 * Extract a WHOLE archive to a directory the fast, correct AND safe way.
 *
 * Speed - the golden rule for 7z (and any solid archive): extract everything in ONE call to
 * {@code IInArchive.extract(indices, false, callback)}. That streams the solid block a single time
 * (O(n)); item-by-item {@code extractSlow} re-decompresses the block up to each item (O(n^2)).
 *
 * Callback rules that keep it fast: open the output file ONCE in getStream(); write() fires MANY times
 * per file, just keep appending; close it in setOperationResult(); do no per-chunk work in the hot path.
 *
 * Correctness &amp; safety, because people copy examples:
 *   - SECURITY (Zip Slip): an archive entry can be {@code ../../etc/passwd} or absolute. Resolve each
 *     target's canonical path and REFUSE anything that escapes the output directory.
 *   - Errors arrive TWO ways: as a thrown exception (re-thrown from {@code extract()} = fatal) OR as a
 *     per-item non-OK {@code ExtractOperationResult} decided by the engine WITH NO EXCEPTION (CRC/data
 *     error), after which extraction CONTINUES. Record both, keep the healthy items, and report.
 *   - {@code IS_FOLDER} (and other properties) can be {@code null} for some formats: use
 *     {@code Boolean.TRUE.equals(...)}, never {@code ((Boolean) x).booleanValue()} (that NPEs).
 *   - Empty directories are real entries - create them, or they are lost.
 *   - Never let a {@code close()} error mask the real cause; propagate close only on a SUCCESSFUL item.
 *   - Exit non-zero when anything went wrong.
 */
public class ExtractToFolder {

    /** One thing that went wrong with one item. Non-fatal: other items still extract. */
    public static final class ItemError {
        public final int index;
        public final String path;
        public final String kind;    // human-readable: what went wrong
        public final Throwable cause; // may be null (e.g. an engine-reported result carries no exception)

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
        private final String outputDirCanonical;       // null if it could not be resolved
        private final String outputDirCanonicalPrefix; // outputDirCanonical + File.separator
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
            String canonical = null;
            try {
                canonical = outputDir.getCanonicalPath();
            } catch (IOException e) {
                canonical = null; // safeTarget() then refuses every item - we cannot validate paths
            }
            this.outputDirCanonical = canonical;
            this.outputDirCanonicalPrefix = canonical == null ? null : canonical + File.separator;
        }

        public List<ItemError> getItemErrors() {
            return itemErrors;
        }

        /** A property can be missing (null) in some formats; treat that as "not a folder". */
        public boolean isFolder(int index) throws SevenZipException {
            return Boolean.TRUE.equals(inArchive.getProperty(index, PropID.IS_FOLDER));
        }

        /**
         * Resolve a validated target File strictly under outputDir, or null (recording the problem) if the
         * entry would escape it - the Zip Slip defence - or its path cannot be resolved.
         */
        private File safeTarget(int index, String path) {
            if (outputDirCanonicalPrefix == null) {
                record(index, "cannot resolve output directory", null);
                return null;
            }
            File outFile = new File(outputDir, path);
            String canonical;
            try {
                canonical = outFile.getCanonicalPath();
            } catch (IOException e) {
                record(index, "cannot resolve output path", e);
                return null;
            }
            // Inside iff it IS the output dir (root) or lives under "<outputDir>/".
            if (!canonical.equals(outputDirCanonical) && !canonical.startsWith(outputDirCanonicalPrefix)) {
                record(index, "blocked unsafe path (would write outside the output directory)", null);
                return null;
            }
            return outFile;
        }

        /** Create an (empty) directory entry safely - so empty directories are not lost. */
        public void createDirectory(int index) {
            String path;
            try {
                path = inArchive.getStringProperty(index, PropID.PATH);
            } catch (SevenZipException e) {
                record(index, "cannot read directory name", e);
                return;
            }
            File dir = safeTarget(index, path);
            if (dir == null) {
                return; // unsafe / unresolvable - already recorded
            }
            if (!dir.isDirectory() && !dir.mkdirs()) {
                record(index, "cannot create directory", null);
            }
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
            if (isFolder(index)) {
                return null; // directories are created by createDirectory()/the file parents below
            }

            String path = inArchive.getStringProperty(index, PropID.PATH);
            File outFile = safeTarget(index, path);
            if (outFile == null) {
                return null; // unsafe or unresolvable - recorded, skip so the rest still extracts
            }
            try {
                File parent = outFile.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new IOException("cannot create directory " + parent);
                }
                // OPEN ONCE. Buffered so the many small write() chunks do not hit the disk each time.
                currentOut = new BufferedOutputStream(new FileOutputStream(outFile));
                currentFile = outFile;
            } catch (IOException e) {
                // Record and SKIP this item (return null). Throwing here would abort the WHOLE run.
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
                        // while nothing was written. The engine saves this and re-throws it from extract().
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
            // Close first. Keep any close error aside - it must be RECORDED, but must not overwrite the
            // primary cause of a failed item.
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
                // Capture it, or it slips silently. (If write() already recorded the real cause, skip.)
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
     * Extract every file of an open archive into {@code outputDir}, safely; print one row per file
     * extracted. NEVER throws: a run-aborting error is captured as {@link Report#fatalError}, per-item
     * problems (including blocked unsafe paths) as {@link Report#itemErrors}.
     */
    public static Report extractAll(IInArchive inArchive, File outputDir) {
        ExtractToFolderCallback callback = new ExtractToFolderCallback(inArchive, outputDir);
        SevenZipException fatal = null;
        try {
            int count = inArchive.getNumberOfItems();
            List<Integer> fileItems = new ArrayList<Integer>();
            for (int i = 0; i < count; i++) {
                if (callback.isFolder(i)) {
                    callback.createDirectory(i); // materialise (empty) directories too
                } else {
                    fileItems.add(Integer.valueOf(i));
                }
            }
            int[] items = new int[fileItems.size()];
            for (int i = 0; i < items.length; i++) {
                items[i] = fileItems.get(i).intValue();
            }
            inArchive.extract(items, false, callback); // ONE call: solid-safe, O(n)
        } catch (SevenZipException e) {
            fatal = e; // a run-aborting error (or an item exception the engine re-raised)
        } catch (RuntimeException e) {
            // Keep the "never throws" promise even if a property lookup on a broken archive misbehaves.
            fatal = new SevenZipException("Unexpected error during extraction: " + e, e);
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
        // The single most useful stack trace: the fatal error if there was one, otherwise the first item
        // error that actually carries a cause (an engine-reported result may have none).
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

    /** Returns a process exit code: 0 = clean, 1 = something went wrong, 2 = wrong usage. */
    public static int run(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ExtractToFolder <archive> <output-directory>");
            return 2;
        }
        File outputDir = new File(args[1]);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(args[0], "r");
                IInArchive inArchive = SevenZip.openInArchive(null, // autodetect archive type
                        new RandomAccessFileInStream(randomAccessFile))) {

            System.out.println("   Hash   |    Size    | Filename");
            System.out.println("----------+------------+---------");

            Report report = extractAll(inArchive, outputDir);
            printReport(report);
            return report.isClean() ? 0 : 1;
        } catch (SevenZipException e) {
            // Neutral wording: this also catches a failure while CLOSING the archive at the end.
            System.err.println("Archive error: " + e.getMessage());
            e.printStackTraceExtended();
            return 1;
        } catch (IOException e) {
            System.err.println("Archive error: " + e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        System.exit(run(args));
    }
}
/* END_SNIPPET */
