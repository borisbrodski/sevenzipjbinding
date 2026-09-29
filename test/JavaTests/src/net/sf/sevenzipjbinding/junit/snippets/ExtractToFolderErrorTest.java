package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;
import net.sf.sevenzipjbinding.junit.snippets.ExtractToFolder.Extractor;
import net.sf.sevenzipjbinding.junit.snippets.ExtractToFolder.ItemError;
import net.sf.sevenzipjbinding.junit.snippets.ExtractToFolder.Report;

/**
 * Safety and error-handling tests for {@link ExtractToFolder}. They assert invariants that must hold
 * on every OS and across 7-Zip engine upgrades - never specific result codes, and never whether a
 * given failure arrives as an exception or as an engine-reported result.
 */
public class ExtractToFolderErrorTest extends SnippetTest {

    private static final String DATA = "testdata/snippets/";

    // ------------------------------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------------------------------

    private static File freshDir(String tag) {
        return new File(System.getProperty("java.io.tmpdir"), "sevenzipjbinding-etf-" + tag + "-" + System.nanoTime());
    }

    private interface WithArchive<T> {
        T apply(IInArchive archive) throws Exception;
    }

    private static <T> T withArchive(String archivePath, WithArchive<T> action) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(archivePath, "r");
                IInArchive archive = SevenZip.openInArchive(null, (IInStream) new RandomAccessFileInStream(file))) {
            return action.apply(archive);
        }
    }

    private static Report extract(final String archivePath, final File outputDir) throws Exception {
        return withArchive(archivePath, new WithArchive<Report>() {
            public Report apply(IInArchive archive) throws Exception {
                return new Extractor(archive, new File(archivePath).getName(), outputDir).extractAll();
            }
        });
    }

    private static int countFiles(File dir) {
        int n = 0;
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                n += child.isDirectory() ? countFiles(child) : 1;
            }
        }
        return n;
    }

    private static String read(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    private static ItemError errorFor(Report report, String pathFragment) {
        for (ItemError error : report.itemErrors) {
            if (error.path.contains(pathFragment)) {
                return error;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------------------------
    // baseline
    // ------------------------------------------------------------------------------------------

    @Test
    public void cleanArchiveExtractsEverythingWithNoProblems() throws Exception {
        File out = freshDir("clean");
        beginSnippetTest();
        Report report = extract(DATA + "simple.zip", out);
        endSnippetTest();

        assertTrue("clean archive must report no problems: " + report.itemErrors, report.isClean());
        assertEquals(3, report.filesPlanned);
        assertEquals(3, report.filesExtracted);
        assertEquals(3, countFiles(out));
        deleteRecursively(out);
    }

    @Test
    public void relativeOutputDirectoryIsResolvedAgainstTheWorkingDirectory() throws Exception {
        File out = new File("etf-relative-" + System.nanoTime()); // deliberately relative
        beginSnippetTest();
        Report report = extract(DATA + "simple.zip", out);
        endSnippetTest();

        assertTrue(report.isClean());
        assertTrue(new File(out, "file1.txt").isFile());
        deleteRecursively(out);
    }

    // ------------------------------------------------------------------------------------------
    // the two failure channels
    // ------------------------------------------------------------------------------------------

    /** A corrupt item is reported by the engine per item, with no exception. It must not slip. */
    @Test
    public void corruptItemsAreCapturedNotSilentlyLostAndHealthyItemsStillExtract() throws Exception {
        File out = freshDir("corrupt");
        beginSnippetTest();
        Report report = extract(DATA + "corrupt.zip", out);
        endSnippetTest();

        assertFalse("a corrupt archive must not look clean", report.isClean());
        if (report.archiveError == null) {
            assertFalse("engine-reported failures were lost", report.itemErrors.isEmpty());
        }
        assertTrue("healthy items still extract", report.filesExtracted >= 1);
        // A failed item leaves no half-written file behind that a script could mistake for a result.
        for (ItemError error : report.itemErrors) {
            assertFalse("partial file left on disk: " + error.path, new File(out, error.path).exists());
        }
        deleteRecursively(out);
    }

    /** Simulates a full disk: the write fails, extraction stops writing, and the report says exactly that. */
    @Test
    public void writeFailureStopsExtractionAndIsReportedHonestly() throws Exception {
        final File out = freshDir("diskfull");
        beginSnippetTest();
        Report report = withArchive(DATA + "simple.zip", new WithArchive<Report>() {
            public Report apply(IInArchive archive) throws Exception {
                return new Extractor(archive, "simple.zip", out) {
                    private int opened;

                    @Override
                    protected OutputStream openOutput(File file) throws IOException {
                        if (++opened == 2) {
                            return new OutputStream() { // the second file hits a full disk
                                @Override
                                public void write(int b) throws IOException {
                                    throw new IOException("No space left on device");
                                }
                            };
                        }
                        return super.openOutput(file);
                    }
                }.extractAll();
            }
        });
        endSnippetTest();

        assertTrue(report.writeFailed);
        assertEquals("the first file was written before the disk failed", 1, report.filesExtracted);
        assertEquals("exactly the failing item is reported", 1, report.itemErrors.size());
        ItemError error = report.itemErrors.get(0);
        assertNotNull("the real cause is attached", error.cause);
        assertTrue(error.cause.getMessage().contains("No space left"));
        assertEquals("the remaining file is counted as not extracted, not as an error", 1, report.filesNotExtracted);
        assertNull("our own write failure is not reported a second time as an archive error", report.archiveError);
        deleteRecursively(out);
    }

    // ------------------------------------------------------------------------------------------
    // safety: what must never be written
    // ------------------------------------------------------------------------------------------

    @Test
    public void pathTraversalEntryIsBlockedAndNothingEscapes() throws Exception {
        File parent = freshDir("slip");
        File out = new File(parent, "out");
        assertTrue(out.mkdirs());
        File escaped = new File(parent, "zipslip-escaped.txt"); // where "../zipslip-escaped.txt" would land

        beginSnippetTest();
        Report report = extract(DATA + "zipslip.zip", out);
        endSnippetTest();

        assertFalse("nothing may be written outside the output directory", escaped.exists());
        ItemError blocked = errorFor(report, "zipslip-escaped");
        assertNotNull("the traversal entry is reported: " + report.itemErrors, blocked);
        assertTrue(blocked.kind, blocked.kind.startsWith("blocked"));
        assertTrue("the safe entry still extracts", new File(out, "safe.txt").isFile());
        deleteRecursively(parent);
    }

    @Test
    public void linksAreSkippedNotWrittenAsFiles() throws Exception {
        File out = freshDir("links");
        beginSnippetTest();
        Report report = extract(DATA + "links.tar", out);
        endSnippetTest();

        assertEquals("real content\n", read(new File(out, "real.txt")));
        assertFalse("a symlink must not become a file holding its target", new File(out, "symlink").exists());
        assertFalse("a hard link must not become an empty file", new File(out, "hardlink").exists());
        assertTrue(errorFor(report, "symlink").kind.contains("symbolic link"));
        assertTrue(errorFor(report, "hardlink").kind.contains("hard link"));
        assertFalse("skipped entries are visible in the verdict, not silently dropped", report.isClean());
        deleteRecursively(out);
    }

    @Test
    public void existingFileIsNeverOverwritten() throws Exception {
        File out = freshDir("exists");
        assertTrue(out.mkdirs());
        File existing = new File(out, "file1.txt");
        try (OutputStream o = new FileOutputStream(existing)) {
            o.write("KEEP ME".getBytes(StandardCharsets.UTF_8));
        }

        beginSnippetTest();
        Report report = extract(DATA + "simple.zip", out);
        endSnippetTest();

        assertEquals("KEEP ME", read(existing));
        ItemError refused = errorFor(report, "file1.txt");
        assertNotNull(refused);
        assertTrue(refused.kind, refused.kind.contains("not overwritten"));
        assertEquals("the other two files still extract", 2, report.filesExtracted);
        deleteRecursively(out);
    }

    @Test
    public void directoryInTheWayOfAFileIsReportedWithoutMaskingAndOthersExtract() throws Exception {
        File out = freshDir("inway");
        assertTrue(new File(out, "file1.txt").mkdirs());

        beginSnippetTest();
        Report report = extract(DATA + "simple.zip", out);
        endSnippetTest();

        ItemError blocked = errorFor(report, "file1.txt");
        assertNotNull(blocked);
        assertTrue(blocked.kind, blocked.kind.contains("in the way"));
        assertFalse("a close error must never mask the real problem", blocked.kind.contains("close"));
        assertEquals(2, report.filesExtracted);
        deleteRecursively(out);
    }

    // ------------------------------------------------------------------------------------------
    // entries without a name
    // ------------------------------------------------------------------------------------------

    /** A gzip stream without a stored name must become a file NEXT TO nothing - never the output dir itself. */
    @Test
    public void namelessSingleStreamArchiveGetsTheArchivesNameAndKeepsTheOutputDirectory() throws Exception {
        File out = freshDir("nameless");
        beginSnippetTest();
        int exitCode = ExtractToFolder.run(new String[] { DATA + "nameless.gz", out.getPath() });
        endSnippetTest();

        assertEquals(0, exitCode);
        assertTrue("the output directory must still be a directory", out.isDirectory());
        assertEquals("hello\n", read(new File(out, "nameless"))); // "nameless.gz" -> "nameless"
        deleteRecursively(out);
    }

    @Test
    public void emptyZipEntryNameFallsBackToTheArchiveName() throws Exception {
        File out = freshDir("emptyname");
        beginSnippetTest();
        Report report = extract(DATA + "emptyname.zip", out);
        endSnippetTest();

        assertTrue(report.itemErrors.toString(), report.isClean());
        assertEquals("ok\n", read(new File(out, "ok.txt")));
        assertEquals("nameless entry\n", read(new File(out, "emptyname")));
        deleteRecursively(out);
    }

    @Test
    public void fallbackNameFollowsThe7ZipCommandLine() {
        assertEquals("data", Extractor.fallbackName("data.gz"));
        assertEquals("backup.tar", Extractor.fallbackName("backup.tgz"));
        assertEquals("backup.tar", Extractor.fallbackName("backup.TBZ2"));
        assertEquals("x.tar", Extractor.fallbackName("x.tar.gz"));
        assertEquals("h~", Extractor.fallbackName("h"));
        assertEquals("data", Extractor.fallbackName("data.bin"));
    }

    // ------------------------------------------------------------------------------------------
    // path rules, on their own
    // ------------------------------------------------------------------------------------------

    @Test
    public void pathComponentsAreCleanedAndTraversalIsRejected() {
        assertEquals(Arrays.asList("a", "b"), Extractor.safeComponents("a/b", false));
        assertEquals(Arrays.asList("a", "b"), Extractor.safeComponents("a\\b", false));
        assertEquals(Arrays.asList("abs", "path"), Extractor.safeComponents("/abs/path", false));
        assertEquals(Arrays.asList("x", "y"), Extractor.safeComponents("./x//y/", false));
        assertTrue(Extractor.safeComponents("", false).isEmpty());
        assertRejected("a/../b", false);
        assertRejected("..", false);
        assertRejected("bad\u0000name", false);
    }

    @Test
    public void windowsOnlyRulesRejectReservedNamesAndIllegalCharacters() {
        List<String> ok = Extractor.safeComponents("CON.txt", false); // legal on Unix
        assertEquals(Arrays.asList("CON.txt"), ok);
        assertRejected("CON.txt", true);
        assertRejected("dir/lpt1", true);
        assertRejected("file.txt:stream", true);
        assertRejected("trailing dot.", true);
        assertEquals(Arrays.asList("plain.txt"), Extractor.safeComponents("plain.txt", true));
    }

    private static void assertRejected(String path, boolean windowsRules) {
        try {
            Extractor.safeComponents(path, windowsRules);
            fail("expected rejection of: " + path);
        } catch (IllegalArgumentException expected) {
            assertFalse(expected.getMessage().isEmpty());
        }
    }

    @Test
    public void containmentCheckHandlesTheRootAndSiblingPrefixes() throws Exception {
        File root = new File(File.listRoots()[0].getPath()).getCanonicalFile(); // "/" or "C:\"
        assertTrue(Extractor.isInside(root, new File(root, "x").getCanonicalFile()));
        assertTrue(Extractor.isInside(root, root));

        File base = freshDir("contain").getCanonicalFile();
        assertTrue(Extractor.isInside(base, new File(base, "child")));
        assertFalse("'/tmp/a' must not contain '/tmp/ab'", Extractor.isInside(base, new File(base.getPath() + "b")));
        assertFalse(Extractor.isInside(base, base.getParentFile()));
    }

    // ------------------------------------------------------------------------------------------
    // command line
    // ------------------------------------------------------------------------------------------

    @Test
    public void exitCodeReflectsTheOutcome() {
        File clean = freshDir("exit-clean");
        File bad = freshDir("exit-bad");
        beginSnippetFailingTest();
        int ok = ExtractToFolder.run(new String[] { DATA + "simple.zip", clean.getPath() });
        int problems = ExtractToFolder.run(new String[] { DATA + "corrupt.zip", bad.getPath() });
        int tooFew = ExtractToFolder.run(new String[] { DATA + "simple.zip" });
        int tooMany = ExtractToFolder.run(new String[] { "a", "b", "c" });
        endSnippetTest();

        assertEquals(0, ok);
        assertEquals(1, problems);
        assertEquals(2, tooFew);
        assertEquals(2, tooMany);
        assertTrue("usage goes to stderr", getSnippetErrOutput().contains("Usage:"));
        assertTrue("problems are reported", getSnippetErrOutput().contains("extraction problems"));
        deleteRecursively(clean);
        deleteRecursively(bad);
    }

    // ------------------------------------------------------------------------------------------
    // a verdict without any data
    // ------------------------------------------------------------------------------------------

    /**
     * Encrypted data without a password: the engine never asks for an EXTRACT stream and only reports
     * a verdict per item. That verdict must be recorded - an empty error list would hide the failure.
     */
    @Test
    public void encryptedItemWithoutPasswordIsReportedNotSilentlyDropped() throws Exception {
        File dir = freshDir("encrypted");
        assertTrue(dir.mkdirs());
        File archiveFile = new File(dir, "secret.7z");
        writePasswordProtected7z(archiveFile, "Secret123");

        beginSnippetTest();
        Report report = extract(archiveFile.getPath(), new File(dir, "out"));
        endSnippetTest();

        assertFalse(report.isClean());
        assertEquals("nothing can be written without the password", 0, report.filesExtracted);
        assertEquals("every planned file received a verdict", 0, report.filesNotExtracted);
        ItemError error = errorFor(report, "a.txt");
        assertNotNull("the engine's verdict must be recorded: " + report.itemErrors, error);
        assertTrue(error.kind, error.kind.startsWith("engine reported"));
        assertTrue("the report explains that a password is needed", error.kind.contains("password"));
        deleteRecursively(dir);
    }

    private static void writePasswordProtected7z(File archiveFile, final String password) throws Exception {
        final byte[] content = "top secret\n".getBytes(StandardCharsets.UTF_8);
        try (RandomAccessFile file = new RandomAccessFile(archiveFile, "rw");
                IOutCreateArchive7z archive = SevenZip.openOutArchive7z()) {
            class Item implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {
                public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> factory) {
                    IOutItem7z item = factory.createOutItem();
                    item.setPropertyPath("a.txt");
                    item.setDataSize((long) content.length);
                    return item;
                }

                public ISequentialInStream getStream(int index) {
                    return new ByteArrayStream(content, true);
                }

                public String cryptoGetTextPassword() {
                    return password;
                }

                public void setOperationResult(boolean ok) {
                }

                public void setTotal(long total) {
                }

                public void setCompleted(long completed) {
                }
            }
            archive.createArchive(new RandomAccessFileOutStream(file), 1, new Item());
        }
    }
}
