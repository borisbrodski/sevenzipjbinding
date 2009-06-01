package net.sf.sevenzipjbinding;

/**
 * Interface to provide information (properties and input streams) to open
 * archive from volumes.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public interface IArchiveOpenVolumeCallback {

	/**
	 * Get property of the volume file. An implementation must support at least
	 * following PropIDs:
	 * <li> {@link PropID#NAME} (Type: String)
	 * <li> {@link PropID#IS_FOLDER} (Type: Boolean):
	 * <li> {@link PropID#SIZE} (Type: Long)
	 * <li> {@link PropID#ATTRIBUTES} (Type: int)
	 * <li> {@link PropID#LAST_ACCESS_TIME} (Type: Date)
	 * <li> {@link PropID#CREATION_TIME} (Type: Date)
	 * <li> {@link PropID#LAST_WRITE_TIME} (Type: Date) <br>
	 * 
	 * @param propID
	 *            property
	 * @return property value
	 */
	public Object getProperty(PropID propID);

	/**
	 * Return {@link IInStream} for volume with filename <code>filename</code>.
	 * 
	 * @param filename
	 *            name of the next required volume
	 * @return IInStream for required volume
	 */
	public IInStream getStream(String filename);
}
