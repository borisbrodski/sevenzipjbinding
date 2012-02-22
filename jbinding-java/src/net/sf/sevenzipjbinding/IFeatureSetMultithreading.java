package net.sf.sevenzipjbinding;

/**
 * 
 * Feature interface for setting solid. Use {@link SevenZip#openOutArchive(ArchiveFormat)} to get implementation of this
 * interface.
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 * 
 */
public interface IFeatureSetMultithreading {

    /**
     * Set number of threads to use.
     * 
     * @param threadCount
     *            number of threads to use,<br>
     *            <code>0</code> - match count of threads to the count of the available processors,<br>
     *            <code>threadCount < 0</code> - use default value
     */
    public void setThreadCount(int threadCount);
}
