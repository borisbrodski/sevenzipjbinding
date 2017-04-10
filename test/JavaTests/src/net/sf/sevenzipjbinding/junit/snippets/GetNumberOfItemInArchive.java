package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.RandomAccessFile;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class GetNumberOfItemInArchive {
    @Test
    public void snippetRunner() throws Exception {
        assertEquals(4, getNumberOfItemsInArchive("testdata/snippets/simple.zip"));
    }

    /* BEGIN_SNIPPET(GetNumberOfItemsInArchive) */
    private int getNumberOfItemsInArchive(String archiveFile) throws Exception {
        IInArchive archive;
        RandomAccessFile randomAccessFile;

        randomAccessFile = new RandomAccessFile(archiveFile, "r");

        archive = SevenZip.openInArchive(ArchiveFormat.ZIP, // null - autodetect
                new RandomAccessFileInStream(randomAccessFile));

        int numberOfItems = archive.getNumberOfItems();

        archive.close();
        randomAccessFile.close();

        return numberOfItems;
    }
    /* END_SNIPPET */
}
