package net.sf.sevenzipjbinding.junit.snippets;

/* BEGIN_SNIPPET(ExtractNestedArchive) */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Extract a WRAPPED archive - one archive nested inside another, like {@code .tar.gz} - in one go.
 *
 * A {@code .tar.gz} is a GZip stream that contains exactly one item: a {@code .tar} archive, which
 * in turn contains the real files. So "extracting" it is two open/extract steps:
 *   1. open the OUTER archive (GZip) and pull out its single item (the inner {@code .tar});
 *   2. open that inner {@code .tar} and read the actual files.
 *
 * The only question is WHERE the intermediate {@code .tar} goes. Two options, both shown here:
 *   - {@link #extractInMemory}  - keep the inner archive in a {@link ByteArrayStream}: no temp file,
 *                                 fastest, but the whole inner archive must fit in memory;
 *   - {@link #extractViaTempFile} - stream the inner archive to a temporary file and reopen it from
 *                                 disk: constant memory, good for huge inner archives.
 *
 * The same idea works for any nesting (e.g. {@code .cpio.gz}, a {@code .zip} inside a {@code .7z}, ...).
 */
public class ExtractNestedArchive {

    /** Read EVERY file of an already-opened archive in a single pass and print a verifiable table. */
    private static void listInnerContent(IInArchive innerArchive) throws SevenZipException {
        int count = innerArchive.getNumberOfItems();
        int[] items = new int[count]; // NB: for a real unwrap you'd usually skip folders
        for (int i = 0; i < count; i++) {
            items[i] = i;
        }
        innerArchive.extract(items, false, new IArchiveExtractCallback() {
            private int hash;
            private long size;
            private int index;

            public ISequentialOutStream getStream(int i, ExtractAskMode mode) {
                index = i;
                hash = 0;
                size = 0;
                if (mode != ExtractAskMode.EXTRACT) {
                    return null;
                }
                return new ISequentialOutStream() {
                    public int write(byte[] data) {
                        hash ^= Arrays.hashCode(data); // just to make the example verifiable
                        size += data.length;
                        return data.length; // return the number of bytes consumed
                    }
                };
            }

            public void setOperationResult(ExtractOperationResult result) throws SevenZipException {
                if (result != ExtractOperationResult.OK) {
                    System.err.println("Extraction error: " + result);
                    return;
                }
                boolean folder = ((Boolean) innerArchive.getProperty(index, PropID.IS_FOLDER)).booleanValue();
                if (!folder) {
                    System.out.println(String.format("%9X | %10s | %s", hash, size,
                            innerArchive.getStringProperty(index, PropID.PATH)));
                }
            }

            public void prepareOperation(ExtractAskMode extractAskMode) {
            }

            public void setCompleted(long completeValue) {
            }

            public void setTotal(long total) {
            }
        });
    }

    /** Variant 1: unwrap fully in memory - the inner archive lives in a ByteArrayStream, no temp file. */
    public static void extractInMemory(String wrappedArchive) {
        RandomAccessFile outerFile = null;
        IInArchive outer = null;
        IInArchive inner = null;
        ByteArrayStream innerArchiveInMemory = new ByteArrayStream(Integer.MAX_VALUE);
        try {
            outerFile = new RandomAccessFile(wrappedArchive, "r");
            outer = SevenZip.openInArchive(null, new RandomAccessFileInStream(outerFile));

            // A GZip/BZip2/XZ stream holds exactly ONE item - the inner archive. Extract it into memory.
            outer.extractSlow(firstFileItem(outer), innerArchiveInMemory);

            // Reopen that in-memory data as an archive and read the real files.
            innerArchiveInMemory.rewind();
            inner = SevenZip.openInArchive(null, (IInStream) innerArchiveInMemory);
            listInnerContent(inner);
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            closeQuietly(inner);
            closeQuietly(outer);
            closeQuietly(outerFile);
        }
    }

    /** Variant 2: unwrap through a temporary file - constant memory, good for a huge inner archive. */
    public static void extractViaTempFile(String wrappedArchive) {
        RandomAccessFile outerFile = null;
        RandomAccessFile innerFile = null;
        IInArchive outer = null;
        IInArchive inner = null;
        File tempTar = null;
        try {
            outerFile = new RandomAccessFile(wrappedArchive, "r");
            outer = SevenZip.openInArchive(null, new RandomAccessFileInStream(outerFile));

            // Stream the single inner item to a temp file instead of holding it in memory.
            tempTar = File.createTempFile("sevenzipjbinding-inner-", ".tmp");
            innerFile = new RandomAccessFile(tempTar, "rw");
            outer.extractSlow(firstFileItem(outer), new RandomAccessFileOutStream(innerFile));

            // Reopen the temp file as an archive (RandomAccessFileInStream seeks as needed) and read it.
            inner = SevenZip.openInArchive(null, new RandomAccessFileInStream(innerFile));
            listInnerContent(inner);
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            closeQuietly(inner);
            closeQuietly(outer);
            closeQuietly(innerFile);
            closeQuietly(outerFile);
            if (tempTar != null && !tempTar.delete()) {
                tempTar.deleteOnExit();
            }
        }
    }

    /** The index of the first non-folder item - the inner archive in a single-stream wrapper. */
    private static int firstFileItem(IInArchive archive) throws SevenZipException {
        int count = archive.getNumberOfItems();
        for (int i = 0; i < count; i++) {
            if (!((Boolean) archive.getProperty(i, PropID.IS_FOLDER)).booleanValue()) {
                return i;
            }
        }
        throw new SevenZipException("Wrapper archive contains no file to unwrap");
    }

    private static void closeQuietly(IInArchive archive) {
        if (archive != null) {
            try {
                archive.close();
            } catch (SevenZipException e) {
                System.err.println("Error closing archive: " + e);
            }
        }
    }

    private static void closeQuietly(RandomAccessFile file) {
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
                System.err.println("Error closing file: " + e);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ExtractNestedArchive <wrapped-archive, e.g. foo.tar.gz>");
            return;
        }
        System.out.println("== In memory ==");
        System.out.println("   Hash   |    Size    | Filename");
        System.out.println("----------+------------+---------");
        extractInMemory(args[0]);

        System.out.println("== Via temporary file ==");
        System.out.println("   Hash   |    Size    | Filename");
        System.out.println("----------+------------+---------");
        extractViaTempFile(args[0]);
    }
}
/* END_SNIPPET */
