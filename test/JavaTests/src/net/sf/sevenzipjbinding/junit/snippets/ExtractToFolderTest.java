package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * Tests the {@link ExtractToFolder} snippet: it must print the same per-file hash/size table as the
 * other extract snippets AND actually drop the files onto disk in the target directory.
 */
public class ExtractToFolderTest extends SnippetTest {
    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(ExtractToFolder) */
        String expected = "   Hash   |    Size    | Filename\n";
        expected += "----------+------------+---------\n";
        expected += " C1FD1029 |       4481 | file1.txt\n";
        expected += " 8CB12E6A |         75 | file2.txt\n";
        expected += " E8EEC7F4 |          6 | folder/file in folder.txt\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testExtractToFolder() {
        String expected = getExpectedOutput();

        File outputDir = new File(System.getProperty("java.io.tmpdir"),
                "sevenzipjbinding-extract-to-folder-" + System.nanoTime());

        beginSnippetTest();
        // Call run() (not main() - main() calls System.exit, which would kill the test JVM).
        int exitCode = ExtractToFolder.run(new String[] { "testdata/snippets/simple.zip", outputDir.getPath() });
        String output = endSnippetTest();

        assertEquals(expected, output);
        assertEquals("a clean extraction must exit 0", 0, exitCode);

        // the files must really be on disk now, with the expected sizes
        assertFile(outputDir, "file1.txt", 4481);
        assertFile(outputDir, "file2.txt", 75);
        assertFile(outputDir, "folder" + File.separator + "file in folder.txt", 6);

        deleteRecursively(outputDir);
    }

    private void assertFile(File dir, String relativePath, long expectedSize) {
        File file = new File(dir, relativePath);
        assertTrue("missing extracted file: " + file, file.isFile());
        assertEquals("wrong size for " + relativePath, expectedSize, file.length());
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
