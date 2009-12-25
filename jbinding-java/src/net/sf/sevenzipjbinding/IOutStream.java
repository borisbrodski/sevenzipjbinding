package net.sf.sevenzipjbinding;

/**
 * OutStream interface used to operate with seekable output streams
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutStream extends ISequentialOutStream, ISeekableStream {
    /**
     * Set new size for the out stream
     * 
     * @param newSize
     *            new size
     * 
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There are no guarantee, that there are no further call back methods will be called. The
     *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
     *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
     */
    public void setSize(long newSize) throws SevenZipException;
}
