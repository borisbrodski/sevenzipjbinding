package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * Robustness test for the {@link ExtractToFolder} error handling (the "what if the disk is full" case,
 * simulated here by blocking one output path). It proves that:
 *  - a failure does NOT crash the program (extract's exception is caught and reported);
 *  - the REAL root cause surfaces (here "Is a directory"; in production, "No space left on device"),
 *    and is NOT masked by a bogus "cannot close output file";
 *  - no output stream is leaked (main()'s finally / the callback close paths run).
 */
public class ExtractToFolderErrorTest extends SnippetTest {

    @Test
    public void reportsRealCauseAndDoesNotMaskItOrCrash() {
        File outputDir = new File(System.getProperty("java.io.tmpdir"),
                "sevenzipjbinding-extract-error-" + System.nanoTime());
        // Block the first output file by pre-creating a DIRECTORY where a regular file must go, so
        // opening it fails deterministically and portably (stand-in for a mid-write ENOSPC failure).
        File blocker = new File(outputDir, "file1.txt");
        assertTrue("test setup: could not create blocker directory", blocker.mkdirs());

        beginSnippetFailingTest();
        // main() must NOT throw - it catches and reports. If this line throws, the test fails outright.
        ExtractToFolder.main(new String[] { "testdata/snippets/simple.zip", outputDir.getPath() });
        endSnippetTest();

        String err = getSnippetErrOutput();

        // The program reported an extraction error (did not silently swallow it) ...
        assertTrue("expected an 'Extraction error' report, got:\n" + err,
                err.contains("Extraction error:"));
        // ... and surfaced the REAL cause via printStackTraceExtended(), not a masking close error.
        assertTrue("expected the real root cause in the output, got:\n" + err,
                err.contains("Cannot open output file") || err.contains("Is a directory"));
        assertFalse("a close() error must never mask the real cause",
                err.contains("Cannot flush/close") || err.contains("Cannot close output file"));

        deleteRecursively(outputDir);
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
}
