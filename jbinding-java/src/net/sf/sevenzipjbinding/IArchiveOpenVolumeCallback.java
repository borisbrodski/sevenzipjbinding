package net.sf.sevenzipjbinding;

/**
 * Interface to provide information (properties and input streams) to open archive from volumes.
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public interface IArchiveOpenVolumeCallback {

    /**
     * Get property of the volume file. An implementation must support at least following PropIDs:
     * <ul>
     * <li>{@link PropID#NAME} (Type: String)</li>
     * <li>{@link PropID#IS_FOLDER} (Type: Boolean)</li>
     * <li>{@link PropID#SIZE} (Type: Long)</li>
     * <li>{@link PropID#ATTRIBUTES} (Type: int)</li>
     * <li>{@link PropID#LAST_ACCESS_TIME} (Type: Date)</li>
     * <li>{@link PropID#CREATION_TIME} (Type: Date)</li>
     * <li>{@link PropID#LAST_WRITE_TIME} (Type: Date)</li>
     * </ul>
     * 
     * @param propID
     *            property
     * @return property value
     * 
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There are no guarantee, that there are no further call back methods will be called. The
     *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
     *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
     */
    public Object getProperty(PropID propID) throws SevenZipException;

    /**
     * Return {@link IInStream} for volume with filename <code>filename</code>.
     * 
     * @param filename
     *            name of the next required volume
     * @return IInStream for required volume
     * 
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There are no guarantee, that there are no further call back methods will be called. The
     *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
     *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
     */
    public IInStream getStream(String filename) throws SevenZipException;
}
