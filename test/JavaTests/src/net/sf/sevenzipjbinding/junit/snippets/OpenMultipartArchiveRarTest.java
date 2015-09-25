package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link OpenMultipartArchiveRar} snippet
 * 
 * @author Boris Brodski
 * @since 4.65-1
 */
public class OpenMultipartArchiveRarTest extends SnippetTest {
    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(OpenMultipartRarArchiveOutput) */
        String expected = "   Size   | Compr.Sz. | Filename\n";
        expected += "----------+-----------+---------\n";
        expected += "        6 |         6 | folder/file in folder.txt\n";
        expected += "     4481 |      4481 | file1.txt\n";
        expected += "       75 |        75 | file2.txt\n";
        expected += "        0 |         0 | folder\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testListItemsInArchiveStandard() {
        String expected = getExpectedOutput();

        beginSnippetTest();
        OpenMultipartArchiveRar.main(new String[] { "testdata/snippets/multipart-rar.part1.rar" });
        String output = endSnippetTest();
        assertEquals(expected, output);
    }
}
