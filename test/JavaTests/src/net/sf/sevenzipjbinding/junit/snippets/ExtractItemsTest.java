package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link ExtractItemsSimple} and {@link ExtractItemsStandard} snippets
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public class ExtractItemsTest extends SnippetTest {
    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(ExtractItems) */
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
    public void testExtractItemsSimple() {
        String expected = getExpectedOutput();

        beginSnippetTest();
        ExtractItemsSimple.main(new String[] { "testdata/snippets/simple.zip" });
        String output = endSnippetTest();
        assertEquals(expected, output);
    }

    @Test
    public void testExtractItemsStandard() {
        String expected = getExpectedOutput();

        beginSnippetTest();
        ExtractItemsStandard.main(new String[] { "testdata/snippets/simple.zip" });
        String output = endSnippetTest();
        assertEquals(expected, output);
    }

    @Test
    public void testExtractItemsStandardCallback() {
        String expected = getExpectedOutput();

        beginSnippetTest();
        ExtractItemsStandardCallback.main(new String[] { "testdata/snippets/simple.zip" });
        String output = endSnippetTest();
        assertEquals(expected, output);
    }
}
