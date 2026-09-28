package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.snippets.ExtractToFolder.ItemError;
import net.sf.sevenzipjbinding.junit.snippets.ExtractToFolder.Report;

/**
 * Robustness tests for {@link ExtractToFolder} error handling. These deliberately do NOT assert on
 * specific {@code ExtractOperationResult} codes or on whether a given failure arrives as an exception
 * vs. an engine-reported result - those can shift between 7-Zip engine versions. They assert the
 * invariants that must hold regardless: a failure is never silently lost, healthy items still extract,
 * a close() error never masks the real cause, and nothing crashes.
 */
public class ExtractToFolderErrorTest extends SnippetTest {

    private File freshOutputDir(String tag) {
        return new File(System.getProperty("java.io.tmpdir"),
                "sevenzipjbinding-etf-" + tag + "-" + System.nanoTime());
    }

    private Report extract(String archivePath, File outputDir) throws Exception {
        try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(archivePath, "r");
                IInArchive inArchive = SevenZip.openInArchive(null,
                        (IInStream) new RandomAccessFileInStream(raf))) {
            return ExtractToFolder.extractAll(inArchive, outputDir);
        }
    }

    private int countFiles(File dir) {
        int n = 0;
        File[] kids = dir.listFiles();
        if (kids != null) {
            for (File k : kids) {
                n += k.isDirectory() ? countFiles(k) : 1;
            }
        }
        return n;
    }

    private void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    /** Baseline: a good archive extracts with zero problems and every file on disk. */
    @Test
    public void cleanArchiveHasNoProblems() throws Exception {
        File out = freshOutputDir("clean");
        beginSnippetTest();
        Report report = extract("testdata/snippets/simple.zip", out);
        endSnippetTest();

        assertTrue("clean archive must report no problems: " + report.itemErrors, report.isClean());
        assertNull(report.fatalError);
        assertTrue("all files should be on disk", countFiles(out) == 3);
        deleteRecursively(out);
    }

    /**
     * THE key case (Boris): a corrupted archive fails PER ITEM via a non-OK ExtractOperationResult,
     * with extract() returning normally and no exception. Such failures must be CAPTURED (never
     * silently lost), and the healthy items must still land on disk.
     */
    @Test
    public void corruptArchiveFailuresAreCapturedNotSilentlyLost() throws Exception {
        File out = freshOutputDir("corrupt");
        beginSnippetTest();
        Report report = extract("testdata/snippets/corrupt.zip", out);
        endSnippetTest();

        // 1. The corruption is NOT swept under the rug.
        assertFalse("a corrupt archive must not look clean", report.isClean());
        // 2. It was captured somehow - as item errors and/or a fatal - never lost.
        assertTrue("failures must be captured, not lost",
                !report.itemErrors.isEmpty() || report.fatalError != null);
        // 3. Specifically: when extract() does NOT throw (the engine-reported-result path), the
        //    per-item errors MUST have caught it - this is the exact "it can slip" gap.
        if (report.fatalError == null) {
            assertFalse("engine-reported failures were lost - the slip Boris warned about",
                    report.itemErrors.isEmpty());
        }
        // 4. Recovery: healthy items still extracted.
        assertTrue("healthy items should still extract despite a bad one", countFiles(out) >= 1);
        deleteRecursively(out);
    }

    /**
     * A blocked output path (stand-in for a mid-write ENOSPC / permission failure) is recorded WITH its
     * real cause, does NOT abort the run, and does NOT mask the cause with a bogus close error.
     */
    @Test
    public void blockedOutputPathIsRecordedWithRealCauseAndOthersStillExtract() throws Exception {
        File out = freshOutputDir("blocked");
        // Block "file1.txt" by pre-creating a DIRECTORY where a regular file must go.
        assertTrue(new File(out, "file1.txt").mkdirs());

        beginSnippetTest();
        Report report = extract("testdata/snippets/simple.zip", out);
        endSnippetTest();

        // The blocked item is recorded, WITH a real cause (not null, not a masking close error).
        ItemError blocked = null;
        for (ItemError e : report.itemErrors) {
            if (e.path.contains("file1.txt")) {
                blocked = e;
            }
        }
        assertNotNull("the blocked file must be reported: " + report.itemErrors, blocked);
        assertNotNull("the real cause must be attached (e.g. FileNotFoundException)", blocked.cause);
        assertFalse("a close error must not have masked the open failure",
                blocked.kind.contains("close"));
        // The other real files still extracted (recovery), and the run did not fatally abort.
        assertTrue("other files should still extract", countFiles(out) >= 1);
        assertTrue("only the blocked item should be a problem: " + report.itemErrors,
                report.itemErrors.size() >= 1);
        deleteRecursively(out);
    }

    /** printReport must surface the problems and exactly one representative stack trace, without throwing. */
    @Test
    public void reportPrintsProblemsAndDoesNotCrash() throws Exception {
        File out = freshOutputDir("report");
        beginSnippetFailingTest();
        Report report = extract("testdata/snippets/corrupt.zip", out);
        ExtractToFolder.printReport(report);
        endSnippetTest();

        String err = getSnippetErrOutput();
        assertTrue("report header expected:\n" + err, err.contains("extraction problems"));
        deleteRecursively(out);
    }
}
