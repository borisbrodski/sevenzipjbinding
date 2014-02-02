package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import org.junit.Test;

public class FirstStepsSimpleSnippets {

    private static final String TEST_ARCHIVE_SIMPLE = "testdata/snippets/simple.zip";
    private IInArchive inArchive;
    private RandomAccessFile randomAccessFile;

    @Test
    public void testOpenArchiveSnippet() throws Exception {
        openArchive(TEST_ARCHIVE_SIMPLE);
        assertNotNull(inArchive);
        inArchive.close();
        randomAccessFile.close();
    }

    /* BEGIN_SNIPPET(SimpleOpen) */
    public void openArchive(String archiveFilename) //
            throws SevenZipException, FileNotFoundException {

        /*f*/randomAccessFile/* */= new RandomAccessFile(archiveFilename, "r");

        /*f*/inArchive/* */= SevenZip.openInArchive(null, // Choose format automatically
                new RandomAccessFileInStream(randomAccessFile));
    }
    /* END_SNIPPET */
}
