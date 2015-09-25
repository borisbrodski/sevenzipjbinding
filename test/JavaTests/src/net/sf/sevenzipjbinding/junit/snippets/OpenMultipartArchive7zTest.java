package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link OpenMultipartArchive7z} snippet
 * 
 * @author Boris Brodski
 * @since 4.65-1
 */
public class OpenMultipartArchive7zTest extends SnippetTest {
    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(OpenMultipart7zArchiveOutput) */
        String expected = "   Size   | Compr.Sz. | Filename\n";
        expected += "----------+-----------+---------\n";
        expected += "        6 |       114 | folder/file in folder.txt\n";
        expected += "     4481 |      null | file1.txt\n";
        expected += "       75 |      null | file2.txt\n";
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
        OpenMultipartArchive7z.main(new String[] { "testdata/snippets/multipart-7z.7z.001" });
        String output = endSnippetTest();
        assertEquals(expected, output);
    }
}
