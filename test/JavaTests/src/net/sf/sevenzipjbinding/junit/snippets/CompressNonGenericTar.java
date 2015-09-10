package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(CompressNonGenericTar) */
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchiveTar;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class CompressNonGenericTar {
    /**
     * The callback provides information about archive items.
     */
    private final class MyCreateCallback //
            implements IOutCreateCallback<IOutItemTar> {

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

        public IOutItemTar getItemInformation(int index,//
                OutItemFactory<IOutItemTar> outItemFactory) {
            IOutItemTar item = outItemFactory.createOutItem();

            if (/*f*/items/**/[index].getContent() == null) {
                // Directory
                item.setPropertyIsDir(true);
            } else {
                // File
                item.setDataSize((long) /*f*/items/**/[index].getContent()./*f*/length/**/);
            }

            item.setPropertyPath(/*f*/items/**/[index].getPath());

            return item;
        }

        public ISequentialInStream getStream(int i) throws SevenZipException {
            if (/*f*/items/**/[i].getContent() == null) {
                return null;
            }
            return new ByteArrayStream(/*f*/items/**/[i].getContent(), true);
        }
    }

    private Item[] items;

    public static void main(String[] args) {
        if (args./*f*/length/* */== 1) {
            new CompressNonGenericTar().compress(args[0]);
            return;
        }
        System.out.println("Usage: java CompressNonGenericTar <archive>");
    }


    private void compress(String filename) {
        items = CompressArchiveStructure.create();

        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchiveTar outArchive = null;
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchiveTar();

            // No configuration methods for Tar

            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),//
                    /*f*/items/**/./*f*/length/**/, new MyCreateCallback());

            success = true;
        } catch (SevenZipException e) {
            System.err.println("Tar-Error occurs:");
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