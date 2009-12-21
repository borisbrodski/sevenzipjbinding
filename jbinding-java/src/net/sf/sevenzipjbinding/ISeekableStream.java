package net.sf.sevenzipjbinding;

/**
 * Interface for seekable streams.
 *  
 * @author boris
 *
 */
public interface ISeekableStream {
	/**
	 * Indicates, that the seek operation should be done from the beginning of the stream
	 */
	public static final int SEEK_SET = 0;

	/**
	 * Indicates, that the seek operation should be done from the current position (file pointer) of the stream.
	 * Positive values move file pointer forward.
	 */
	public static final int SEEK_CUR = 1;

	/**
	 * Indicates, that the seek operation should be done from the end of the stream. Positive offset values move file
	 * pointer over the end of the stream. For the read only streams it's equivalent reaching end of the stream while
	 * reading.
	 */
	public static final int SEEK_END = 2;

	/**
	 * Move current location pointer to the new offset depending on <code>seekOrigin</code>
	 * 
	 * @param offset
	 *            absolute or relative offset in the stream to move to
	 * @param seekOrigin
	 *            on of three possible seek origins:<br>
	 *            <li> {@link #SEEK_SET} - <code>offset</code> is an absolute offset to move to,<br><li>
	 *            {@link #SEEK_CUR} - <code>offset</code> is a relative offset to the current position in stream,<br>
	 *            <li> {@link #SEEK_END} - <code>offset</code> is an offset from the end of the stream
	 *            <code>(offset <= 0)</code>.
	 * 
	 * @return new absolute position in the stream.
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
	 *             as failed. There are no guarantee, that there are no further call back methods will be called. The
	 *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
	 */
	public long seek(long offset, int seekOrigin) throws SevenZipException;
}
