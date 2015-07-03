package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(PrintCountOfItems) */
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class PrintCountOfItems {
    public static void main(String[] args) {
        if (args./*f*/length/* */== 0) {
            System.out.println("Usage: java PrintCountOfItems <archive-name>");
            return;
        }
        String archiveFilename = args[0];

        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(archiveFilename, "r");
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));

            System.out.println("Count of items in archive: " //
                    + inArchive.getNumberOfItems());

        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
    }
}

/* END_SNIPPET */
