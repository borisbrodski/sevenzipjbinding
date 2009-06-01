package net.sf.sevenzipjbinding;

/**
 * Interface used to operate with sequential output stream.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public interface ISequentialOutStream {
	/**
	 * if (size > 0) this function must write at least 1 byte. This function is
	 * allowed to write less than "size". You must call Write function in loop,
	 * if you need to write exact amount of data
	 * 
	 * @param data
	 *            data to write
	 * @return count of written bytes
	 */
	public int write(byte[] data);
}
