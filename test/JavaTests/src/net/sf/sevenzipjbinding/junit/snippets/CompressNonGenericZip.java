package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(CompressNonGenericZip) */
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class CompressNonGenericZip {
    /**
     * The callback provides information about archive items.
     */
    private final class MyCreateCallback //
            implements IOutCreateCallback<IOutItemZip> {

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

        public IOutItemZip getItemInformation(int index,//
                OutItemFactory<IOutItemZip> outItemFactory) {
            int attr = PropID.AttributesBitMask./*sf*/FILE_ATTRIBUTE_UNIX_EXTENSION/**/;

            IOutItemZip item = outItemFactory.createOutItem();

            if (/*f*/items/**/[index].getContent() == null) {
                // Directory
                item.setPropertyIsDir(true);
                attr |= PropID.AttributesBitMask./*sf*/FILE_ATTRIBUTE_DIRECTORY/**/;
                attr |= 0x81ED << 16; // permissions: drwxr-xr-x
            } else {
                // File
                item.setDataSize((long) /*f*/items/**/[index].getContent()./*f*/length/**/);
                attr |= 0x81a4 << 16; // permissions: -rw-r--r--
            }
            item.setPropertyPath(/*f*/items/**/[index].getPath());

            item.setPropertyAttributes(attr);

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
        if (args./*f*/length/* */!= 1) {
            System.out.println("Usage: java Compress <archive>");
            return;
        }

        new CompressNonGenericZip().compress(args[0]);
    }


    private void compress(String filename) {
        items = CompressArchiveStructure.initArchiveStructure();

        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchiveZip outArchive = null;
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchiveZip();

            // Configure archive
            outArchive.setLevel(5);

            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),//
                    /*f*/items/**/./*f*/length/**/, new MyCreateCallback());

            success = true;
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