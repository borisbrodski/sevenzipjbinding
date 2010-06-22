package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveUpdateCallback;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipOutArchive;

public class OutArchiveImpl implements ISevenZipOutArchive {
    @SuppressWarnings("unused")
    private long jbindingSession;

    @SuppressWarnings("unused")
    private long sevenZipArchiveInstance;

    /**
     * Archive format enum value.
     */
    private ArchiveFormat archiveFormat;

    /**
     * 7-zip CCoders-index of the archive format {@link #archiveFormat}
     */
    private int archiveFormatIndex;

    private OutArchiveImpl() {
    }

    private native void updateItemsNative(int archiveFormatIndex, ISequentialOutStream outStream, int numberOfItems,
            IArchiveUpdateCallback archiveUpdateCallback);

    /**
     * {@inheritDoc}
     */
    public void updateItems(ISequentialOutStream outStream, int numberOfItems,
            IArchiveUpdateCallback archiveUpdateCallback) {
        updateItemsNative(archiveFormatIndex, outStream, numberOfItems, archiveUpdateCallback);
    }

    /**
     * {@inheritDoc}
     */
    public ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) {
        setLevelNative(compressionLevel);
    }

    private native void setLevelNative(int compressionLevel);

}
