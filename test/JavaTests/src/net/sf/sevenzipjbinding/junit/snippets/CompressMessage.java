package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(CompressMessage) */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallbackZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

public class CompressMessage {
    /**
     * The callback defines structure of the archive being created
     */
    private static final class MyCreateCallback //
            implements IOutCreateCallback<IOutItemCallbackZip> {
        private final byte[] /*f*/bytesToCompress/**/;

        private MyCreateCallback(byte[] bytesToCompress) {
            this./*f*/bytesToCompress/* */= bytesToCompress;
        }

        public ISequentialInStream getStream(int index)//
                throws SevenZipException {
            // Convert to message to compress into sequential byte stream
            return new ByteArrayStream(/*f*/bytesToCompress/**/, true);
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

        public IOutItemCallbackZip getOutItemCallback(int index) //
                throws SevenZipException {
            // Creating a archive with a single item.
            // The 'index' will always be 0 here.
            return new MyItemCallbackZip(bytesToCompress);
        }
    }

    /**
     * The callback provides information about archive items.<br>
     * In this example it's a file named "message.txt" with the message.
     */
    private static final class MyItemCallbackZip implements IOutItemCallbackZip {
        private final byte[] /*f*/bytesToCompress/**/;

        private MyItemCallbackZip(byte[] bytesToCompress) {
            this./*f*/bytesToCompress/* */= bytesToCompress;
        }

        public boolean isDir() throws SevenZipException {
            return false; // Regular file
        }

        public long getSize() throws SevenZipException {
            return /*f*/bytesToCompress/**/./*f*/length/**/;
        }

        public String getPath() throws SevenZipException {
            return "message.txt"; // Set name of the file in the archive
        }

        public Date getModificationTime() throws SevenZipException {
            return null;
        }

        public Integer getAttributes() throws SevenZipException {
            return null; // Use 0x81808000 to get u+rw permissions on linux
        }

        public Date getLastAccessTime() throws SevenZipException {
            return null;
        }

        public Date getCreationTime() throws SevenZipException {
            return new Date();
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