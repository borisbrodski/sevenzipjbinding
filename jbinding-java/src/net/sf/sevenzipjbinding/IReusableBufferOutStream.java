package net.sf.sevenzipjbinding;

/**
 * Output stream that takes its data from a buffer owned and reused by 7-Zip-JBinding. A plain
 * {@link ISequentialOutStream} gets a fresh <code>byte[]</code> for every {@link ISequentialOutStream#write(byte[])}
 * call, so the array may be kept. That costs one allocation per chunk (128 KiB and more) during an extraction or
 * compression. An implementation of this interface receives the same array again and again, with the valid bytes in
 * <code>buffer[offset .. offset + length - 1]</code>, and must copy or process them before returning: the next call
 * overwrites the buffer. Nothing else changes compared to {@link ISequentialOutStream}: partial writes, threading and
 * exception handling work the same.
 * <p>
 * Pass an implementation wherever an {@link ISequentialOutStream} is expected (extraction callbacks,
 * {@link IOutCreateArchive#createArchive(ISequentialOutStream, int, IOutCreateCallback)}, ...). 7-Zip-JBinding
 * detects the interface and switches to the buffer reuse for that stream.
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public interface IReusableBufferOutStream extends ISequentialOutStream {
    /**
     * Write <code>length</code> bytes starting at <code>buffer[offset]</code>. If <code>length &gt; 0</code> this
     * method must write at least one byte and may write fewer than <code>length</code> bytes; 7-Zip calls it again
     * for the rest. The buffer belongs to 7-Zip-JBinding and is overwritten by the next call.
     *
     * @param buffer
     *            buffer with the data, reused for the next call
     * @param offset
     *            index of the first byte to write
     * @param length
     *            count of bytes to write
     * @return count of written bytes
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. See {@link ISequentialOutStream#write(byte[])}.
     */
    public int write(byte[] buffer, int offset, int length) throws SevenZipException;

    /**
     * Writes the whole array through {@link #write(byte[], int, int)}. Not called by 7-Zip-JBinding for this
     * interface.
     */
    public default int write(byte[] data) throws SevenZipException {
        return write(data, 0, data.length);
    }
}
