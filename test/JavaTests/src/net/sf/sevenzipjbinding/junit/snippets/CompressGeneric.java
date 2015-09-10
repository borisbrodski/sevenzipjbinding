package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(CompressGeneric) */
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class CompressGeneric {
    /**
     * The callback provides information about archive items.
     */
    private final class MyCreateCallback //
            implements IOutCreateCallback<IOutItemAllFormats> {

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

        public IOutItemAllFormats getItemInformation(int index,//
                OutItemFactory<IOutItemAllFormats> outItemFactory) {
            IOutItemAllFormats item = outItemFactory.createOutItem();

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
        if (args./*f*/length/* */!= 3) {
            System.out.println("Usage: java CompressGeneric " //
                    + "<archive-format> <archive> <count-of-files>");
            for (ArchiveFormat af : ArchiveFormat.values()) {
                if (af.isOutArchiveSupported()) {
                    System.out.println("Supported formats: " + af.name());
                }
            }
            return;
        }

        int itemsCount = Integer.valueOf(args[2]);
        new CompressGeneric().compress(args[0], args[1], itemsCount);
    }


    private void compress(String filename, String fmtName, int count) {
        items = CompressArchiveStructure.create();

        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchive<IOutItemAllFormats> outArchive = null;
        ArchiveFormat archiveFormat = ArchiveFormat.valueOf(fmtName);
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchive(archiveFormat);

            // Configure archive
            if (outArchive instanceof IOutFeatureSetLevel) {
                ((IOutFeatureSetLevel) outArchive).setLevel(5);
            }
            if (outArchive instanceof IOutFeatureSetMultithreading) {
                ((IOutFeatureSetMultithreading) outArchive).setThreadCount(2);
            }

            // Create archive
            outArchive.createArchive(new RandomAccessFileOutStream(raf),//
                    count, new MyCreateCallback());

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
            System.out.println(archiveFormat.getMethodName() //
                    + " archive with " + count + " item(s) created");
        }
    }
}
/* END_SNIPPET */