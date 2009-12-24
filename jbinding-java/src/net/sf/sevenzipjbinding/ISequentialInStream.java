package net.sf.sevenzipjbinding;

/**
 * Interface used to operate with sequential input stream.
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public interface ISequentialInStream {
    /**
     * Reads at least 1 and maximum <code>data.length</code> from the in-stream. If <code>data.length == 0</code> 0
     * should be returned. If <code>data.length != 0</code>, then return value 0 indicates end-of-stream (EOF). This
     * means no more bytes can be read from the stream. <br>
     * This function is allowed to read less than number of remaining bytes in stream and less then
     * <code>data.length</code>. You must call <code>read()</code> function in loop, if you need exact amount of data.
     * 
     * @param data
     *            buffer to get read data
     * 
     * @return amount of bytes written in the <code>data</code> array. 0 - represents end of stream.
     * 
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There are no guarantee, that there are no further call back methods will be called. The
     *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
     *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
     */
    public int read(byte[] data) throws SevenZipException;
}
