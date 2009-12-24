package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveUpdateCallback;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipOutArchive;

public class OutArchiveImpl implements ISevenZipOutArchive {
    private final ArchiveFormat archiveFormat;

    public OutArchiveImpl(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    private native void updateItemsNative(ISequentialOutStream outStream, int numberOfItems,
            IArchiveUpdateCallback archiveUpdateCallback);

    /**
     * {@inheritDoc}
     */
    public void updateItems(ISequentialOutStream outStream, int numberOfItems,
            IArchiveUpdateCallback archiveUpdateCallback) {
        updateItemsNative(outStream, numberOfItems, archiveUpdateCallback);
    }

}
