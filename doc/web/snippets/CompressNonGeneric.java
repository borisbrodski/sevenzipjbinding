import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Random;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class Compress {
    /**
     * The callback provides information about archive items
     */
    private final class MyCreateCallback 
            implements IOutCreateCallback<IOutItemZip> {

        public void setOperationResult(boolean operationResultOk)
                throws SevenZipException {
            // Track each operation result here
        }

        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
        }

        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
        }

        public IOutItemZip getItemInformation(int index,
                OutItemFactory<IOutItemZip> outItemFactory) {
            IOutItemZip item = outItemFactory.createOutItem();

            item.setDataSize((long) contents[index].length);

            item.setPropertyPath(filenames[index]);
            item.setPropertyCreationTime(new Date());

            // Use to get u+rw permissions on linux with 'unzip'
            // item.setPropertyAttributes(0x81808000);

            return item;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return new ByteArrayStream(contents[index], true);
        }
    }

    private byte[][] contents;
    private String[] filenames;

    public static void main(String[] args) {
        if (args.length != 1) {
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
            outArchive.createArchive(new RandomAccessFileOutStream(raf),
                    contents.length, new MyCreateCallback());

            System.out.println("Compression operation succeeded");
        } catch (SevenZipException e) {
            System.err.println("7z-Error occurs:");
            // Get more information using extended method
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
