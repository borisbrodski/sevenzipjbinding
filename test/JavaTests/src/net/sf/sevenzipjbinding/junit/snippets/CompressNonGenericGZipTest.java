package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link CompressNonGenericGZip} snippet.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressNonGenericGZipTest extends SnippetTest {
    private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";

    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(CompressNonGenericGZip) */
        String expected = "Compression operation succeeded\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testCompress() {
        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed.gz");

        beginSnippetTest();
        CompressNonGenericGZip.main(new String[] { archiveFile.getAbsolutePath() });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(), output);
    }
}
