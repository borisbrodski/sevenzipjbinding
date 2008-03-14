package net.sf.sevenzip;

public interface ISequentialOutStream {
	/**
	 * if (size > 0) this function must write at least 1 byte. This function is
	 * allowed to write less than "size". You must call Write function in loop,
	 * if you need to write exact amount of data
	 * 
	 * @return count of written bytes
	 */
	public int write(byte[] data);
}
