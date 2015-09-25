package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link CompressGeneric} snippet.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class UpdateAddRemoveItemsTest extends SnippetTest {
    private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";
    /* BEGIN_OUTPUT(UpdateAddRemoveItems) */
    String expected7z = "Update successful\n";
    /* END_OUTPUT */

    private String getExpectedOutput(String expected) {
        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }


    @Test
    public void testCompress7z() {
        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "updated-add-remove-items.7z");

        beginSnippetTest();
        UpdateAddRemoveItems.main(new String[] { "testdata/snippets/to-update.7z", archiveFile.getAbsolutePath() });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(expected7z), output);
    }
}
