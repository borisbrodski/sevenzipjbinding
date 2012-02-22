package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveUpdateCallback;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

public class OutArchiveImpl implements IOutArchive {

    private long jbindingSession;
    private long sevenZipArchiveInstance;

    /**
     * Archive format enum value.
     */
    private ArchiveFormat archiveFormat;

    /**
     * 7-zip CCoders-index of the archive format {@link #archiveFormat}
     */
    private int archiveFormatIndex;

    private int compressionLevel = -1;
    private int threadCount = -1;

    /**
     * {@inheritDoc}
     */
    public void updateItems(ISequentialOutStream outStream, int numberOfItems,
            IArchiveUpdateCallback archiveUpdateCallback) throws SevenZipException {
        applyFeatures();
        nativeUpdateItems(archiveFormatIndex, outStream, numberOfItems, archiveUpdateCallback);
    }

    protected void featureSetLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    protected void featureSetThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    protected void applyFeatures() throws SevenZipException {
        // Set compression level
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
            IArchiveUpdateCallback archiveUpdateCallback) throws SevenZipException;
}
