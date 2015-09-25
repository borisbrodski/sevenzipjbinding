package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link ListItemsSimple} and {@link ListItemsStandard} snippets
 * 
 * @author Boris Brodski
 * @since 4.65-1
 */
public class ListItemsTest extends SnippetTest {
    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(ListItemsOutput) */
        String expected = "   Size   | Compr.Sz. | Filename\n";
        expected += "----------+-----------+---------\n";
        expected += "     4481 |        68 | file1.txt\n";
        expected += "       75 |        62 | file2.txt\n";
        expected += "        0 |         0 | folder\n";
        expected += "        6 |         6 | folder/file in folder.txt\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testListItemsInArchiveStandard() {
        String expected = getExpectedOutput();

        beginSnippetTest();
        ListItemsStandard.main(new String[] { "testdata/snippets/simple.zip" });
        String output = endSnippetTest();
        assertEquals(expected, output);
    }

    @Test
    public void testListItemsInArchiveSimple() {
        String expected = getExpectedOutput();

        beginSnippetTest();
        ListItemsStandard.main(new String[] { "testdata/snippets/simple.zip" });
        String output = endSnippetTest();
        assertEquals(expected, output);
    }
}
