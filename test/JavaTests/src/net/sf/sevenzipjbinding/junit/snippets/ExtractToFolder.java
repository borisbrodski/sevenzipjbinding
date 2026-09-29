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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
 * Extract a whole archive into a directory - fast, safe, and honest about what happened.
 *
 * <p>An archive is untrusted input. This example therefore works in three separate phases, and the
 * separation is the point:
 * <ol>
 *   <li><b>Plan</b> - look at every item and decide, before a single byte is written, whether and
 *       where it may go. All the safety rules live here: path traversal, links, special files,
 *       nameless entries.</li>
 *   <li><b>Stream</b> - hand the engine ONE {@code extract()} call for all files. The callback does
 *       nothing but open a file once, append chunks, and close it. This is where the speed comes from.</li>
 *   <li><b>Account</b> - collect every problem, keep going where that is safe, and finish with a
 *       report and an exit code you can trust in a script.</li>
 * </ol>
 *
 * <p>Two facts about the 7-Zip engine drive the design and are worth remembering:
 * <ul>
 *   <li>Solid archives (7z by default) compress many files into one stream. Asking for the files one
 *       at a time re-decompresses that stream for every request - O(n^2). One {@code extract()} call
 *       streams it once.</li>
 *   <li>Failures reach you on two channels. An exception you throw from the callback is saved and
 *       re-thrown from {@code extract()}. But a broken item (CRC error, truncated data, ...) is
 *       reported per item as a non-OK {@link ExtractOperationResult} <em>with no exception at all</em>
 *       - {@code extract()} returns normally and continues with the next item. Code that only catches
 *       exceptions silently loses those.</li>
 * </ul>
 */
public class ExtractToFolder {

    // =============================================================================================
    // Result types
    // =============================================================================================

    /** One item that could not be extracted as intended. Never fatal: the other items still extract. */
    public static final class ItemError {
        public final int index;
        public final String path;    // the item's path as stored in the archive
        public final String kind;    // what went wrong, in plain words
        public final Throwable cause; // null when the engine reported a result without an exception

        ItemError(int index, String path, String kind, Throwable cause) {
            this.index = index;
            this.path = path;
            this.kind = kind;
            this.cause = cause;
        }

        @Override
        public String toString() {
            return "[#" + index + " " + path + "] " + kind + (cause != null ? ": " + cause.getMessage() : "");
        }
    }

    /** Everything a caller needs to decide what to do next. */
    public static final class Report {
        public final List<ItemError> itemErrors;
        public final int filesPlanned;       // files the plan phase approved for extraction
        public final int filesExtracted;     // files written completely and closed successfully
        public final int filesNotExtracted;  // approved files that never got a verdict (see writeFailed)
        public final boolean writeFailed;    // an output write failed (disk full, ...) and extraction stopped
        public final SevenZipException archiveError; // extract() failed for a reason we did not cause

        Report(List<ItemError> itemErrors, int filesPlanned, int filesExtracted, int filesNotExtracted,
                boolean writeFailed, SevenZipException archiveError) {
            this.itemErrors = Collections.unmodifiableList(itemErrors);
            this.filesPlanned = filesPlanned;
            this.filesExtracted = filesExtracted;
            this.filesNotExtracted = filesNotExtracted;
            this.writeFailed = writeFailed;
            this.archiveError = archiveError;
        }

        public boolean isClean() {
            return itemErrors.isEmpty() && archiveError == null && filesNotExtracted == 0;
        }
    }

    // =============================================================================================
    // The extractor: plan -> stream -> account
    // =============================================================================================

    public static class Extractor implements IArchiveExtractCallback {
        private static final boolean WINDOWS = File.separatorChar == '\\';

        private final IInArchive archive;
        private final File outputDir;      // canonical, absolute, exists, is a directory
        private final String fallbackName; // name for entries the archive does not name (see below)

        // plan
        private final Map<Integer, File> targets = new HashMap<Integer, File>();
        private final Map<Integer, String> displayNames = new HashMap<Integer, String>();
        private final List<ItemError> itemErrors = new ArrayList<ItemError>();
        private int filesPlanned;

        // stream (state of the item currently being written)
        private int currentIndex;
        private File currentFile;
        private OutputStream currentOut;
        private boolean currentRecorded;
        private int currentHash;
        private long currentSize;

        // account
        private int filesExtracted;
        private int filesAccounted; // files that reached a verdict: extracted, or an error recorded
        private boolean writeFailed;

        /**
         * @param archive     an open archive
         * @param archiveName the archive's file name; only used to name entries that carry no name
         * @param outputDir   where to extract to; created if missing
         * @throws IOException if the output directory cannot be created or resolved
         */
        public Extractor(IInArchive archive, String archiveName, File outputDir) throws IOException {
            this.archive = archive;
            // Resolve the output directory ONCE, up front. Every later safety check compares against
            // this canonical form, so relative paths ("", ".", "out") and symlinked directories all
            // behave. The root directory itself is a legal output directory.
            File dir = outputDir.getAbsoluteFile();
            if (!dir.isDirectory() && !dir.mkdirs()) {
                throw new IOException("Output directory cannot be created: " + dir);
            }
            this.outputDir = dir.getCanonicalFile();
            this.fallbackName = fallbackName(archiveName);
        }

        // -------------------------------------------------------------------------------------
        // Phase 1: plan. Decide for every item what it is and where it may go - nothing is written.
        // -------------------------------------------------------------------------------------

        private void plan() {
            int count;
            try {
                count = archive.getNumberOfItems();
            } catch (SevenZipException e) {
                itemErrors.add(new ItemError(-1, "", "cannot read the item count", e));
                return;
            }
            for (int i = 0; i < count; i++) {
                try {
                    planItem(i);
                } catch (Exception e) {
                    // A damaged archive can make even a property lookup fail; that is one bad item,
                    // not a reason to abandon the rest.
                    itemErrors.add(new ItemError(i, "#" + i, "cannot read item properties", e));
                }
            }
        }

        private void planItem(int index) throws SevenZipException, IOException {
            String path = stringProperty(index, PropID.PATH);
            // Properties are optional in some formats: a missing IS_FOLDER is null, never assume Boolean.
            boolean directory = Boolean.TRUE.equals(archive.getProperty(index, PropID.IS_FOLDER));

            // Links and special files are skipped by design. A symbolic link extracted as a link can
            // point anywhere - including outside the output directory - and a later item written
            // "through" it would escape. Extracting it as a file instead would silently produce a file
            // containing the link target, which is not what anyone wants. Skip, and say so.
            String special = directory ? null : specialEntryKind(index);
            if (special != null) {
                itemErrors.add(new ItemError(index, path, "skipped: " + special + " entries are not extracted", null));
                return;
            }

            List<String> parts;
            try {
                parts = safeComponents(path, WINDOWS);
            } catch (IllegalArgumentException unsafe) {
                itemErrors.add(new ItemError(index, path, "blocked: " + unsafe.getMessage(), null));
                return;
            }
            String displayName = path;
            if (parts.isEmpty()) {
                if (directory) {
                    return; // the archive root - it exists already
                }
                // Single-stream formats (gzip, bzip2, xz, ...) and some zip/tar entries store no name.
                // Without a name the target would be the output directory itself - and writing a file
                // over a directory is exactly what must never happen. Do what the 7-Zip command line
                // does: derive a name from the archive's file name.
                parts = safeComponents(fallbackName, WINDOWS);
                displayName = fallbackName;
            }

            File target = new File(outputDir, join(parts));
            // Belt and braces: the components above are already clean, but the canonical path also
            // resolves symlinks that may already exist inside the output directory. If "a" is a link
            // to /etc, then "a/passwd" must still be refused.
            if (!isInside(outputDir, target.getCanonicalFile())) {
                itemErrors.add(new ItemError(index, path, "blocked: resolves outside the output directory", null));
                return;
            }

            if (directory) {
                if (!target.isDirectory() && !target.mkdirs()) {
                    itemErrors.add(new ItemError(index, path, "cannot create directory", null));
                }
                return; // directories - including empty ones - are created right here
            }
            targets.put(index, target);
            displayNames.put(index, displayName);
            filesPlanned++;
        }

        /** Names the kind of entry that must not be written as a plain file, or null for a regular file. */
        private String specialEntryKind(int index) throws SevenZipException {
            if (!stringProperty(index, PropID.SYM_LINK).isEmpty()) {
                return "symbolic link";
            }
            if (!stringProperty(index, PropID.HARD_LINK).isEmpty()) {
                return "hard link"; // carries a regular file mode, so only the property reveals it
            }
            Object attributes = archive.getProperty(index, PropID.ATTRIBUTES);
            if (attributes instanceof Integer && (((Integer) attributes) & 0x400) != 0) {
                return "reparse point"; // FILE_ATTRIBUTE_REPARSE_POINT: a Windows link or junction
            }
            int mode = unixMode(index);
            if (mode >= 0) {
                int type = mode & 0xF000; // S_IFMT; zero when the format stores permissions only
                if (type != 0 && type != 0x8000 /* S_IFREG */ && type != 0x4000 /* S_IFDIR */) {
                    return type == 0xA000 /* S_IFLNK */ ? "symbolic link" : "device, pipe or socket";
                }
            }
            return null;
        }

        /** The Unix mode bits, from POSIX_ATTRIB or from the high half of ATTRIBUTES, or -1 if unknown. */
        private int unixMode(int index) throws SevenZipException {
            Object posix = archive.getProperty(index, PropID.POSIX_ATTRIB);
            if (posix instanceof Integer) {
                return (Integer) posix;
            }
            // Zip and 7z store Unix bits in the upper 16 bits of the Windows attributes when the
            // 0x8000 "unix extension" flag is set - the 7-Zip convention.
            Object attributes = archive.getProperty(index, PropID.ATTRIBUTES);
            if (attributes instanceof Integer && (((Integer) attributes) & 0x8000) != 0) {
                return ((Integer) attributes) >>> 16;
            }
            return -1;
        }

        private String stringProperty(int index, PropID propID) throws SevenZipException {
            String value = archive.getStringProperty(index, propID);
            return value == null ? "" : value;
        }

        // -------------------------------------------------------------------------------------
        // Phase 2: stream. One extract() call; the callback opens once, appends, closes once.
        // -------------------------------------------------------------------------------------

        /** Where the bytes of one file go. Override to redirect output (tests use this). */
        protected OutputStream openOutput(File file) throws IOException {
            // Buffered: the engine hands over many small chunks, and each unbuffered write is a syscall.
            return new BufferedOutputStream(new FileOutputStream(file));
        }

        public ISequentialOutStream getStream(int index, ExtractAskMode mode) throws SevenZipException {
            currentIndex = index;
            currentFile = null;
            currentOut = null;
            currentRecorded = false;
            if (mode != ExtractAskMode.EXTRACT) {
                return null; // a test or skip pass wants no data
            }
            if (writeFailed) {
                return null; // once the disk failed, writing more can only fail again; skip, and count
            }
            File target = targets.get(index);
            if (target == null) {
                return null; // not approved by the plan (the engine is only given approved indices)
            }
            // Never overwrite silently. This is 7-Zip's "-aos" (skip existing) policy; overwriting is a
            // deliberate, documented decision an application may make - not a default.
            if (target.exists()) {
                record(target.isDirectory() ? "a directory is in the way" : "already exists - not overwritten", null);
                return null;
            }
            File parent = target.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
                record("cannot create parent directory " + parent, null);
                return null;
            }
            try {
                currentOut = openOutput(target); // opened ONCE per file
            } catch (IOException e) {
                record("cannot create output file", e);
                return null;
            }
            currentFile = target;
            currentHash = 0;
            currentSize = 0;

            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    try {
                        currentOut.write(data); // called many times per file: just append
                    } catch (IOException e) {
                        // Never claim to have consumed bytes that were not written - that is silent
                        // corruption. Record the real cause, remember that the disk is unusable, and
                        // throw; the engine saves the exception and re-throws it from extract().
                        record("cannot write output file", e);
                        writeFailed = true;
                        throw new SevenZipException("Cannot write " + currentFile + ": " + e.getMessage(), e);
                    }
                    currentHash ^= Arrays.hashCode(data); // demo checksum shared by all snippets here
                    currentSize += data.length;
                    return data.length; // the contract: return the number of bytes consumed
                }
            };
        }

        public void prepareOperation(ExtractAskMode mode) {
            // Called before each item with the operation kind (extract/test/skip). Nothing to do here.
        }

        public void setOperationResult(ExtractOperationResult result) throws SevenZipException {
            // The engine is done with the current item: close first, and keep a close failure aside.
            // A failed close must be reported, but it must never replace the item's primary error.
            IOException closeError = null;
            if (currentOut != null) {
                try {
                    currentOut.close(); // flushes the last buffered block
                } catch (IOException e) {
                    closeError = e;
                }
                currentOut = null;
            }
            if (currentFile == null && !currentRecorded) {
                return; // nothing was written for this item (skipped, or a test pass)
            }
            filesAccounted++;

            if (currentRecorded) {
                discardPartialFile(); // our own open/write failure already speaks for this item
                return;
            }
            if (result != ExtractOperationResult.OK) {
                // This is the second failure channel: the engine judged the item bad (CRC error, data
                // error, ...) and told us here - with no exception. Record it, or it is lost.
                record("engine reported " + result, null);
                if (closeError != null) {
                    record("cannot close output file", closeError);
                }
                discardPartialFile();
                return;
            }
            if (closeError != null) {
                // The engine was happy, but the final flush failed: the file on disk is truncated.
                record("cannot flush output file", closeError);
                discardPartialFile();
                return;
            }

            filesExtracted++;
            Object modified = archive.getProperty(currentIndex, PropID.LAST_MODIFICATION_TIME);
            if (modified instanceof Date) {
                currentFile.setLastModified(((Date) modified).getTime()); // cosmetic: failure is not an error
            }
            System.out.println(String.format("%9X | %10s | %s", currentHash, currentSize,
                    displayNames.get(currentIndex)));
        }

        public void setTotal(long total) {
            // Progress: total bytes to process. Wire this and setCompleted() to a progress bar.
        }

        public void setCompleted(long completed) {
        }

        /**
         * A file that failed halfway must not stay on disk: a script that only checks "does the file
         * exist" would otherwise trust a truncated or corrupt result. Pre-existing files are never
         * touched - they were refused before anything was opened, so {@code currentFile} is null.
         */
        private void discardPartialFile() {
            if (currentFile != null) {
                currentFile.delete();
            }
        }

        private void record(String kind, Throwable cause) {
            currentRecorded = true;
            String path = "#" + currentIndex;
            try {
                path = stringProperty(currentIndex, PropID.PATH);
            } catch (SevenZipException e) {
                // keep the index as the name
            }
            itemErrors.add(new ItemError(currentIndex, path, kind, cause));
        }

        // -------------------------------------------------------------------------------------
        // Phase 3: run the phases and account for everything.
        // -------------------------------------------------------------------------------------

        /** Plan, stream, and account. Never throws: everything ends up in the {@link Report}. */
        public Report extractAll() {
            plan();
            int[] files = new int[targets.size()];
            int n = 0;
            for (int index = 0; n < files.length; index++) {
                if (targets.containsKey(index)) {
                    files[n++] = index;
                }
            }
            SevenZipException archiveError = null;
            try {
                if (files.length > 0) {
                    archive.extract(files, false, this); // ONE call, ascending indices: one pass, O(n)
                }
            } catch (SevenZipException e) {
                // If we made the engine stop by throwing from write(), this is our own exception coming
                // back - already recorded against its item. Anything else is a genuine archive error.
                if (!writeFailed) {
                    archiveError = e;
                }
            } catch (RuntimeException e) {
                archiveError = new SevenZipException("Unexpected error during extraction: " + e, e);
            } finally {
                if (currentOut != null) {
                    // The engine stopped without giving the item in progress a verdict. Never leak the
                    // handle, never leave the half-written file, and never leave the item unaccounted.
                    closeQuietly();
                    discardPartialFile();
                    if (!currentRecorded) {
                        record("extraction stopped before this file was complete", null);
                    }
                    filesAccounted++;
                }
            }
            return new Report(itemErrors, filesPlanned, filesExtracted, filesPlanned - filesAccounted,
                    writeFailed, archiveError);
        }

        private void closeQuietly() {
            OutputStream out = currentOut;
            currentOut = null;
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // the file is already reported as broken; only the handle matters here
                }
            }
        }

        // -------------------------------------------------------------------------------------
        // Path rules. Small, explicit, and testable on their own.
        // -------------------------------------------------------------------------------------

        /**
         * Splits an archive path into clean components. Both separators are accepted (7-Zip reports
         * the native one), empty and "." components are dropped, and anything that could leave the
         * output directory or misbehave on the current OS is rejected with the reason.
         */
        static List<String> safeComponents(String entryPath, boolean windowsRules) {
            List<String> parts = new ArrayList<String>();
            for (String part : entryPath.split("[/\\\\]")) {
                if (part.isEmpty() || part.equals(".")) {
                    continue;
                }
                if (part.equals("..")) {
                    throw new IllegalArgumentException("path contains '..'");
                }
                for (int i = 0; i < part.length(); i++) {
                    char c = part.charAt(i);
                    if (c < 0x20) {
                        throw new IllegalArgumentException("path contains a control character");
                    }
                    if (windowsRules && "<>:\"|?*".indexOf(c) >= 0) {
                        throw new IllegalArgumentException("path contains '" + c + "', illegal on Windows");
                    }
                }
                if (windowsRules) {
                    if (part.endsWith(" ") || part.endsWith(".")) {
                        throw new IllegalArgumentException("Windows drops a trailing space or dot from '" + part + "'");
                    }
                    String base = part.toUpperCase(Locale.ROOT);
                    int dot = base.indexOf('.');
                    if (dot >= 0) {
                        base = base.substring(0, dot); // "CON.txt" is still the console device
                    }
                    if (base.matches("CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9]")) {
                        throw new IllegalArgumentException("'" + part + "' is a reserved Windows device name");
                    }
                }
                parts.add(part);
            }
            return parts;
        }

        /** True if {@code candidate} is {@code root} or lies below it. Both must be canonical. */
        static boolean isInside(File root, File candidate) {
            String rootPath = root.getPath();
            String path = candidate.getPath();
            if (path.equals(rootPath)) {
                return true;
            }
            String prefix = rootPath.endsWith(File.separator) ? rootPath : rootPath + File.separator;
            return path.startsWith(prefix);
        }

        /**
         * The name 7-Zip gives an unnamed entry, derived from the archive's file name: strip the
         * compression extension ("data.gz" -> "data"), turn the tar shorthands into ".tar"
         * ("backup.tgz" -> "backup.tar"), and append "~" when there is nothing to strip.
         */
        static String fallbackName(String archiveName) {
            String lower = archiveName.toLowerCase(Locale.ROOT);
            for (String shorthand : new String[] { ".tgz", ".tpz", ".tbz", ".tbz2", ".txz", ".tlz", ".taz" }) {
                if (lower.endsWith(shorthand)) {
                    return archiveName.substring(0, archiveName.length() - shorthand.length()) + ".tar";
                }
            }
            int dot = archiveName.lastIndexOf('.');
            return dot > 0 ? archiveName.substring(0, dot) : archiveName + "~";
        }

        private static String join(List<String> parts) {
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                if (sb.length() > 0) {
                    sb.append(File.separatorChar);
                }
                sb.append(part);
            }
            return sb.toString();
        }
    }

    // =============================================================================================
    // Command line
    // =============================================================================================

    /** Prints every problem, one line each, then the single most useful stack trace. */
    public static void printReport(Report report) {
        if (report.isClean()) {
            return;
        }
        System.err.println("=== extraction problems ===");
        for (ItemError error : report.itemErrors) {
            System.err.println("  " + error);
        }
        if (report.filesNotExtracted > 0) {
            System.err.println("  " + report.filesNotExtracted + " of " + report.filesPlanned + " files not extracted"
                    + (report.writeFailed ? " because writing had already failed" : " (no verdict from the engine)"));
        }
        if (report.archiveError != null) {
            System.err.println("  archive error, extraction stopped: " + report.archiveError.getMessage());
        }
        Throwable primary = report.archiveError;
        for (int i = 0; primary == null && i < report.itemErrors.size(); i++) {
            primary = report.itemErrors.get(i).cause;
        }
        if (primary instanceof SevenZipException) {
            ((SevenZipException) primary).printStackTraceExtended(); // includes every saved cause
        } else if (primary != null) {
            primary.printStackTrace();
        }
    }

    /** @return the process exit code: 0 = everything extracted, 1 = problems, 2 = wrong usage */
    public static int run(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java ExtractToFolder <archive> <output-directory>");
            return 2;
        }
        File archiveFile = new File(args[0]);
        try (RandomAccessFile file = new RandomAccessFile(archiveFile, "r");
                IInArchive archive = SevenZip.openInArchive(null, // null: detect the format
                        new RandomAccessFileInStream(file))) {
            Extractor extractor = new Extractor(archive, archiveFile.getName(), new File(args[1]));

            System.out.println("   Hash   |    Size    | Filename");
            System.out.println("----------+------------+---------");

            Report report = extractor.extractAll();
            printReport(report);
            return report.isClean() ? 0 : 1;
        } catch (SevenZipException e) {
            // Reached when the archive cannot be opened - or closed: hence the neutral wording.
            System.err.println("Archive error: " + e.getMessage());
            e.printStackTraceExtended();
            return 1;
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        System.exit(run(args));
    }
}
/* END_SNIPPET */
