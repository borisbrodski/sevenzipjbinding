package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutStream;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * <code>close()</code> racing with calls on other threads (issues #70, #62). Up to 26.06-2.3 <code>close()</code>
 * released the native session while another thread was still inside <code>nativeGetStringProperty()</code> or an
 * extraction, so the JVM crashed (SIGSEGV, double free) or the binding aborted it. Now every call holds a read lock
 * and <code>close()</code> the write lock: it waits for running calls, later calls throw "InArchive closed", and a
 * <code>close()</code> from a callback on the same thread throws instead of deadlocking on the lock upgrade.
 * <p>
 * The tests are deterministic: a running extraction or creation is held inside <code>write()</code> of its output
 * stream until the closing thread has been seen waiting on the lock; the order of completion is recorded, not timed.
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public class ConcurrentCloseTest extends JUnitNativeTestBase<VoidContext> {
    private static final int ITEM_COUNT = 200;
    private static final int READER_THREADS = 8;
    private static final int READS_BEFORE_CLOSE = 100;
    private static final long TIMEOUT_SECONDS = 60;

    /**
     * Output stream that blocks inside the first <code>write()</code> until {@link #release()}.
     */
    private static class BlockingOutStream implements IOutStream {
        private final ByteArrayStream target = new ByteArrayStream(Integer.MAX_VALUE);
        private final CountDownLatch inWrite = new CountDownLatch(1);
        private final CountDownLatch gate = new CountDownLatch(1);

        public int write(byte[] data) throws SevenZipException {
            inWrite.countDown();
            try {
                if (!gate.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    throw new SevenZipException("BlockingOutStream: not released");
                }
            } catch (InterruptedException e) {
                throw new SevenZipException("Interrupted", e);
            }
            return target.write(data);
        }

        public long seek(long offset, int seekOrigin) throws SevenZipException {
            return target.seek(offset, seekOrigin);
        }

        public void setSize(long newSize) throws SevenZipException {
            target.setSize(newSize);
        }

        void awaitWrite() throws InterruptedException {
            assertTrue("Operation didn't reach write()", inWrite.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        void release() {
            gate.countDown();
        }

        byte[] bytes() {
            return Arrays.copyOf(target.getBytes(), (int) target.getSize());
        }
    }

    private class CreateCallback implements IOutCreateCallback<IOutItemAllFormats> {
        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            IOutItemAllFormats item = outItemFactory.createOutItem();
            item.setPropertyPath(path(index));
            item.setDataSize((long) data(index).length);
            return item;
        }

        public ISequentialInStream getStream(int index) {
            return new ByteArrayStream(data(index), true);
        }

        public void setOperationResult(boolean operationResultOk) {
            operationFinished.set(sequence.incrementAndGet());
        }

        public void setTotal(long total) {
        }

        public void setCompleted(long complete) {
        }
    }

    /**
     * Order of events between the operation running inside the archive and <code>close()</code> on another thread.
     * {@link #operationFinished} is stamped from the last callback of the operation, that is inside the native call
     * while the archive is still locked; {@link #closeFinished} after <code>close()</code> returned. The second must
     * be the larger one.
     */
    private final AtomicInteger sequence = new AtomicInteger();
    private final AtomicInteger operationFinished = new AtomicInteger();
    private final AtomicInteger closeFinished = new AtomicInteger();
    private final List<Thread> threads = new ArrayList<Thread>();

    private static String path(int index) {
        return "item" + index + ".txt";
    }

    private static byte[] data(int index) {
        Random random = new Random(index);
        byte[] data = new byte[100 + index * 7];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) ('a' + random.nextInt(8));
        }
        return data;
    }

    private byte[] createArchive(ArchiveFormat format, int itemCount) throws Exception {
        ByteArrayStream outStream = new ByteArrayStream(Integer.MAX_VALUE);
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(format);
        try {
            outArchive.createArchive(outStream, itemCount, new CreateCallback());
        } finally {
            outArchive.close();
        }
        sequence.set(0); // The creation above stamped operationFinished as well
        operationFinished.set(0);
        return Arrays.copyOf(outStream.getBytes(), (int) outStream.getSize());
    }

    private static IInArchive open(byte[] archive) throws SevenZipException {
        return SevenZip.openInArchive(null, new ByteArrayStream(archive, false));
    }

    private Thread start(String name, final Runnable runnable, final AtomicReference<Throwable> failure) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    failure.compareAndSet(null, e);
                }
            }
        }, name);
        thread.setDaemon(true);
        thread.start();
        threads.add(thread);
        return thread;
    }

    private void joinAll() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
            assertFalse("Thread " + thread.getName() + " didn't finish", thread.isAlive());
        }
    }

    private static void assertNoFailure(AtomicReference<Throwable> failure) {
        Throwable throwable = failure.get();
        if (throwable != null) {
            throwable.printStackTrace();
            fail("Unexpected: " + throwable);
        }
    }

    private Thread startCloser(final java.io.Closeable archive, AtomicReference<Throwable> failure) {
        return start("close", new Runnable() {
            public void run() {
                try {
                    archive.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                closeFinished.set(sequence.incrementAndGet());
            }
        }, failure);
    }

    private void assertCloseFinishedLast(String operation) {
        assertTrue(operation + " must have finished", operationFinished.get() > 0);
        assertTrue("close() (" + closeFinished.get() + ") must finish after the " + operation + " ("
                + operationFinished.get() + ")", closeFinished.get() > operationFinished.get());
    }

    /**
     * Wait until the thread parks on the lock (or ends, if <code>close()</code> didn't wait).
     */
    private static void awaitWaiting(Thread thread) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS);
        while (System.nanoTime() < deadline) {
            Thread.State state = thread.getState();
            if (state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING || state == Thread.State.BLOCKED
                    || state == Thread.State.TERMINATED) {
                return;
            }
            Thread.sleep(2);
        }
        fail("Thread " + thread.getName() + " neither waits nor finished");
    }

    private static void assertClosedFromCallback(SevenZipException e, String archive) {
        Throwable cause = e;
        while (cause != null) {
            if (cause.getMessage() != null
                    && cause.getMessage().contains(archive + " can't be closed from within a callback")) {
                return;
            }
            cause = cause.getCause();
        }
        e.printStackTraceExtended();
        fail("Expected 'can't be closed from within a callback', got: " + e.getMessage());
    }

    /**
     * Reporter's scenario from issue #70: threads spinning on item properties while the archive gets closed. Every
     * thread must see successful reads, then only "InArchive closed", and the JVM must survive.
     * <p>
     * No timing involved: the readers loop until the archive is closed, and <code>close()</code> is called once every
     * reader has completed {@link #READS_BEFORE_CLOSE} rounds, that is while all of them are hammering the archive.
     */
    @Test
    public void closeWhileReadingProperties() throws Exception {
        final IInArchive inArchive = open(createArchive(ArchiveFormat.ZIP, ITEM_COUNT));
        final CountDownLatch halfway = new CountDownLatch(READER_THREADS);
        final AtomicInteger successfulReads = new AtomicInteger();
        final AtomicInteger closedReads = new AtomicInteger();
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        for (int t = 0; t < READER_THREADS; t++) {
            final int offset = t;
            start("reader-" + t, new Runnable() {
                public void run() {
                    for (int i = 0;; i++) {
                        int index = (offset + i) % ITEM_COUNT;
                        try {
                            assertEquals(path(index), inArchive.getStringProperty(index, PropID.PATH));
                            assertEquals(Long.valueOf(data(index).length), inArchive.getProperty(index, PropID.SIZE));
                        } catch (SevenZipException e) {
                            assertEquals("InArchive closed", e.getMessage());
                            closedReads.incrementAndGet();
                            return;
                        }
                        successfulReads.incrementAndGet();
                        if (i == READS_BEFORE_CLOSE - 1) {
                            halfway.countDown();
                        }
                    }
                }
            }, failure);
        }
        assertTrue("Readers didn't get halfway", halfway.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        inArchive.close();
        joinAll();
        assertNoFailure(failure);
        assertEquals("Every reader ends with 'InArchive closed'", READER_THREADS, closedReads.get());
        assertTrue(successfulReads.get() >= READER_THREADS * READS_BEFORE_CLOSE);
    }

    /**
     * <code>close()</code> on another thread waits for a running extraction; the extraction completes normally.
     */
    @Test
    public void closeWaitsForRunningExtraction() throws Exception {
        final IInArchive inArchive = open(createArchive(ArchiveFormat.ZIP, 3));
        final BlockingOutStream outStream = new BlockingOutStream();
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        start("extract", new Runnable() {
            public void run() {
                try {
                    inArchive.extract(new int[] { 1 }, false, new IArchiveExtractCallback() {
                        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
                            return outStream;
                        }

                        public void prepareOperation(ExtractAskMode extractAskMode) {
                        }

                        public void setOperationResult(ExtractOperationResult extractOperationResult) {
                            assertEquals(ExtractOperationResult.OK, extractOperationResult);
                            operationFinished.set(sequence.incrementAndGet());
                        }

                        public void setTotal(long total) {
                        }

                        public void setCompleted(long complete) {
                        }
                    });
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }
        }, failure);
        outStream.awaitWrite();
        Thread closer = startCloser(inArchive, failure);
        awaitWaiting(closer);
        assertEquals("Extraction must still be running while close() waits", 0, operationFinished.get());
        outStream.release();
        joinAll();
        assertNoFailure(failure);
        assertCloseFinishedLast("extraction");
        assertArrayEquals(data(1), outStream.bytes());
        try {
            inArchive.getNumberOfItems();
            fail("Archive must be closed");
        } catch (SevenZipException e) {
            assertEquals("InArchive closed", e.getMessage());
        }
    }

    /**
     * <code>close()</code> of the in-archive waits for a running update through its connected out-archive (both share
     * one native session).
     */
    @Test
    public void closeInArchiveWaitsForRunningConnectedUpdate() throws Exception {
        final IInArchive inArchive = open(createArchive(ArchiveFormat.ZIP, 3));
        final BlockingOutStream outStream = new BlockingOutStream();
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        start("update", new Runnable() {
            public void run() {
                try {
                    IOutUpdateArchive<IOutItemAllFormats> outArchive = inArchive.getConnectedOutArchive();
                    outArchive.updateItems(outStream, 3, new CreateCallback() { // New data for item 0
                        @Override
                        public IOutItemAllFormats getItemInformation(int index,
                                OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                            if (index != 0) {
                                return outItemFactory.createOutItem(index);
                            }
                            IOutItemAllFormats item = outItemFactory.createOutItemAndCloneProperties(index);
                            item.setUpdateIsNewData(true);
                            item.setUpdateIsNewProperties(true);
                            item.setDataSize((long) data(10).length);
                            return item;
                        }

                        @Override
                        public ISequentialInStream getStream(int index) {
                            return index == 0 ? new ByteArrayStream(data(10), true) : null;
                        }
                    });
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }
        }, failure);
        outStream.awaitWrite();
        Thread closer = startCloser(inArchive, failure);
        awaitWaiting(closer);
        outStream.release();
        joinAll();
        assertNoFailure(failure);
        assertCloseFinishedLast("update");
        IInArchive updated = open(outStream.bytes());
        try {
            assertEquals(3, updated.getNumberOfItems());
            assertEquals(Long.valueOf(data(10).length), updated.getProperty(0, PropID.SIZE));
        } finally {
            updated.close();
        }
    }

    /**
     * <code>close()</code> on another thread waits for a running archive creation.
     */
    @Test
    public void closeOutArchiveWaitsForRunningCreation() throws Exception {
        final IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(ArchiveFormat.ZIP);
        final BlockingOutStream outStream = new BlockingOutStream();
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        start("create", new Runnable() {
            public void run() {
                try {
                    outArchive.createArchive(outStream, 3, new CreateCallback());
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }
        }, failure);
        outStream.awaitWrite();
        Thread closer = startCloser(outArchive, failure);
        awaitWaiting(closer);
        outStream.release();
        joinAll();
        assertNoFailure(failure);
        assertCloseFinishedLast("creation");
        IInArchive created = open(outStream.bytes());
        try {
            assertEquals(3, created.getNumberOfItems());
            assertEquals(path(2), created.getStringProperty(2, PropID.PATH));
        } finally {
            created.close();
        }
    }

    /**
     * <code>close()</code> from an extraction callback on the same thread throws; the archive stays usable.
     */
    @Test
    public void closeInArchiveFromCallbackThrows() throws Exception {
        final IInArchive inArchive = open(createArchive(ArchiveFormat.ZIP, 3));
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        final AtomicReference<SevenZipException> thrown = new AtomicReference<SevenZipException>();
        start("extract", new Runnable() {
            public void run() {
                try {
                    inArchive.extract(new int[] { 0 }, false, new IArchiveExtractCallback() {
                        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode)
                                throws SevenZipException {
                            inArchive.close();
                            return new ByteArrayStream(Integer.MAX_VALUE);
                        }

                        public void prepareOperation(ExtractAskMode extractAskMode) {
                        }

                        public void setOperationResult(ExtractOperationResult extractOperationResult) {
                        }

                        public void setTotal(long total) {
                        }

                        public void setCompleted(long complete) {
                        }
                    });
                } catch (SevenZipException e) {
                    thrown.set(e);
                }
            }
        }, failure);
        joinAll(); // A self-deadlock would show up here
        assertNoFailure(failure);
        assertTrue("extract() must fail", thrown.get() != null);
        assertClosedFromCallback(thrown.get(), "InArchive");
        assertEquals("Archive still open", 3, inArchive.getNumberOfItems());
        inArchive.close();
        try {
            inArchive.getNumberOfItems();
            fail("Archive must be closed");
        } catch (SevenZipException e) {
            assertEquals("InArchive closed", e.getMessage());
        }
    }

    /**
     * <code>close()</code> from a creation callback on the same thread throws; the archive stays usable.
     */
    @Test
    public void closeOutArchiveFromCallbackThrows() throws Exception {
        final IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(ArchiveFormat.ZIP);
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        final AtomicReference<SevenZipException> thrown = new AtomicReference<SevenZipException>();
        start("create", new Runnable() {
            public void run() {
                try {
                    outArchive.createArchive(new ByteArrayStream(Integer.MAX_VALUE), 3, new CreateCallback() {
                        @Override
                        public ISequentialInStream getStream(int index) {
                            try {
                                outArchive.close();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return super.getStream(index);
                        }
                    });
                } catch (SevenZipException e) {
                    thrown.set(e);
                }
            }
        }, failure);
        joinAll(); // A self-deadlock would show up here
        assertNoFailure(failure);
        assertTrue("createArchive() must fail", thrown.get() != null);
        assertClosedFromCallback(thrown.get(), "OutArchive");
        outArchive.close(); // Still open: closes without "OutArchive closed"
        try {
            outArchive.createArchive(new ByteArrayStream(Integer.MAX_VALUE), 1, new CreateCallback());
            fail("Archive must be closed");
        } catch (SevenZipException e) {
            assertEquals("OutArchive closed", e.getMessage());
        }
    }
}
