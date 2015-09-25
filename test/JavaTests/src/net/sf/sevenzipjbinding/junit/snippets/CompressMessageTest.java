package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link CompressMessage} snippet.
 * 
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressMessageTest extends SnippetTest {
    private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";

    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(CompressMessage) */
        String expected = "Compression operation succeeded\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testCompressMessage() {

        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed_message.zip");

        beginSnippetTest();
        CompressMessage.main(new String[] { archiveFile.getAbsolutePath(), "my cool message" });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(), output);
    }
}
