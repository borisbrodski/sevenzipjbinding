package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(CompressMessage) */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class CompressMessage {
    /**
     * The callback defines structure of the archive being created
     */
    private static final class MyCreateCallback //
            implements IOutCreateCallback<IOutItemZip> {
        private final byte[] /*f*/bytesToCompress/**/;

        private MyCreateCallback(byte[] bytesToCompress) {
            this./*f*/bytesToCompress/* */= bytesToCompress;
        }

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
        }

        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
        }

        public IOutItemZip getItemInformation(int index, OutItemFactory<IOutItemZip> outItemFactory)
                throws SevenZipException {
            IOutItemZip outItem = outItemFactory.createOutItem();

            // Convert to message to compress into sequential byte stream
            outItem.setDataStream(new ByteArrayStream(/*f*/bytesToCompress/**/, true));

            outItem.setPropertySize((long) /*f*/bytesToCompress/**/./*f*/length/**/);
            outItem.setPropertyPath("message.txt"); // Set name of the file in the archive
            outItem.setPropertyCreationTime(new Date());

            // Use this to get u+rw permissions on linux, if extracting with unzip
            // outItem.setPropertyAttributes(Integer.valueOf(0x81808000));

            return outItem;
        }

        public void freeResources(int index, IOutItemZip outItem) {
            // no need to close ByteArrayStream
        }
    }

    public static void main(String[] args) {
        if (args./*f*/length/* */!= 2) {
            System.out.println("Usage: java CompressMessage <archive> <msg>");
            return;
        }

        final byte[] bytesToCompress = args[1].getBytes();

        RandomAccessFile raf = null;
        IOutCreateArchiveZip outArchive = null;
        try {
            raf = new RandomAccessFile(args[0], "rw");

            outArchive = SevenZip.openOutArchiveZip();
            outArchive.setLevel(5);
            outArchive.createArchive(new RandomAccessFileOutStream(raf), 1, //
                    new MyCreateCallback(bytesToCompress));

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