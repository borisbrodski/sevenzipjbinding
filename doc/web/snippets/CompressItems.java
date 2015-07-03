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

public class CompressItems {
    /**
     * This callback provides the structure of the archive being created
     */
    private static final class MyCreateCallback 
            implements IOutCreateCallback<IOutItemCallbackZip> {
        private final byte[] bytesToCompress;

        private MyCreateCallback(byte[] bytesToCompress) {
            this.bytesToCompress = bytesToCompress;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            // Convert to message to compress into sequential byte stream
            return new ByteArrayStream(bytesToCompress, true);
        }

        public void setOperationResult(boolean operationResultOk)
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

        public IOutItemCallbackZip getOutItemCallback(int index) throws SevenZipException {
            // Creating the archive with a single item. The 'index' will be always 0 here.
            return new MyItemCallbackZip(bytesToCompress);
        }
    }

    /**
     * This callback provides information about a archive items.
     * In this example it's a file named "message.txt" with the message.
     */
    private static final class MyItemCallbackZip implements IOutItemCallbackZip {
        private final byte[] bytesToCompress;

        private MyItemCallbackZip(byte[] bytesToCompress) {
            this.bytesToCompress = bytesToCompress;
        }

        public boolean isDir() throws SevenZipException {
            return false; // Regular file
        }

        public long getSize() throws SevenZipException {
            return bytesToCompress.length;
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
        if (args.length != 2) {
            System.out.println("Usage: java CompressItems <archive-name> <message>");
            return;
        }

        final byte[] bytesToCompress = args[1].getBytes();

        RandomAccessFile randomAccessFile = null;
        IOutCreateArchiveZip outArchive = null;
        try {
            outArchive = SevenZip.openOutArchiveZip();
            outArchive.setLevel(5);
            randomAccessFile = new RandomAccessFile(args[0], "rw");

            outArchive.createArchive(new RandomAccessFileOutStream(randomAccessFile), 1, 
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
