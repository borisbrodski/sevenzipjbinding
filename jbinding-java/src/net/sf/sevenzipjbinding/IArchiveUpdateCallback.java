package net.sf.sevenzipjbinding;

/**
 * Interface to provide information needed to update an archive (create a new archive based on an existing one)
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IArchiveUpdateCallback extends IArchiveCreateCallback {
    /**
     * Determine, whether the archive item data has changed.
     * 
     * @param index
     *            index of the archive item in archive (starting from 0)
     * @return <code>true</code> - the archive item with index <code>index</code> has new data (always true for new
     *         archives). <code>false</code> - the archive item data from the old archive can be reused.
     */
    public boolean isNewData(int index) throws SevenZipException;

    /**
     * Determine, whether the archive item properties have changed.
     * 
     * @param index
     *            index of the archive item in archive (starting from 0)
     * @return <code>true</code> - the archive item with index <code>index</code> has new properties (always true for
     *         new archives). <code>false</code> - the archive item properties from the old archive can be reused.
     */
    public boolean isNewProperties(int index) throws SevenZipException;

    /**
     * Determine index of the archive item in the archive being updated.
     * 
     * @param index
     *            index of the archive item in the new archive (starting from 0)
     * @return corresponding index of the archive item in the old archive (starting from 0). <code>-1</code> if there is
     *         no corresponding archive item in the old archive or if doesn't matter.
     */
    public int getOldArchiveItemIndex(int index) throws SevenZipException;
}
