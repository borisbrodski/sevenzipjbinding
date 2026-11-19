package net.sf.sevenzipjbinding.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * Common archive create and update class.
 *
 * @param <T>
 *            the type of the item callback implementation
 *
 * @see OutArchive7zImpl
 * @see OutArchiveZipImpl
 * @see OutArchiveGZipImpl
 * @see OutArchiveBZip2Impl
 * @see OutArchiveTarImpl
 * @see OutArchiveXzImpl
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
// TODO null check all parameters: If null slips through into native code there will be no NPE :(
public class OutArchiveImpl<T extends IOutItemBase> implements IOutArchive<T> {

    private long jbindingSession;
    private long sevenZipArchiveInstance;

    /**
     * Instance of {@link IInArchive} for connected OutArchives.
     */
    private IInArchive inArchive;

    /**
     * Archive format enum value.
     */
    private ArchiveFormat archiveFormat;

    private Boolean headerEncryption;
    private int compressionLevel = -1;
    private int threadCount = -1;
    private long dictionarySize = -1;
    private boolean propertiesApplied;
    private PrintStream tracePrintStream;
    private boolean trace; // Read by native code

    /**
     * See {@link InArchiveImpl}: read lock around every native call, write lock in {@link #close()}. A connected
     * out-archive uses the lock of its in-archive, since it shares the native session with it.
     */
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean closed; // Guarded by lock

    protected void setInArchive(IInArchive inArchive) {
        this.inArchive = inArchive;
        lock = ((InArchiveImpl) inArchive).getLock();
    }

    protected void featureSetHeaderEncryption(boolean enabled) {
        this.headerEncryption = Boolean.valueOf(enabled);
    }

    protected void featureSetLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    protected void featureSetThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    protected void featureSetDictionarySize(long dictionarySize) {
        if (dictionarySize != -1 && dictionarySize <= 0) {
            throw new IllegalArgumentException("Dictionary size must be positive or -1 (default): " + dictionarySize);
        }
        this.dictionarySize = dictionarySize;
    }

    protected void applyFeatures() throws SevenZipException {
        applyFeatures(null, false);
    }

    /**
     * Apply all configured output properties in one native call. 7-Zip resets
     * its complete property set on every ISetProperties.setProperties() call,
     * so applying these settings separately makes each setting erase the ones
     * that preceded it.
     */
    protected final void applyFeatures(String solidBlockSpec, boolean solidBlockSpecDefined)
            throws SevenZipException {
        lockForCall();
        try {
            int headerEncryptionValue = -1;
            if (headerEncryption != null) {
                headerEncryptionValue = headerEncryption.booleanValue() ? 1 : 0;
            }
            String dictionarySizeSpec = null;
            // Level 0 means no compression (Copy). With a dictionary size 7-Zip would compress with LZMA2 instead.
            if (dictionarySize != -1 && compressionLevel != 0) {
                // The 'b' suffix makes 7-Zip read the value as bytes. A plain number would be read as a power of two.
                dictionarySizeSpec = dictionarySize + "b";
            }
            boolean anyPropertySet = compressionLevel != -1 || headerEncryptionValue != -1 || threadCount >= 0
                    || solidBlockSpecDefined || dictionarySizeSpec != null;
            // 7-Zip keeps the properties until the next call. If an earlier operation on this archive object sent some,
            // an empty property list is still needed to reset them to the defaults.
            if (anyPropertySet || propertiesApplied) {
                // A failing call may still have applied the properties before the rejected one
                propertiesApplied = true;
                nativeSetProperties(compressionLevel, headerEncryptionValue, threadCount, solidBlockSpec,
                        solidBlockSpecDefined, dictionarySizeSpec);
                propertiesApplied = anyPropertySet;
            }
        } finally {
            unlockAfterCall();
        }
    }

    /**
     * {@inheritDoc}
     */
    public ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    protected void setArchiveFormat(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    private native void nativeSetProperties(int compressionLevel, int headerEncryption, int threadCount,
            String solidBlockSpec, boolean solidBlockSpecDefined, String dictionarySizeSpec) throws SevenZipException;

    private native void nativeUpdateItems(ISequentialOutStream outStream, int numberOfItems,
            Object archiveUpdateCallback) throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        if (lock.getReadHoldCount() > 0) {
            // A read-to-write upgrade would deadlock
            throw new SevenZipException("OutArchive can't be closed from within a callback of this archive");
        }
        lock.writeLock().lock();
        try {
            if (closed) {
                return;
            }
            closed = true;
            if (inArchive != null) {
                return; // In case of connected OutArchive no explicit closing necessary.
            }
            nativeClose();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private native void nativeClose() throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public void updateItems(ISequentialOutStream outStream, int numberOfItems, IOutCreateCallback<T> outUpdateCallback)
            throws SevenZipException {
        lockForCall();
        try {
            doUpdateItems(outStream, numberOfItems, outUpdateCallback);
        } finally {
            unlockAfterCall();
        }
    }

    private void doUpdateItems(ISequentialOutStream outStream, int numberOfItems,
            IOutCreateCallback<?> archiveCreateCallback) throws SevenZipException {
        applyFeatures();
        nativeUpdateItems(outStream, numberOfItems, archiveCreateCallback);
    }

    /**
     * {@inheritDoc}
     */
    public void createArchive(ISequentialOutStream outStream, int numberOfItems,
            IOutCreateCallback<? extends T> outCreateCallback) throws SevenZipException {
        lockForCall();
        try {
            doUpdateItems(outStream, numberOfItems, outCreateCallback);
        } finally {
            unlockAfterCall();
        }
    }

    /**
     * {@inheritDoc}
     */
    public IInArchive getConnectedInArchive() {
        return inArchive;
    }

    // Called by native code
    private final void traceMessage(String message) {
        if (trace) {
            if (tracePrintStream == null) {
                System.out.println(message);
            } else {
                tracePrintStream.println(message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setTracePrintStream(PrintStream tracePrintStream) {
        this.tracePrintStream = tracePrintStream;
    }

    /**
     * {@inheritDoc}
     */
    public PrintStream getTracePrintStream() {
        return tracePrintStream;
    }

    /**
     * {@inheritDoc}
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTrace() {
        return trace;
    }

    /**
     * Take the read lock for a call into the native code and check that this archive and, for a connected out-archive,
     * the in-archive are still open. On return the read lock is held and {@link #unlockAfterCall()} must be called
     * (<code>finally</code>).
     */
    private void lockForCall() throws SevenZipException {
        if (inArchive != null) {
            ((InArchiveImpl) inArchive).lockForCall(); // Same lock
        } else {
            lock.readLock().lock();
        }
        if (closed) {
            lock.readLock().unlock();
            throw new SevenZipException("OutArchive closed");
        }
    }

    private void unlockAfterCall() {
        lock.readLock().unlock();
    }
}
