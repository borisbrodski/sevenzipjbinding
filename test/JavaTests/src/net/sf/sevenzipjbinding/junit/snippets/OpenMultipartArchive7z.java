package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(OpenMultipartArchive7z) */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.VolumedArchiveInStream;

public class OpenMultipartArchive7z {
    /**
     * In this example we use VolumedArchiveInStream class only. <br>
     * It means, we doesn't pass instances of this class directly <br>
     * to 7-Zip, so not complete implementation<br>
     * of {@link IArchiveOpenVolumeCallback} is required. <br>
     * See VolumedArchiveInStream JavaDoc for more information
     */
    private static class ArchiveOpenVolumeCallback //
            implements IArchiveOpenVolumeCallback {

        /**
         * Cache for opened file streams
         */
        private Map<String, RandomAccessFile> /*f*/openedRandomAccessFileList/* */= //
        /*                */new HashMap<String, RandomAccessFile>();

        /**
         * This method doesn't needed, if using with VolumedArchiveInStream <br>
         * and pass the name of the first archive in constructor. <br>
         * (Use two argument constructor)
         * 
         * @see IArchiveOpenVolumeCallback#getProperty(PropID)
         */
        public Object getProperty(PropID propID) throws SevenZipException {
            return null;
        }

        /**
         * 
         * The name of the required volume will be calculated out of the<br>
         * name of the first volume and volume index. If you need <br>
         * need volume index (integer) you will have to parse filename<br>
         * and extract index.
         * 
         * <pre>
         * int index = filename.substring(filename.length() - 3, //
         *         filename.length());
         * </pre>
         * 
         */
        public IInStream getStream(String filename) throws SevenZipException {
            try {
                // We use caching of opened streams, so check cache first
                RandomAccessFile randomAccessFile = /*f*/openedRandomAccessFileList/**///
                        .get(filename);
                if (randomAccessFile != null) { // Cache hit.
                    // Move the file pointer back to the beginning
                    // in order to emulating new stream
                    randomAccessFile.seek(0);
                    return new RandomAccessFileInStream(randomAccessFile);
                }

                // Nothing useful in cache. Open required volume.
                randomAccessFile = new RandomAccessFile(filename, "r");

                // Put new stream in the cache
                /*f*/openedRandomAccessFileList/**/.put(filename, randomAccessFile);

                return new RandomAccessFileInStream(randomAccessFile);
            } catch (FileNotFoundException fileNotFoundException) {
                // Required volume doesn't exist. This happens if the volume:
                // 1. never exists. 7-Zip doesn't know how many volumes should
                //    exist, so it have to try each volume.
                // 2. should be there, but doesn't. This is an error case.

                // Since normal and error cases are possible,
                // we can't throw an error message
                return null; // We return always null in this case
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Close all opened streams
         */
        void close() throws IOException {
            for (RandomAccessFile file : /*f*/openedRandomAccessFileList/**/.values()) {
                file.close();
            }
        }
    }

    public static void main(String[] args) {
        if (args./*f*/length/* */== 0) {
            System.out.println(//
                    "Usage: java OpenMultipartArchive7z <first-volume>");
            return;
        }
        ArchiveOpenVolumeCallback archiveOpenVolumeCallback = null;
        IInArchive inArchive = null;
        try {

            archiveOpenVolumeCallback = new ArchiveOpenVolumeCallback();
            inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, //
                    new VolumedArchiveInStream(args[0], //
                            archiveOpenVolumeCallback));

            System.out.println("   Size   | Compr.Sz. | Filename");
            System.out.println("----------+-----------+---------");
            int itemCount = inArchive.getNumberOfItems();
            for (int i = 0; i < itemCount; i++) {
                System.out.println(String.format("%9s | %9s | %s", // 
                        inArchive.getProperty(i, PropID.SIZE), //
                        inArchive.getProperty(i, PropID.PACKED_SIZE), //
                        inArchive.getProperty(i, PropID.PATH)));
            }
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (archiveOpenVolumeCallback != null) {
                try {
                    archiveOpenVolumeCallback.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
    }
}
/* END_SNIPPET */