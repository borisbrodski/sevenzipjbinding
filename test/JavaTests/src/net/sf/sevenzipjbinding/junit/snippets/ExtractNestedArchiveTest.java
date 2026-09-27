package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests the {@link ExtractNestedArchive} snippet: unwrapping a {@code .tar.gz} both in memory and
 * through a temporary file must reach the same inner files and print the same hash/size table.
 */
public class ExtractNestedArchiveTest extends SnippetTest {
    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(ExtractNestedArchive) */
        String expected = "== In memory ==\n";
        expected += "   Hash   |    Size    | Filename\n";
        expected += "----------+------------+---------\n";
        expected += " E606EF4B |         23 | hello.txt\n";
        expected += " D444A5D4 |         10 | data/numbers.txt\n";
        expected += " 179C5D86 |         40 | data/readme.md\n";
        expected += "== Via temporary file ==\n";
        expected += "   Hash   |    Size    | Filename\n";
        expected += "----------+------------+---------\n";
        expected += " E606EF4B |         23 | hello.txt\n";
        expected += " D444A5D4 |         10 | data/numbers.txt\n";
        expected += " 179C5D86 |         40 | data/readme.md\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testExtractNestedArchive() {
        String expected = getExpectedOutput();

        beginSnippetTest();
        ExtractNestedArchive.main(new String[] { "testdata/snippets/wrapped.tar.gz" });
        String output = endSnippetTest();

        assertEquals(expected, output);
    }
}
