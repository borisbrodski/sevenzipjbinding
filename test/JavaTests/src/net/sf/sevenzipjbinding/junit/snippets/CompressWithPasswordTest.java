package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link CompressWithPassword} snippet.
 *
 * @author Boris Brodski
 * @since 15.09-2.01
 */
public class CompressWithPasswordTest extends SnippetTest {
    private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";
    private static final String PASSWORD = "12345";

    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(CompressWithPassword) */
        String expected = "Compression operation succeeded\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testCompress() {

        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed-with-pass-" + PASSWORD + ".7z");

        beginSnippetTest();
        CompressWithPassword.main(new String[] { archiveFile.getAbsolutePath(), PASSWORD });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(), output);
    }
}
