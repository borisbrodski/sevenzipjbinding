package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(CompressWithPassword) */
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.junit.snippets.CompressArchiveStructure.Item;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class CompressWithPassword {
    /**
     * The callback provides information about archive items.<br>
     * <br>
     * It also implements
     * <ul>
     * <li>{@link ICryptoGetTextPassword}
     * </ul>
     * to provide a password for encryption.
     */
    private final class MyCreateCallback //
            implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {
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

        public IOutItem7z getItemInformation(int index,//
                OutItemFactory<IOutItem7z> outItemFactory) {
            IOutItem7z item = outItemFactory.createOutItem();

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

        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
        }
    }

    private Item[] items;
    private String password;

    public static void main(String[] args) {
        if (args./*f*/length/* */ == 2) {
            new CompressWithPassword().compress(args[0], args[1]);
            return;
        }
        System.out.println("Usage: java CompressWithPassword <archive> <pass>");
    }

    private void compress(String filename, String pass) {
        items = CompressArchiveStructure.create();
        password = pass;

        boolean success = false;
        RandomAccessFile raf = null;
        IOutCreateArchive7z outArchive = null;
        try {
            raf = new RandomAccessFile(filename, "rw");

            // Open out-archive object
            outArchive = SevenZip.openOutArchive7z();

            // Header encryption is only available for 7z
            outArchive.setHeaderEncryption(true);

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