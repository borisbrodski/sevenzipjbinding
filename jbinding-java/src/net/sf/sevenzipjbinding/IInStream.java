package net.sf.sevenzipjbinding;

/**
 * InStream interface used to operate with seekable input stream
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public interface IInStream extends ISequentialInStream {
	public static final int SEEK_SET = 0;
	public static final int SEEK_CUR = 1;
	public static final int SEEK_END = 2;

	/**
	 * Move current location pointer to the new offset depending on
	 * <code>seekOrigin</code>
	 * 
	 * @param offset
	 *            absolute or relative offset in the stream to move to
	 * @param seekOrigin
	 *            on of three possible seek origins:<br>
	 *            {@link #SEEK_SET} - <code>offset</code> is an absolute
	 *            offset to move to,<br>
	 *            {@link #SEEK_CUR} - <code>offset</code> is a relative offset
	 *            to the current position in stream<br>, {@link #SEEK_END}
	 *            <code>offset</code> is an offset from the end of the stream (<code>offset < 0</code>).
	 * @param newPositionOneElementArray
	 *            (call by ref) one element array to return new absolute
	 *            position in the stream.
	 * @return 0 - success, else failure
	 */
	public int seek(long offset, int seekOrigin,
			long[] newPositionOneElementArray);
}
