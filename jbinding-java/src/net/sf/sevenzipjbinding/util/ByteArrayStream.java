package net.sf.sevenzipjbinding.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutStream;
import net.sf.sevenzipjbinding.ISeekableStream;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * A byte array based implementation of
 * <ul>
 * <li>{@link ISequentialOutStream}</li>
 * <li>{@link IOutStream}</li>
 * <li>{@link ISequentialInStream}</li>
 * <li>{@link IInStream}</li>
 * <li>{@link ISeekableStream}</li>
 * </ul>
 * 
 * Provide read/write access to the content represented as a byte array. Provide bridge to {@link InputStream} and
 * {@link OutputStream} through various methods.
 * 
 * @author boris
 * 
 */
public class ByteArrayStream implements IInStream, IOutStream {

    private static final int INPUT_STREAM_READ_BUFFER_SIZE = 16 * 1024;

    private static final int MAX_CHUNK_SIZE = 1024 * 1024;

    private final List<byte[]> chunkList = new ArrayList<byte[]>();
    private final int initialSize;
    private final int maxSize;

    private int currentPosition;
    private int currentPositionInChunk;
    private int currentChunkIndex;
    private int size;
    private int seekToPosition;

    /**
     * Create new empty instance of ByteArrayStream with content <code>content</code> specifying maximal length of the
     * stored data.
     * 
     * @param content
     *            content to initialize byte array stream with. The current position will be set at the beginning of the
     *            stream.
     * 
     * @param copyContentArray
     *            <code>true</code> - copy <code>newContent</code> byte array, so the original array can be modified
     *            safely, without affecting the byte stream<br>
     *            <code>false</code> - don't copy byte array <code>newContent</code>. Any change to the byte array
     *            <code>newContent</code> will be reflected by the byte array stream.
     * 
     * @param maxSize
     *            maximal length of the stored data. Use {@link Integer.MAX} to disable maximal length constraint.
     */
    public ByteArrayStream(byte[] content, boolean copyContentArray, int maxSize) {
        this(1024, maxSize);
        setBytes(content, copyContentArray);
    }

    /**
     * Create new empty instance of ByteArrayStream with content <code>content</code> without specifying maximal length
     * of the stored data.<br>
     * <b>WARNING:</b> The maximal length of the byte array stream will be set to <code>content.length</code>. This
     * means, that no more data can be added to such byte array stream. However it's still possible to override or
     * truncate existing data.
     * 
     * @param content
     *            content to initialize byte array stream with. The current position will be set at the beginning of the
     *            stream.
     * 
     * @param copyContentArray
     *            <code>true</code> - copy <code>newContent</code> byte array, so the original array can be modified
     *            safely, without affecting the byte stream<br>
     *            <code>false</code> - don't copy byte array <code>newContent</code>. Any change to the byte array
     *            <code>newContent</code> will be reflected by the byte array stream.
     */
    public ByteArrayStream(byte[] content, boolean copyContentArray) {
        this(1024 > content.length ? content.length : 1024, content.length);
        setBytes(content, copyContentArray);
    }

    /**
     * Create new empty instance of ByteArrayStream specifying maximal length of the stored data.
     * 
     * @param maxSize
     *            maximal length of the stored data. Use {@link Integer.MAX} to disable maximal length constraint.
     */
    public ByteArrayStream(int maxSize) {
        this(1024, maxSize);
    }

    /**
     * Create new empty instance of ByteArrayStream specifing maximal length of the stored data.
     * 
     * @param maxSize
     *            maximal length of the stored data. Use {@link Integer.MAX} to disable maximal length constraint.
     * @param initialSize
     *            size of the first data chunk. The first data chunk (byte array) will be allocated after first writing
     *            request.
     */
    public ByteArrayStream(int initialSize, int maxSize) {
        this.initialSize = initialSize;
        if (maxSize <= 0) {
            throw new IllegalStateException("Maximal size of the byte array stream should be >0");
        }
        if (initialSize <= 0) {
            throw new IllegalStateException("Initial size of the byte array stream should be >0");
        }
        this.maxSize = maxSize;
        init();
    }

    private void init() {
        chunkList.clear();
        currentPosition = 0;
        currentPositionInChunk = 0;
        currentChunkIndex = -1;
        size = 0;
        seekToPosition = -1;
    }

    /**
     * {@inheritDoc}
     */
    public int read(byte[] data) throws SevenZipException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long seek(long offset, int seekOrigin) throws SevenZipException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(long newSize) {
        if (newSize == 0) {
            truncate();
            return;
        }

        if (newSize > maxSize) {
            throw new RuntimeException("Maximal size of the byte array stream was reached by setSize(" + newSize
                    + "). Maximal size is " + maxSize + " bytes");
        }

        if (size == 0) {
            chunkList.add(new byte[(int) newSize]);
            currentChunkIndex = 0;
            size = (int) newSize;
        }

        if (newSize > size) {
            int sizeToAdd = (int) (newSize - size);
            int entireSize = 0;
            for (int i = 0; i < chunkList.size(); i++) {
                entireSize += chunkList.get(i).length;
            }
            //int lastChunkLength = chunkList.get(chunkList.size() - 1).length;
            int lastChunkFreeSpace = entireSize - size;
            if (lastChunkFreeSpace < sizeToAdd) {
                size += lastChunkFreeSpace; // Needed for correct maximal stream size detection
                allocateNextChunk(sizeToAdd - lastChunkFreeSpace);
            }
            size = (int) newSize;
        }
        if (newSize < size) {
            int currentSize = 0;
            for (int i = 0; i < chunkList.size(); i++) {
                currentSize += chunkList.get(i).length;
                if (currentSize >= newSize) {
                    // Remove all chunks after i-th chunk
                    for (int j = chunkList.size() - 1; j > i; j--) {
                        chunkList.remove(j);
                    }
                    if (seekToPosition != -1 && seekToPosition > newSize) {
                        seekToPosition = (int) newSize;
                    } else if (currentPosition > newSize) {
                        currentPosition = (int) newSize;
                        currentPositionInChunk = currentPosition - currentSize + chunkList.get(i).length;
                        currentChunkIndex = i;
                    }
                    size = (int) newSize;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public int write(byte[] data) {
        return write(data, 0, data.length);
    }

    /**
     * Write <code>length</code> byte from the byte array <code>data</code> beginning from the position
     * <code>startPosition</code>.
     * 
     * @param data
     *            data to write
     * @param startPosition
     *            index of the first byte to write (beginning with 0)
     * @param length
     *            count of bytes to write
     * @return count of written bytes
     */
    public int write(byte[] data, int startPosition, int length) {
        if (startPosition < 0 || length < 0 || data.length < (startPosition + length)) {
            throw new IllegalStateException("Invalid start position (" + startPosition + ") and length (" + length
                    + ")");
        }
        if (length == 0) {
            return 0;
        }
        startWriting();

        int startPositionInData = startPosition;
        int dataToWrite = length;
        do {
            byte[] chunk = chunkList.get(currentChunkIndex);
            int freeSpaceInChunk = chunk.length - currentPositionInChunk;
            int copySize = freeSpaceInChunk < dataToWrite ? freeSpaceInChunk : dataToWrite;
            System.arraycopy(data, startPositionInData, chunk, currentPositionInChunk, copySize);
            currentPositionInChunk += copySize;
            currentPosition += copySize;
            startPositionInData += copySize;
            dataToWrite -= copySize;
            if (currentPositionInChunk >= chunk.length) {
                currentPositionInChunk = 0;
                currentChunkIndex++;
                if (currentChunkIndex >= chunkList.size()) {
                    allocateNextChunk(-1);
                }
            }
        } while (dataToWrite > 0);
        if (currentPosition > size) {
            size = currentPosition;
        }
        endWriting();
        return data.length;
    }

    /**
     * Get a deattached input stream associated with the entire byte stream content. Reading from this input stream
     * doesn't affect the current position of the byte array stream.<br>
     * <b>Warning:</b> The returned instance of the InputStream is still attached to the content of the byte array
     * stream. That means, that any change of the content will be immediately visible through InputStream.
     */
    public InputStream getDeattachedInputStream() {
        return null; // TODO
    }

    /**
     * Get an attached input stream associated with the byte stream content. Reading from returned InputStream is
     * equivalent to reading from the byte array itself. It means, that reading from InputStream started at the current
     * position of the byte array stream and moves it forward.
     */
    public InputStream getInputStream() {
        return null; // TODO
    }

    /**
     * Get an attached output stream associated with the byte stream content. Writing to returned OutputStream is
     * equivalent to writing to the byte array itself. It means, that writing to OutputStream affects the current
     * position of the byte array stream.
     */
    public OutputStream getOutputStream() {
        return null; // TODO
    }

    /**
     * Write entire content of the stream to the output stream.
     * 
     * @param outputStream
     *            output stream to write the entire content to
     * @param closeStreamAfterWriting
     *            <code>true</code> close output stream <code>outputStream</code> by calling
     *            {@link OutputStream#close()} method, <code>false</code> don't close output stream
     * @throws IOException
     *             if I/O exception occurs
     */
    public void writeToOutputStream(OutputStream outputStream, boolean closeStreamAfterWriting) throws IOException {
        try {
            int pos = 0;
            for (byte[] chunk : chunkList) {
                int copySize = pos + chunk.length > size ? size - pos : chunk.length;
                outputStream.write(chunk, 0, copySize);
                pos += copySize;
            }
        } finally {
            if (closeStreamAfterWriting) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public void writeFromInputStream(InputStream inputStream, boolean closeStreamAfterReading) throws IOException {
        performDelayedSeek();

        // A call to this method on empty stream (for example after truncate()) will be handled separately
        // for performance reasons.
        if (size == 0) {
            init();
            // First, try to read entire stream at once. This does work well with streams associated for example 
            // with regular files, but it doesn't work well with streams associated with network sockets.
            int available = inputStream.available();
            if (available > maxSize) {
                throw new RuntimeException("Maximal size of the byte array stream was reached. (Max size = " + maxSize
                        + ")");
            }
            if (available > initialSize) {
                byte[] chunk = new byte[available];
                int read = inputStream.read(chunk);
                if (read == -1) {
                    // End of stream. This shouldn't actually happen. 
                    return;
                }
                size = read;
                currentPosition = read;
                currentPositionInChunk = read;
                currentChunkIndex = 0;

                chunkList.add(chunk);
            }

            // Continue reading block after block
            startWriting();
            while (true) {
                byte[] lastChunk = chunkList.get(currentChunkIndex);
                int read = inputStream.read(lastChunk, currentPositionInChunk, lastChunk.length
                        - currentPositionInChunk);
                if (read == -1) {
                    break;
                }
                currentPosition += read;
                currentPositionInChunk += read;
                if (lastChunk.length - currentPositionInChunk == 0) {
                    allocateNextChunk(-1);
                }
            }
            size = currentPosition;
            endWriting();
        } else {
            byte[] buffer = new byte[INPUT_STREAM_READ_BUFFER_SIZE];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                write(buffer, 0, read);
            }
        }

        if (closeStreamAfterReading) {
            inputStream.close();
        }
    }

    /**
     * Clear all content of the stream
     */
    public void truncate() {
        init();
    }

    /**
     * Return the size of the byte array stream content in bytes.
     * 
     * @return the size of the byte array stream content in bytes.
     */
    public int getSize() {
        return size;
    }

    /**
     * Return current position in the byte array stream. The current position determines which part of the data will be
     * affected by next read or write operation. The current position can be changed explicitly by calling
     * {@link #seek(long, int)} method.
     * 
     * @return current position in the stream beginning with 0. if current position is equal to the size of the stream
     *         {@link #getSize()}, that means, that end of stream (EOF) was reached. All subsequent read operations will
     *         return EOF. All subsequent write operation will expand the stream until maximal size of stream will be
     *         reached. (See {@link #ByteArrayStream(int)}).
     */
    public int getCurrentPosition() {
        if (seekToPosition != -1) {
            return seekToPosition;
        }
        return currentPosition;
    }

    /**
     * Return the content of the byte array stream in a new byte array. The current content of the byte array stream
     * copied to the new byte array.
     * 
     * @return new array with the entire content of the byte array stream
     */
    public byte[] getBytes() {
        byte[] result = new byte[size];
        int pos = 0;
        for (byte[] chunk : chunkList) {
            int copySize = pos + chunk.length > size ? size - pos : chunk.length;
            System.arraycopy(chunk, 0, result, pos, copySize);
            pos += copySize;
        }
        return result;
    }

    /**
     * Reinitialize byte array stream, replace current content with the new content <code>newContent</code> and set the
     * current position to the beginning of the stream.
     * 
     * @param newContent
     *            new content of the byte array stream
     * @param copyNewContentArray
     *            <code>true</code> - copy <code>newContent</code> byte array, so the original array can be modified
     *            safely, without affecting the byte stream<br>
     *            <code>false</code> - don't copy byte array <code>newContent</code>. Any change to the byte array
     *            <code>newContent</code> will be reflected by the byte array stream.
     */
    public void setBytes(byte[] newContent, boolean copyNewContentArray) {
        init();
        byte[] content = newContent;
        int newContentLength = newContent.length;
        if (copyNewContentArray) {
            content = new byte[newContentLength];
            System.arraycopy(newContent, 0, content, 0, newContentLength);
        }
        chunkList.add(content);
        currentChunkIndex = 0;
        currentPosition = 0;
        currentPositionInChunk = 0;
        size = newContentLength;
    }

    private void performDelayedSeek() {
        if (seekToPosition == -1) {
            return;
        }

        // TODO
    }

    private void startWriting() {
        if (currentChunkIndex == -1 || currentPositionInChunk >= chunkList.get(currentChunkIndex).length) {
            allocateNextChunk(-1);
        }
    }

    private void endWriting() {
        if (size == currentPosition && currentPositionInChunk == 0) {
            if (currentChunkIndex == 0) {
                init();
            } else {
                chunkList.remove(currentChunkIndex--);
                currentPositionInChunk = chunkList.get(currentChunkIndex).length;
            }
        }
    }

    private void allocateNextChunk(int atLeastAmount) {
        if (currentChunkIndex == -1
                || (currentChunkIndex == chunkList.size() - 1 && chunkList.get(currentChunkIndex).length == currentPositionInChunk)) {
            currentPositionInChunk = 0;
            currentChunkIndex++;
        }
        if (size >= maxSize) {
            throw new RuntimeException("Maximal size of the byte array stream was reached. (Max size = " + maxSize
                    + ")");
        }

        int lastChunkIndex = chunkList.size() - 1;
        int nextChunkSize = lastChunkIndex == -1 ? initialSize : chunkList.get(lastChunkIndex).length << 1;
        if (nextChunkSize < 0 || nextChunkSize > MAX_CHUNK_SIZE) {
            nextChunkSize = MAX_CHUNK_SIZE;
        }
        if (size + nextChunkSize > maxSize) {
            nextChunkSize = maxSize - size;
        }
        if (atLeastAmount != -1 && nextChunkSize < atLeastAmount) {
            if ((size + atLeastAmount) >= maxSize) {
                throw new RuntimeException("Maximal size of the byte array stream was reached. (Max size = " + maxSize
                        + ")");
            }
            nextChunkSize = atLeastAmount;
        }
        byte[] chunk = new byte[nextChunkSize];
        chunkList.add(chunk);
    }
}
