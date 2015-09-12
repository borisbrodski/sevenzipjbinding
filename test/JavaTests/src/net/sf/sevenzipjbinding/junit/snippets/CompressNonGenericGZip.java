package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(CompressNonGenericGZip) */
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchiveGZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemGZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class CompressNonGenericGZip {
    /**
     * The callback provides information about archive items.
     */
    private final class MyCreateCallback //
            implements IOutCreateCallback<IOutItemGZip> {

        public void setOperationResult(boolean operationResultOk)//
                throws SevenZipException {
            // Track each operation result here
        }

        public void setTotal(long total) throws SevenZipException {
            // Track operation progress here
        }

        public void setCompleted(long complete) throws SevenZipException {
            // Track operation progress here
        }

        public IOutItemGZip getItemInformation(int index,//
                OutItemFactory<IOutItemGZip> outItemFactory) {
            IOutItemGZip item = outItemFactory.createOutItem();

            item.setDataSize((long) /*f*/content/**/./*f*/length/**/);

            return item;
        }

        public ISequentialInStream getStream(int i) throws SevenZipException {
            return new ByteArrayStream(/*f*/content/**/, true);
        }
    }

    byte[] content;

    public static void main(String[] args) {
        if (args./*f*/length/* */== 1) {
            new CompressNonGenericGZip().compress(args[0]);
            return;
        }
        System.out.println("Usage: java CompressNonGenericGZip <archive>");
    }

    private void compress(String filename) {
        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchiveGZip outArchive = null;
        content = CompressArchiveStructure.create()[0].getContent();
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchiveGZip();

            // Configure archive
            outArchive.setLevel(5);

            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),//
                    1, new MyCreateCallback());

            success = true;
        } catch (SevenZipException e) {
            System.err.println("GZip-Error occurs:");
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
                    success = false;
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    success = false;
                }
            }
        }
        if (success) {
            System.out.println("Compression operation succeeded");
        }
    }
}
/* END_SNIPPET */