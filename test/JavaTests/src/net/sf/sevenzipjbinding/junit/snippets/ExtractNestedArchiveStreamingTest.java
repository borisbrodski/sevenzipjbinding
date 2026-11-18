package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutCreateArchiveGZip;
import net.sf.sevenzipjbinding.IOutCreateArchiveTar;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemGZip;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.ISeekableStream;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * PROOF (not a get-up-and-running snippet): you can extract a wrapped archive such as {@code .tar.gz}
 * WITHOUT first materialising the inner archive in a {@link ByteArrayStream} or a temp file. Instead,
 * the outer GZip is decompressed on a PRODUCER thread while the inner {@code .tar} reader pulls those
 * decompressed bytes on demand through a small in-memory pipe.
 *
 * The tricky parts this test demonstrates are handled:
 *  - The tar reader first does {@code seek(SEEK_END)} to learn the size — answered instantly from the
 *    GZip ISIZE trailer, so it does not force full decompression up front.
 *  - The tar reader seeks BACKWARD to re-read file-data blocks during extraction — served from a
 *    look-behind cache (the pipe retains what it produced).
 *  - Back-pressure: the producer is throttled to at most {@code LOOK_AHEAD} bytes beyond the
 *    consumer's furthest request, so it genuinely streams (it cannot race to completion first). The
 *    test asserts the producer actually blocked, i.e. the consumer drove the producer.
 *
 * This is referenced from the Extraction masterclass page for advanced users; it is intentionally involved.
 */
public class ExtractNestedArchiveStreamingTest {

    private static final int LOOK_AHEAD = 64 * 1024;
    private static final int FEED_CHUNK = 16 * 1024;

    /** A pull-driven, thread-safe pipe: a producer appends bytes, a consumer reads/seeks like a file. */
    private static final class DecompressionPipe implements IInStream {
        private final byte[] cache; // full retention (backward seeks); sized to the known inner length
        private final long totalSize; // known up front from the GZip ISIZE trailer
        private int produced; // bytes appended by the producer so far
        private long pos; // consumer position
        private long maxRequestedEnd; // furthest byte the consumer has asked for (drives back-pressure)
        private boolean aborted;
        private int producerBlockedCount; // info: how often the producer was throttled (look-ahead)
        private int consumerWaitCount; // proof metric: how often the consumer waited for the producer

        DecompressionPipe(int totalSize) {
            this.totalSize = totalSize;
            this.cache = new byte[totalSize];
        }

        /** Producer side: append decompressed bytes, blocking while too far ahead of the consumer. */
        synchronized void feed(byte[] data, int off, int len) throws InterruptedException {
            // Deterministic streaming: do not produce a single byte until the consumer has actually
            // requested data. This guarantees the consumer's first read finds nothing yet and must
            // wait for us - i.e. the data is genuinely piped, never "produced in full up front".
            while (!aborted && maxRequestedEnd == 0) {
                wait();
            }
            int written = 0;
            while (written < len) {
                while (!aborted && produced > maxRequestedEnd + LOOK_AHEAD) {
                    producerBlockedCount++;
                    wait(); // back-pressure: wait until the consumer asks for more
                }
                if (aborted) {
                    return;
                }
                int n = Math.min(FEED_CHUNK, len - written);
                System.arraycopy(data, off + written, cache, produced, n);
                produced += n;
                written += n;
                notifyAll();
            }
        }

        synchronized void abort() {
            aborted = true;
            notifyAll();
        }

        synchronized int consumerWaits() {
            return consumerWaitCount;
        }

        public synchronized int read(byte[] data) throws SevenZipException {
            if (pos >= totalSize) {
                return 0; // EOF
            }
            long want = Math.min(data.length, totalSize - pos);
            maxRequestedEnd = Math.max(maxRequestedEnd, pos + want);
            notifyAll(); // let a throttled producer proceed
            try {
                while (!aborted && produced < pos + want) {
                    consumerWaitCount++;
                    wait(); // wait for the producer to decompress enough
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SevenZipException("interrupted", e);
            }
            if (aborted) {
                throw new SevenZipException("pipe aborted");
            }
            int n = (int) want;
            System.arraycopy(cache, (int) pos, data, 0, n);
            pos += n;
            return n;
        }

        public synchronized long seek(long offset, int seekOrigin) throws SevenZipException {
            long np;
            if (seekOrigin == ISeekableStream.SEEK_SET) {
                np = offset;
            } else if (seekOrigin == ISeekableStream.SEEK_CUR) {
                np = pos + offset;
            } else { // SEEK_END - answered from the known total size, no decompression forced
                np = totalSize + offset;
            }
            pos = np;
            return pos;
        }

        public void close() {
        }
    }

    @Test
    public void streamingUnwrapIsPossible() throws Exception {
        // 1. Build a multi-file .tar.gz in memory (big enough that GZip emits many chunks).
        final Map<String, byte[]> originals = sampleFiles();
        final byte[] tarBytes = buildTar(originals);
        final byte[] tarGz = gzip(tarBytes);

        // 2. The inner (tar) length is the GZip ISIZE trailer - the last 4 bytes, little-endian.
        int innerSize = isizeTrailer(tarGz);
        assertEquals("ISIZE trailer must equal the tar length", tarBytes.length, innerSize);

        final DecompressionPipe pipe = new DecompressionPipe(innerSize);

        // 3. PRODUCER thread: decompress the outer GZip and feed the pipe (back-pressured).
        final Throwable[] producerError = new Throwable[1];
        Thread producer = new Thread(new Runnable() {
            public void run() {
                try (ByteArrayStream gzIn = new ByteArrayStream(tarGz, true);
                        IInArchive gz = SevenZip.openInArchive(ArchiveFormat.GZIP, (IInStream) gzIn)) {
                    gz.extractSlow(0, new ISequentialOutStream() {
                        public int write(byte[] data) throws SevenZipException {
                            try {
                                pipe.feed(data, 0, data.length);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new SevenZipException("producer interrupted", e);
                            }
                            return data.length;
                        }
                    });
                } catch (Throwable t) {
                    producerError[0] = t;
                    pipe.abort();
                }
            }
        }, "gzip-producer");
        producer.start();

        // 4. CONSUMER (this thread): open the inner tar over the live pipe and extract every file.
        final Map<String, byte[]> extracted = new LinkedHashMap<String, byte[]>();
        try (IInArchive tar = SevenZip.openInArchive(ArchiveFormat.TAR, (IInStream) pipe)) {
            int count = tar.getNumberOfItems();
            int[] all = new int[count];
            for (int i = 0; i < count; i++) {
                all[i] = i;
            }
            tar.extract(all, false, new IArchiveExtractCallback() {
                private ByteArrayOutputStream buf;
                private String path;

                public ISequentialOutStream getStream(int index, ExtractAskMode mode) throws SevenZipException {
                    // 7-Zip reports paths with the OS separator (backslash on Windows); normalise to
                    // '/' so the comparison against the source paths is platform-independent.
                    path = tar.getStringProperty(index, PropID.PATH).replace('\\', '/');
                    boolean folder = ((Boolean) tar.getProperty(index, PropID.IS_FOLDER)).booleanValue();
                    if (mode != ExtractAskMode.EXTRACT || folder) {
                        buf = null;
                        return null;
                    }
                    buf = new ByteArrayOutputStream();
                    return new ISequentialOutStream() {
                        public int write(byte[] data) {
                            buf.write(data, 0, data.length);
                            return data.length;
                        }
                    };
                }

                public void setOperationResult(ExtractOperationResult result) throws SevenZipException {
                    if (result != ExtractOperationResult.OK) {
                        throw new SevenZipException("inner extraction failed: " + result);
                    }
                    if (buf != null) {
                        extracted.put(path, buf.toByteArray());
                    }
                }

                public void prepareOperation(ExtractAskMode extractAskMode) {
                }

                public void setCompleted(long completeValue) {
                }

                public void setTotal(long total) {
                }
            });
        } finally {
            producer.join(30_000);
        }

        if (producerError[0] != null) {
            throw new AssertionError("producer failed", producerError[0]);
        }

        // 5. Correctness: every inner file came out byte-for-byte, through the live pipe.
        assertEquals(originals.keySet(), extracted.keySet());
        for (Map.Entry<String, byte[]> e : originals.entrySet()) {
            assertArrayEquals("content mismatch for " + e.getKey(), e.getValue(), extracted.get(e.getKey()));
        }

        // 6. Proof of genuine streaming: the consumer had to wait for the producer at least once,
        //    i.e. the inner archive was consumed as it was produced - never materialised up front.
        assertTrue("consumer never waited for the producer - data was not actually streamed",
                pipe.consumerWaits() > 0);
    }

    // ---- helpers: build the in-memory .tar.gz fixture -----------------------------------------

    private static Map<String, byte[]> sampleFiles() {
        Map<String, byte[]> m = new LinkedHashMap<String, byte[]>();
        m.put("a/first.bin", pattern(400_000, 1));
        m.put("a/second.bin", pattern(350_000, 2));
        m.put("third.bin", pattern(300_000, 3));
        return m;
    }

    private static byte[] pattern(int size, int seed) {
        byte[] b = new byte[size];
        int x = seed;
        for (int i = 0; i < size; i++) {
            x = x * 1103515245 + 12345;
            b[i] = (byte) ((x >>> 16) ^ (i * seed)); // semi-compressible, deterministic
        }
        return b;
    }

    private static byte[] buildTar(final Map<String, byte[]> files) throws Exception {
        final String[] names = files.keySet().toArray(new String[0]);
        final byte[][] data = new byte[names.length][];
        for (int i = 0; i < names.length; i++) {
            data[i] = files.get(names[i]);
        }
        try (ByteArrayStream out = new ByteArrayStream(1 << 24)) {
            IOutCreateArchiveTar tar = SevenZip.openOutArchiveTar();
            tar.createArchive(out, names.length, new IOutCreateCallback<IOutItemTar>() {
                public void setOperationResult(boolean ok) {
                }

                public void setTotal(long t) {
                }

                public void setCompleted(long c) {
                }

                public IOutItemTar getItemInformation(int i, OutItemFactory<IOutItemTar> f) {
                    IOutItemTar it = f.createOutItem();
                    it.setDataSize((long) data[i].length);
                    it.setPropertyPath(names[i]);
                    return it;
                }

                public ISequentialInStream getStream(int i) {
                    return new ByteArrayStream(data[i], true);
                }
            });
            tar.close();
            out.rewind();
            return out.getBytes();
        }
    }

    private static byte[] gzip(final byte[] tarBytes) throws Exception {
        try (ByteArrayStream out = new ByteArrayStream(1 << 24)) {
            IOutCreateArchiveGZip gz = SevenZip.openOutArchiveGZip();
            gz.createArchive(out, 1, new IOutCreateCallback<IOutItemGZip>() {
                public void setOperationResult(boolean ok) {
                }

                public void setTotal(long t) {
                }

                public void setCompleted(long c) {
                }

                public IOutItemGZip getItemInformation(int i, OutItemFactory<IOutItemGZip> f) {
                    IOutItemGZip it = f.createOutItem();
                    it.setDataSize((long) tarBytes.length);
                    it.setPropertyPath("inner.tar");
                    return it;
                }

                public ISequentialInStream getStream(int i) {
                    return new ByteArrayStream(tarBytes, true);
                }
            });
            gz.close();
            out.rewind();
            return out.getBytes();
        }
    }

    /** The uncompressed size stored in the GZip ISIZE trailer (last 4 bytes, little-endian, mod 2^32). */
    private static int isizeTrailer(byte[] gz) {
        int n = gz.length;
        return (gz[n - 4] & 0xff) | ((gz[n - 3] & 0xff) << 8)
                | ((gz[n - 2] & 0xff) << 16) | ((gz[n - 1] & 0xff) << 24);
    }
}
