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
 * WHERE the intermediate {@code .tar} goes gives you three options:
 *   - {@link #extractInMemory}   - keep the inner archive in a {@link ByteArrayStream}. No temp file;
 *                                  simplest and fast, as long as the inner archive fits in memory.
 *   - {@link #extractViaTempFile} - stream the inner archive to a temporary file and reopen it from
 *                                  disk. Bounded memory, the safe default for a huge inner archive.
 *   - A third, advanced option avoids materialising the inner archive at all: decompress the GZip on
 *     one thread and let the {@code .tar} reader pull those bytes on demand through a small in-memory
 *     pipe (with a look-behind cache, because the tar reader seeks). It is genuinely tricky, so it is
 *     not shown here - see the test {@code ExtractNestedArchiveStreamingTest} for a working proof.
 *
 * Note the stream handling below: everything Closeable is managed with try-with-resources, so a
 * failing {@code close()} on the WRITE path (e.g. the last block cannot be flushed - disk full) is
 * NOT swallowed but propagates as it must; a swallowed close is silent data corruption.
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
                    throw new SevenZipException("Extraction failed: " + result);
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
    public static void extractInMemory(String wrappedArchive) throws SevenZipException, IOException {
        try (RandomAccessFile outerFile = new RandomAccessFile(wrappedArchive, "r");
                IInArchive outer = SevenZip.openInArchive(null, new RandomAccessFileInStream(outerFile));
                ByteArrayStream innerData = new ByteArrayStream(Integer.MAX_VALUE)) {

            // A GZip/BZip2/XZ stream holds exactly ONE item - the inner archive. Decompress it to memory.
            checkOk(outer.extractSlow(firstFileItem(outer), innerData));

            // Reopen that in-memory data as an archive and read the real files.
            innerData.rewind();
            try (IInArchive inner = SevenZip.openInArchive(null, (IInStream) innerData)) {
                listInnerContent(inner);
            }
        }
    }

    /** Variant 2: unwrap through a temporary file - bounded memory, good for a huge inner archive. */
    public static void extractViaTempFile(String wrappedArchive) throws SevenZipException, IOException {
        File tempTar = File.createTempFile("sevenzipjbinding-inner-", ".tmp");
        try {
            // One handle for both writing the inner archive and reading it back. try-with-resources
            // guarantees close() runs - and, crucially, its IOException is NOT swallowed: a failed
            // flush of the final block (e.g. disk full) must surface, or we'd read a truncated archive.
            try (RandomAccessFile innerFile = new RandomAccessFile(tempTar, "rw")) {
                try (RandomAccessFile outerFile = new RandomAccessFile(wrappedArchive, "r");
                        IInArchive outer = SevenZip.openInArchive(null,
                                new RandomAccessFileInStream(outerFile))) {
                    checkOk(outer.extractSlow(firstFileItem(outer),
                            new RandomAccessFileOutStream(innerFile)));
                }
                // Reopen the temp file (RandomAccessFileInStream seeks as needed) and read it.
                try (IInArchive inner = SevenZip.openInArchive(null,
                        new RandomAccessFileInStream(innerFile))) {
                    listInnerContent(inner);
                }
            }
        } finally {
            if (!tempTar.delete()) {
                tempTar.deleteOnExit();
            }
        }
    }

    private static void checkOk(ExtractOperationResult result) throws SevenZipException {
        if (result != ExtractOperationResult.OK) {
            throw new SevenZipException("Unwrapping the outer archive failed: " + result);
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

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ExtractNestedArchive <wrapped-archive, e.g. foo.tar.gz>");
            return;
        }
        System.out.println("== In memory ==");
        printHeader();
        try {
            extractInMemory(args[0]);
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        }

        System.out.println("== Via temporary file ==");
        printHeader();
        try {
            extractViaTempFile(args[0]);
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        }
    }

    private static void printHeader() {
        System.out.println("   Hash   |    Size    | Filename");
        System.out.println("----------+------------+---------");
    }
}
/* END_SNIPPET */
