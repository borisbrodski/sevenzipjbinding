package net.sf.sevenzipjbinding.impl;

import java.io.IOException;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallbackBase;
import net.sf.sevenzipjbinding.IOutUpdateCallback;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

public class OutArchiveImpl<E extends IOutItemCallbackBase> implements IOutArchive<E> {

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

    private int compressionLevel = -1;
    private int threadCount = -1;

    protected void setInArchive(IInArchive inArchive) {
        this.inArchive = inArchive;
    }

    protected void featureSetLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    protected void featureSetThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    protected void applyFeatures() throws SevenZipException {
        if (compressionLevel != -1) {
            nativeSetLevel(compressionLevel);
        }

        if (threadCount >= 0) {
            nativeSetMultithreading(threadCount);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    // TODO reduce visibility
    public void setArchiveFormat(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    protected native void nativeSetLevel(int compressionLevel) throws SevenZipException;

    /**
     * Set solid features.
     * 
     * @param solidBlockSpec
     *            <code>null</code> - turn solid off
     * @throws SevenZipException
     *             if fails
     */
    protected native void nativeSetSolidSpec(String solidBlockSpec) throws SevenZipException;

    protected native void nativeSetMultithreading(int threadCount) throws SevenZipException;

    private native void nativeUpdateItems(int archiveFormatIndex, ISequentialOutStream outStream, int numberOfItems,
            Object archiveUpdateCallback, boolean isInArchiveAttached) throws SevenZipException;

    public void close() throws IOException {
        if (inArchive != null) {
            return; // In case of connected OutArchive no explicit closing necessary. 
        }
        nativeClose();
    }

    private native void nativeClose() throws SevenZipException;

    public void updateItems(ISequentialOutStream outStream, int numberOfItems,
            IOutUpdateCallback<E> archiveUpdateCallback) throws SevenZipException {
        doUpdateItems(outStream, numberOfItems, archiveUpdateCallback);
    }

    private void doUpdateItems(ISequentialOutStream outStream, int numberOfItems, Object archiveCreateCallback)
            throws SevenZipException {
        applyFeatures();
        nativeUpdateItems(archiveFormat.getCodecIndex(), outStream, numberOfItems, archiveCreateCallback,
                inArchive != null);
    }

    /**
     * {@inheritDoc}
     */
    public void createArchive(ISequentialOutStream outStream, int numberOfItems,
            IOutCreateCallback<? extends E> outCreateCallback) throws SevenZipException {
        doUpdateItems(outStream, numberOfItems, outCreateCallback);
    }
}
