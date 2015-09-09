package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link CompressNonGenericBZip2} snippet.
 *
 * @author Boris Brodski
 * @version 9.20-2.00
 */
public class CompressNonGenericBZip2Test extends SnippetTest {
    private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";

    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(CompressNonGenericBZip2) */
        String expected = "Compression operation succeeded\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", System.getProperty("line.separator"));
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testCompress() {
        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed.bz2");

        beginSnippetTest();
        CompressNonGenericBZip2.main(new String[] { archiveFile.getAbsolutePath(), "Hello World" });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(), output);
    }
}
