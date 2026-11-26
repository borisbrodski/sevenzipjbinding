package net.sf.sevenzipjbinding.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemBZip2;
import net.sf.sevenzipjbinding.IOutItemXz;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.IOutItemGZip;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.IOutUpdateArchive7z;
import net.sf.sevenzipjbinding.IOutUpdateArchiveBZip2;
import net.sf.sevenzipjbinding.IOutUpdateArchiveXz;
import net.sf.sevenzipjbinding.IOutUpdateArchiveGZip;
import net.sf.sevenzipjbinding.IOutUpdateArchiveTar;
import net.sf.sevenzipjbinding.IOutUpdateArchiveZip;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.NFileTimeType;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.PropertyInfo;
import net.sf.sevenzipjbinding.ReportExtractResultIndexType;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.impl.SimpleInArchiveImpl;

/**
 * Implementation of {@link IInArchive}.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
//TODO null check all parameters: If null slips through into native code there will be no NPE :(
public final class InArchiveImpl implements IInArchive {
    private static class ExtractSlowCallback implements IArchiveExtractCallback {
        ISequentialOutStream sequentialOutStreamParam;
        private ExtractOperationResult extractOperationResult;

        ExtractSlowCallback(ISequentialOutStream sequentialOutStream) {
            this.sequentialOutStreamParam = sequentialOutStream;
        }

        /**
         * {@inheritDoc}
         */
        public void setTotal(long total) {
        }

        /**
         * {@inheritDoc}
         */
        public void setCompleted(long completeValue) {
        }

        /**
         * {@inheritDoc}
         */
        public void setOperationResult(ExtractOperationResult extractOperationResult) {
            this.extractOperationResult = extractOperationResult;
        }

        /**
         * {@inheritDoc}
         */
        public void prepareOperation(ExtractAskMode extractAskMode) {
        }

        /**
         * {@inheritDoc}
         */
        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
            return extractAskMode.equals(ExtractAskMode.EXTRACT) ? sequentialOutStreamParam : null;
        }

        ExtractOperationResult getExtractOperationResult() {
            return extractOperationResult;
        }

        public void reportExtractResult(ReportExtractResultIndexType indexType, int index,
                ExtractOperationResult extractOperationResult) throws SevenZipException {
        }
    }

    private static final class ExtractSlowCryptoCallback extends ExtractSlowCallback implements ICryptoGetTextPassword {

        private String password;

        public ExtractSlowCryptoCallback(ISequentialOutStream sequentialOutStream, String password) {
            super(sequentialOutStream);
            this.password = password;
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
        }
    }

    private long jbindingSession;
    private long sevenZipArchiveInstance;
    private long sevenZipInStreamInstance;
    private OutArchiveImpl<?> outArchiveImpl;

    private int numberOfItems = -1;

    private ArchiveFormat archiveFormat;

    /**
     * Serializes {@link #close()} against the native calls: every method calling into 7-Zip holds the read lock,
     * <code>close()</code> takes the write lock and so waits for calls running on other threads. Reentrant: callbacks
     * running on the calling thread may use the archive. Shared with the connected {@link OutArchiveImpl}.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean closed; // Guarded by lock

    /**
     * {@inheritDoc}
     */
    public void extract(int[] indices, boolean testMode, IArchiveExtractCallback extractCallback)
            throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            nativeExtract(indices, testMode, extractCallback);
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ExtractOperationResult extractSlow(int index, ISequentialOutStream outStream) throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            ExtractSlowCallback extractCallback = new ExtractSlowCallback(outStream);
            nativeExtract(new int[] { index }, false, extractCallback);
            return extractCallback.getExtractOperationResult();
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    /**
     * {@inheritDoc}
     */

    public ExtractOperationResult extractSlow(int index, ISequentialOutStream outStream, String password)
            throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            ExtractSlowCryptoCallback extractCallback = new ExtractSlowCryptoCallback(outStream, password);
            nativeExtract(new int[] { index }, false, extractCallback);
            return extractCallback.getExtractOperationResult();
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native void nativeExtract(int[] indices, boolean testMode, IArchiveExtractCallback extractCallback)
            throws SevenZipException;

    private native Object nativeGetArchiveProperty(int propID) throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public Object getArchiveProperty(PropID propID) throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            return nativeGetArchiveProperty(propID.getPropIDIndex());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native String nativeGetStringArchiveProperty(int propID) throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public String getStringArchiveProperty(PropID propID) throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            return nativeGetStringArchiveProperty(propID.getPropIDIndex());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native PropertyInfo nativeGetArchivePropertyInfo(int index) throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public PropertyInfo getArchivePropertyInfo(int index) throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            return nativeGetArchivePropertyInfo(index);
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native int nativeGetNumberOfArchiveProperties() throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public int getNumberOfArchiveProperties() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            return nativeGetNumberOfArchiveProperties();
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native int nativeGetNumberOfProperties() throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public int getNumberOfProperties() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            return nativeGetNumberOfProperties();
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native PropertyInfo nativeGetPropertyInfo(int index) throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public PropertyInfo getPropertyInfo(int index) throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            return nativeGetPropertyInfo(index);
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native void nativeClose() throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public void close() throws SevenZipException {
        if (lock.getReadHoldCount() > 0) {
            // A read-to-write upgrade would deadlock
            throw new SevenZipException("InArchive can't be closed from within a callback of this archive");
        }
        lock.writeLock().lock();
        Throwable thrown = null;
        try {
            if (closed) {
                return;
            }
            closed = true;
            nativeClose();
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            InArchiveImpl.unlock(lock.writeLock(), thrown);
        }
    }

    private native int nativeGetNumberOfItems() throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public int getNumberOfItems() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            if (numberOfItems == -1) {
                numberOfItems = nativeGetNumberOfItems();
            }
            return numberOfItems;
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native Object nativeGetProperty(int index, int propID) throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public Object getProperty(int index, PropID propID) throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            if (index < 0 || index >= getNumberOfItems()) {
                throw new SevenZipException(
                        "Index out of range. Index: " + index + ", NumberOfItems: " + getNumberOfItems());
            }
            // Correct some returned values
            Object returnValue = nativeGetProperty(index, propID.getPropIDIndex());
            switch (propID) {
            case SIZE:
            case PACKED_SIZE:
                // ARJ archive returns integer sizes (32 bit).
                // Correcting it here, since every other archive returns Long (64 bit).
                if (returnValue instanceof Integer) {
                    return Long.valueOf(((Integer) returnValue).longValue());
                }

                if (returnValue == null && archiveFormat != null && archiveFormat == ArchiveFormat.NSIS) {
                    return Long.valueOf(0);
                }
                break;
            case IS_FOLDER:
                // Some stream archive formats doesn't set this property.
                if (returnValue == null) {
                    return Boolean.FALSE;
                }
                break;
            case ENCRYPTED:
                // Some stream archive formats doesn't set this property either.
                if (returnValue == null) {
                    return Boolean.FALSE;
                }
                break;
            case TIME_TYPE:
                if (returnValue != null) {
                    return NFileTimeType.values()[((Integer) returnValue).intValue()];
                }
                break;
            }
            return returnValue;
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private native String nativeGetStringProperty(int index, int propID) throws SevenZipException;

    /**
     * {@inheritDoc}
     */
    public String getStringProperty(int index, PropID propID) throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            if (index < 0 || index >= getNumberOfItems()) {
                throw new SevenZipException(
                        "Index out of range. Index: " + index + ", NumberOfItems: " + getNumberOfItems());
            }
            return nativeGetStringProperty(index, propID.getPropIDIndex());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ISimpleInArchive getSimpleInterface() {
        return new SimpleInArchiveImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    public ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    /**
     * Set archive format of the opened archive. This method should be called only through JNI.
     *
     * @param archiveFormat
     *            format of the opened archive
     */
    private void setArchiveFormat(String archiveFormatString) {
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            if (archiveFormat.getMethodName().equalsIgnoreCase(archiveFormatString)) {
                this.archiveFormat = archiveFormat;
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public IOutUpdateArchive<IOutItemAllFormats> getConnectedOutArchive() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            return getConnectedOutArchiveIntern();
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IOutItemBase> IOutUpdateArchive<T> getConnectedOutArchiveIntern() throws SevenZipException {
        if (outArchiveImpl == null) {
            createConnectedOutArchive();
        }
        return (IOutUpdateArchive<T>) outArchiveImpl;
    }

    /**
     * {@inheritDoc}
     */
    public IOutUpdateArchive7z getConnectedOutArchive7z() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            ensureArchiveFormatForArchiveFormatSpecificUpdateAPI(ArchiveFormat.SEVEN_ZIP);
            return (IOutUpdateArchive7z) (this.<IOutItem7z> getConnectedOutArchiveIntern());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IOutUpdateArchiveZip getConnectedOutArchiveZip() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            ensureArchiveFormatForArchiveFormatSpecificUpdateAPI(ArchiveFormat.ZIP);
            return (IOutUpdateArchiveZip) (this.<IOutItemZip> getConnectedOutArchiveIntern());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IOutUpdateArchiveTar getConnectedOutArchiveTar() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            ensureArchiveFormatForArchiveFormatSpecificUpdateAPI(ArchiveFormat.TAR);
            return (IOutUpdateArchiveTar) (this.<IOutItemTar> getConnectedOutArchiveIntern());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IOutUpdateArchiveGZip getConnectedOutArchiveGZip() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            ensureArchiveFormatForArchiveFormatSpecificUpdateAPI(ArchiveFormat.GZIP);
            return (IOutUpdateArchiveGZip) (this.<IOutItemGZip> getConnectedOutArchiveIntern());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IOutUpdateArchiveBZip2 getConnectedOutArchiveBZip2() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            ensureArchiveFormatForArchiveFormatSpecificUpdateAPI(ArchiveFormat.BZIP2);
            return (IOutUpdateArchiveBZip2) (this.<IOutItemBZip2> getConnectedOutArchiveIntern());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IOutUpdateArchiveXz getConnectedOutArchiveXz() throws SevenZipException {
        lockForCall();
        Throwable thrown = null;
        try {
            ensureArchiveFormatForArchiveFormatSpecificUpdateAPI(ArchiveFormat.XZ);
            return (IOutUpdateArchiveXz) (this.<IOutItemXz> getConnectedOutArchiveIntern());
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            unlockAfterCall(thrown);
        }
    }

    private void ensureArchiveFormatForArchiveFormatSpecificUpdateAPI(ArchiveFormat archiveFormat)
            throws SevenZipException {
        if (getArchiveFormat() != archiveFormat) {
            throw new SevenZipException("Archive format specific update API for " + archiveFormat.getMethodName()
                    + "-archives can't work with the currently opened " + getArchiveFormat().getMethodName()
                    + "-archive");
        }
    }

    private void createConnectedOutArchive() throws SevenZipException {
        if (!archiveFormat.isOutArchiveSupported()) {
            throw new IllegalStateException(
                    "Archive format '" + archiveFormat + "' doesn't support archive manipulations.");
        }

        try {
            outArchiveImpl = archiveFormat.getOutArchiveImplementation().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Internal error: Can't create new instance of the class "
                    + archiveFormat.getOutArchiveImplementation() + " using default constructor.");
        }
        outArchiveImpl.setInArchive(this);
        outArchiveImpl.setArchiveFormat(archiveFormat);

        nativeConnectOutArchive(outArchiveImpl, archiveFormat);
    }

    /**
     * Take the read lock for a call into the native code and check that the archive is still open. On return the read
     * lock is held and {@link #unlockAfterCall()} must be called (<code>finally</code>).
     */
    void lockForCall() throws SevenZipException {
        lock.readLock().lock();
        if (closed) {
            lock.readLock().unlock();
            throw new SevenZipException("InArchive closed");
        }
    }

    /**
     * Release the read lock taken by {@link #lockForCall()}.
     *
     * @param thrown
     *            the exception leaving the native call, or <code>null</code> if it completed normally. An exception
     *            from the unlock itself must not replace it, see {@link #unlock(Lock, Throwable)}.
     */
    void unlockAfterCall(Throwable thrown) {
        unlock(lock.readLock(), thrown);
    }

    /**
     * Unlock in a <code>finally</code> block without masking the exception that is already on its way. If
     * <code>lock.unlock()</code> throws while <code>thrown</code> is in flight, the new exception is attached to
     * <code>thrown</code> as a suppressed exception (shown by <code>printStackTrace()</code>) instead of replacing it;
     * without an exception in flight it propagates.
     *
     * @param lock
     *            lock to release
     * @param thrown
     *            the exception leaving the guarded block, or <code>null</code>
     */
    static void unlock(Lock lock, Throwable thrown) {
        try {
            lock.unlock();
        } catch (RuntimeException e) {
            if (thrown == null) {
                throw e;
            }
            thrown.addSuppressed(e);
        }
    }

    ReentrantReadWriteLock getLock() {
        return lock;
    }

    private native void nativeConnectOutArchive(OutArchiveImpl<?> outArchiveImpl, ArchiveFormat archiveFormat)
            throws SevenZipException;
}
