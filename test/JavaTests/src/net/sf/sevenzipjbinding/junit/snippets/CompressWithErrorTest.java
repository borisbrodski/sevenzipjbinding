package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests {@link CompressNonGeneric7z} snippet.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressWithErrorTest extends SnippetTest {
    private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";

    private String getExpectedOutput() {
        /* BEGIN_OUTPUT(CompressWithError) */
        String expected = "Compressing 5 items\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 0)\n" + //
                "Get property 'propertyIsDir' (index: 0)\n" + //
                "Get property 'propertyPosixAttributes' (index: 0)\n" + //
                "Get property 'propertyLastModificationTime' (index: 0)\n" + //
                "Get property 'propertyPath' (index: 0)\n" + //
                "Get property 'propertyUser' (index: 0)\n" + //
                "Get property 'propertyGroup' (index: 0)\n" + //
                "Get property 'dataSize' (index: 0)\n" + //
                "Get update info (new data: true) (new props: true) (old index: -1) (index: 1)\n" + //
                "Get property 'propertyIsDir' (index: 1)\n" + //
                "Get property 'propertyPosixAttributes' (index: 1)\n" + //
                "Get property 'propertyLastModificationTime' (index: 1)\n" + //
                "Get property 'propertyPath' (index: 1)\n" + //
                "Get property 'propertyUser' (index: 1)\n" + //
                "Get property 'propertyGroup' (index: 1)\n" + //
                "Get property 'dataSize' (index: 1)\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    private String getExpectedErrorBegin() {
        /* BEGIN_OUTPUT(CompressWithErrorErr) */
        String expected = "0x80070057 (Invalid argument). Error creating 'tar' archive with 5 items";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        expected = expected.replaceAll("    ", "\t");
        expected = expected.replace("...", "");
        return expected;
    }

    @Test
    public void testCompress() {

        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed.7z");

        beginSnippetFailingTest();
        CompressWithError.main(new String[] { archiveFile.getAbsolutePath() });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(), output);
        String error = getSnippetErrOutput();
        if (!error.contains(getExpectedErrorBegin())) {
            assertEquals(getExpectedErrorBegin(), error);
        }
    }
}
