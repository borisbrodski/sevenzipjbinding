package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(CompressNonGeneric) */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Random;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.OutItemZip;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class Compress {
    /**
     * The callback defines structure of the archive being created
     */
    private final class MyCreateCallback //
            implements IOutCreateCallback<OutItemZip> {

        public void setOperationResult(boolean operationResultOk)//
                throws SevenZipException {
            if (operationResultOk) {
                System.out.println("Compression operation succeeded");
            } else {
                System.out.println("Compression operation failed");
            }
        }

        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
            System.out.println("Total: " + total);
        }

        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
            System.out.println("Complete: " + complete);
        }

        public OutItemZip getOutItem(int index) throws SevenZipException {
            OutItemZip item = new OutItemZip();

            item.setStream(new ByteArrayStream(/*f*/contents/**/[index], true));
            item.setSize((long) /*f*/contents/**/[index].length);
            item.setPath(/*f*/filenames/**/[/*f*/index/**/]);
            item.setCreationTime(new Date());

            // Use to get u+rw permissions on linux with 'unzip'
            // item.setAttributes(0x81808000);

            return item;
        }
    }

    private byte[][] contents;
    private String[] filenames;

    public static void main(String[] args) {
        if (args./*f*/length/* */!= 1) {
            System.out.println("Usage: java Compress <archive>");
            return;
        }

        new Compress().compress(args[0]);
    }


    private void initArchiveStructure() {
        contents = new byte[3][];
        filenames = new String[3];

        filenames[0] = "info.txt";
        contents[0] = "This is the info".getBytes();

        filenames[1] = "random-100-bytes.bin";
        contents[1] = new byte[100];
        new Random().nextBytes(contents[1]);

        filenames[2] = "dir" + File.separator + "file-in-a-directory.txt";
        contents[2] = "This file located in a directory 'dir'".getBytes();
    }

    private void compress(String filename) {
        initArchiveStructure();

        RandomAccessFile raf = null;
        IOutCreateArchiveZip outArchive = null;
        try {
            raf = new RandomAccessFile(filename, "rw");

            outArchive = SevenZip.openOutArchiveZip();
            outArchive.setLevel(5);
            outArchive.createArchive(new RandomAccessFileOutStream(raf), /*f*/contents/**/./*f*/length/**/, //
                    new MyCreateCallback());

        } catch (SevenZipException e) {
            System.err.println("7z-Error occurs:");
            e.printStackTraceExtended();
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (outArchive != null) {
                try {
                    outArchive.close();
                } catch (IOException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
    }
}
/* END_SNIPPET */