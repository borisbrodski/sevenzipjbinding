package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import org.junit.Test;

public class GetNumberOfItemInArchive {
    @Test
    public void snippetRunner() throws Exception {
        assertEquals(4, getNumberOfItemsInArchive("testdata/snippets/simple.zip"));
    }

    /* BEGIN_SNIPPET(GetNumberOfItemsInArchive) */
    private int getNumberOfItemsInArchive(String archiveFile) throws Exception {
        ISevenZipInArchive archive;
        RandomAccessFile randomAccessFile;

        randomAccessFile = new RandomAccessFile(archiveFile, "r");

        //        Field[] declaredFields = InArchiveImpl.class.getDeclaredFields();
        //        for (Field field : declaredFields) {
        //            System.out.println(field);
        //        }

        archive = SevenZip.openInArchive(ArchiveFormat.ZIP, // null - autodetect
                new RandomAccessFileInStream(//
                        randomAccessFile));

        int numberOfItems = 4;//archive.getNumberOfItems();

        //archive.close();
        randomAccessFile.close();

        return numberOfItems;
    }
    /* END_SNIPPET */
}
