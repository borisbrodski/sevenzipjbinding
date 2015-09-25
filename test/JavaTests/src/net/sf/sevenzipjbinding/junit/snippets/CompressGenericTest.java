package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;

import net.sf.sevenzipjbinding.ArchiveFormat;

import org.junit.Test;

/**
 * Tests {@link CompressGeneric} snippet.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressGenericTest extends SnippetTest {
    private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";
    /* BEGIN_OUTPUT(CompressGenericNoParameter) */
    String expectedNoParameter = "Usage: java CompressGeneric <archive-format> <archive> <count-of-files>\n" //
            + "Supported formats: ZIP\n" //
            + "Supported formats: TAR\n" //
            + "Supported formats: GZIP\n" //
            + "Supported formats: BZIP2\n" //
            + "Supported formats: SEVEN_ZIP\n";

    /* END_OUTPUT */

    /* BEGIN_OUTPUT(CompressGeneric7z) */
    String expected7z = "7z archive with 5 item(s) created\n";
    /* END_OUTPUT */

    /* BEGIN_OUTPUT(CompressGenericZip) */
    String expectedZip = "Zip archive with 5 item(s) created\n";
    /* END_OUTPUT */

    /* BEGIN_OUTPUT(CompressGenericTar) */
    String expectedTar = "Tar archive with 5 item(s) created\n";
    /* END_OUTPUT */

    /* BEGIN_OUTPUT(CompressGenericGZip) */
    String expectedGZip = "GZip archive with 1 item(s) created\n";
    /* END_OUTPUT */

    /* BEGIN_OUTPUT(CompressGenericBZip2) */
    String expectedBZip2 = "BZip2 archive with 1 item(s) created\n";

    /* END_OUTPUT */

    private String getExpectedOutput(String expected) {
        expected = expected.replace("\n", NEW_LINE);
        expected = expected.replace('/', File.separatorChar);
        return expected;
    }

    @Test
    public void testNoParameter() {
        beginSnippetTest();
        CompressGeneric.main(new String[0]);
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(expectedNoParameter), output);
    }

    @Test
    public void testCompress7z() {

        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed-generic.7z");

        beginSnippetTest();
        CompressGeneric.main(new String[] { archiveFile.getAbsolutePath(), ArchiveFormat.SEVEN_ZIP.name(),
                "" + CompressArchiveStructure.create().length });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(expected7z), output);
    }

    @Test
    public void testCompressZip() {

        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed-generic.zip");

        beginSnippetTest();
        CompressGeneric.main(new String[] { archiveFile.getAbsolutePath(), ArchiveFormat.ZIP.name(),
                "" + CompressArchiveStructure.create().length });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(expectedZip), output);
    }

    @Test
    public void testCompressTar() {

        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed-generic.tar");

        beginSnippetTest();
        CompressGeneric.main(new String[] { archiveFile.getAbsolutePath(), ArchiveFormat.TAR.name(),
                "" + CompressArchiveStructure.create().length });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(expectedTar), output);
    }

    @Test
    public void testCompressGZip() {

        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed-generic.gz");

        beginSnippetTest();
        CompressGeneric.main(new String[] { archiveFile.getAbsolutePath(), ArchiveFormat.GZIP.name(), "1" });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(expectedGZip), output);
    }

    @Test
    public void testCompressBZip2() {

        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "compressed-generic.bz2");

        beginSnippetTest();
        CompressGeneric.main(new String[] { archiveFile.getAbsolutePath(), ArchiveFormat.BZIP2.name(), "1" });
        String output = endSnippetTest();
        assertEquals(getExpectedOutput(expectedBZip2), output);
    }

}
