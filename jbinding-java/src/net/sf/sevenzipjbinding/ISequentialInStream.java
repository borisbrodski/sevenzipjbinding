package net.sf.sevenzipjbinding;

/**
 * Interface used to operate with sequential input stream.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public interface ISequentialInStream {
	/**
	 * Out: if <code>data.length</code> != 0, return_value = 0 and (<code>processedSizeOneElementArray</code> ==
	 * 0), then there are no more bytes in stream. <br>
	 * <br>
	 * if <code>(data.length > 0)</code> && there are bytes in stream, this
	 * function must read at least 1 byte. <br>
	 * <br>
	 * This function is allowed to read less than number of remaining bytes in
	 * stream. You must call Read function in loop, if you need exact amount of
	 * data
	 * 
	 * @param data
	 *            buffer to get read data
	 * @param processedSizeOneElementArray
	 *            (call by ref) one element array used to return amount of bytes
	 *            written in the <code>data</code> array. 0 - represents end
	 *            of stream.
	 * 
	 * @return 0 - success, else failure
	 */
	public int read(byte[] data, int[] processedSizeOneElementArray);
}
