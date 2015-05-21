package net.sf.sevenzipjbinding;

/**
 * 
 * Feature interface for setting maximal number of threads to use during compression. Use
 * {@link SevenZip#openOutArchive(ArchiveFormat)} to get implementation of this interface.
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 * 
 */
public interface IOutFeatureSetMultithreading {

    /**
     * Set number of threads to use.
     * 
     * @param threadCount
     *            number of threads to use,<br>
     *            <code>0</code> - match count of threads to the count of the available processors,<br>
     *            <code>threadCount < 0</code> - use default value
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setThreadCount(int threadCount) throws SevenZipException;
}
