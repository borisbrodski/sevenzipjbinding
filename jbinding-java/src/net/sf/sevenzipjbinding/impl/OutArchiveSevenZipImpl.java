package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutArchiveSevenZip;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * TODO
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 * 
 */
public class OutArchiveSevenZipImpl extends OutArchiveImpl implements IOutArchiveSevenZip {
    private boolean solid = true;
    private int countOfFilesPerBlock = -1;
    private long countOfBytesPerBlock = -1;
    private boolean solidExtension;

    public void setLevel(int compressionLevel) {
        featureSetLevel(compressionLevel);
    }

    /**
     * {@inheritDoc}
     */
    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    /**
     * {@inheritDoc}
     */
    public void setSolidFiles(int countOfFilesPerBlock) {
        this.countOfFilesPerBlock = countOfFilesPerBlock;
    }

    /**
     * {@inheritDoc}
     */
    public void setSolidSize(long countOfBytesPerBlock) {
        this.countOfBytesPerBlock = countOfBytesPerBlock;
    }

    /**
     * {@inheritDoc}
     */
    public void setSolidExtension(boolean solidExtension) {
        this.solidExtension = solidExtension;
    }

    @Override
    protected void applyFeatures() throws SevenZipException {
        super.applyFeatures();

        StringBuilder stringBuilder = new StringBuilder();
        if (solidExtension) {
            stringBuilder.append("E");
        }
        if (countOfFilesPerBlock != -1) {
            stringBuilder.append(countOfFilesPerBlock);
            stringBuilder.append("F");
        }
        if (countOfBytesPerBlock != -1) {
            stringBuilder.append(countOfBytesPerBlock);
            stringBuilder.append("B");
        }
        if (stringBuilder.length() > 0) {
            nativeSetSolidSpec(stringBuilder.toString());
        }

        // Set solid block configuration
        if (!solid) {
            nativeSetSolidSpec(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setThreadCount(int threadCount) {
        featureSetThreadCount(threadCount);
    }
}
