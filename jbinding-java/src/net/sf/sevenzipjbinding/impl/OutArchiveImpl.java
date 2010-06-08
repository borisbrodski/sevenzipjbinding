package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveUpdateCallback;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipOutArchive;

public class OutArchiveImpl implements ISevenZipOutArchive {
    /**
     * Archive format enum value.
     */
    private final ArchiveFormat archiveFormat;

    /**
     * 7-zip CCoders-index of the archive format {@link #archiveFormat}
     */
    private final int archiveFormatIndex;

    // TODO Make non public
    public OutArchiveImpl(ArchiveFormat archiveFormat, int archiveFormatIndex) {
        this.archiveFormat = archiveFormat;
        this.archiveFormatIndex = archiveFormatIndex;
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
        // TODO Auto-generated method stub

    }
}
