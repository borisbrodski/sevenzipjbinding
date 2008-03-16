package net.sf.sevenzip;

public interface IArchiveOpenVolumeCallback {

	/**
	 * Get property of the volume file.
	 * An implementation must support at least following PropIDs:
	 * <li> {@link PropID#NAME} (Type: String)
	 * <li> {@link PropID#IS_FOLDER} (Type: Boolean):
	 * <li> {@link PropID#SIZE} (Type: Long)
	 * <li> {@link PropID#ATTRIBUTES} (Type: int)
	 * <li> {@link PropID#LAST_ACCESS_TIME} (Type: Date)
	 * <li> {@link PropID#CREATION_TIME} (Type: Date)
	 * <li> {@link PropID#LAST_WRITE_TIME} (Type: Date)
	 * <br>
	 * 
	 * @param propID
	 * @return
	 */
	public Object getProperty(PropID propID);

	/**
	 * Return {@link IInStream} für volume with filename <code>filename</code>.
	 * @param filename name of the next required volume
	 * @return IInStream für required volume
	 */
	public IInStream getStream(String filename);
}
