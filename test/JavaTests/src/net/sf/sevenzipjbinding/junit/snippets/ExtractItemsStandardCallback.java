package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(ExtractItemsStandardCallback) */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class ExtractItemsStandardCallback {
    public static class MyExtractCallback implements IArchiveExtractCallback {
        private int /*f*/hash/* */= 0;
        private int /*f*/size/* */= 0;
        private int /*f*/index/**/;
        private boolean /*f*/skipExtraction/**/;
        private IInArchive /*f*/inArchive/**/;

        public MyExtractCallback(IInArchive inArchive) {
            this./*f*/inArchive/* */= inArchive;
        }

        public ISequentialOutStream getStream(int index, //
                ExtractAskMode extractAskMode) throws SevenZipException {
            this./*f*/index/* */= index;
            /*f*/skipExtraction/* */= (Boolean) /*f*/inArchive/**///
                    .getProperty(index, PropID./*sf*/IS_FOLDER/**/);
            if (/*f*/skipExtraction/* */|| extractAskMode != ExtractAskMode./*sf*/EXTRACT/**/) {
                return null;
            }
            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    /*f*/hash/* */^= Arrays.hashCode(data);
                    size += data.length;
                    return data./*f*/length/**/; // Return amount of proceed data
                }
            };
        }

        public void prepareOperation(ExtractAskMode extractAskMode) //
                throws SevenZipException {
        }

        public void setOperationResult(ExtractOperationResult //
                extractOperationResult) throws SevenZipException {
            if (/*f*/skipExtraction/**/) {
                return;
            }
            if (extractOperationResult != ExtractOperationResult./*sf*/OK/**/) {
                System.err.println("Extraction error");
            } else {
                System.out.println(String.format("%9X | %10s | %s", /*f*/hash/**/, /*f*/size/**/,// 
                        /*f*/inArchive/**/.getProperty(/*f*/index/**/, PropID./*sf*/PATH/**/)));
                /*f*/hash/* */= 0;
                /*f*/size/* */= 0;
            }
        }

        public void setCompleted(long completeValue) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }
    }

    public static void main(String[] args) {
        if (args./*f*/length/* */== 0) {
            System.out.println("Usage: java ExtractItemsStandard <arch-name>");
            return;
        }
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(args[0], "r");
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));

            System.out.println("   Hash   |    Size    | Filename");
            System.out.println("----------+------------+---------");

            int[] in = new int[inArchive.getNumberOfItems()];
            for (int i = 0; i < in.length; i++) {
                in[i] = i;
            }
            inArchive.extract(in, false, // Non-test mode
                    new MyExtractCallback(inArchive));
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
/* END_SNIPPET */