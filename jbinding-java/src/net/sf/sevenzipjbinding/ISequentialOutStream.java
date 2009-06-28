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
	 * 
	 * @throws SevenZipException
	 *             in error case. If this method ends with an exception, the
	 *             current operation will be reported to 7-Zip as failed. There
	 *             are no guarantee, that there are no further call back methods
	 *             will be called. The first thrown exception will be saved and
	 *             thrown late on from the first called 7-Zip-JBinding main
	 *             method, such as <code>ISevenZipInArchive.extract()</code> or
	 *             <code>SevenZip.openInArchive()</code>.
	 */
	public int write(byte[] data) throws SevenZipException;
}
