package net.sf.sevenzipjbinding.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

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

    public void checkInvariant() {
        if (chunkList.size() == 0) {
            myassert(currentPosition == 0);
            myassert(currentPositionInChunk == 0);
            myassert(currentChunkIndex == -1);
            myassert(size == 0);
            return;
        }

        myassert(currentPositionInChunk >= 0);
        myassert(currentPosition >= 0);
        myassert(currentChunkIndex >= 0);
        myassert(size >= 0);

        int sizeBeforeLastChunk = 0;
        int sizeBeforeCurrentChunk = 0;
        for (int i = 0; i < chunkList.size() - 1; i++) {
            if (i < currentChunkIndex) {
                sizeBeforeCurrentChunk += chunkList.get(i).length;
            }
            if (i == currentChunkIndex) {
                if (currentChunkIndex < chunkList.size() - 1) {
                    myassert(currentPositionInChunk < chunkList.get(i).length);
                } else {
                    myassert(currentPositionInChunk <= chunkList.get(i).length);
                }
            }
            sizeBeforeLastChunk += chunkList.get(i).length;
        }
        myassert((sizeBeforeCurrentChunk + currentPositionInChunk) == currentPosition);
        myassert(sizeBeforeLastChunk <= size);
        myassert(sizeBeforeLastChunk + chunkList.get(chunkList.size() - 1).length >= size);
    }

    private void myassert(boolean b) {
        if (!b) {
            throw new IllegalStateException("Invariant broken.");
        }
    }

    public ByteArrayStream(int maxSize) {
        this(1024, maxSize);
    }

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
    public void setSize(long newSize) throws SevenZipException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public int write(byte[] data) {
        return write(data, 0, data.length);
    }

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
                    allocateNextChunk();
                }
            }
        } while (dataToWrite > 0);
        if (currentPosition > size) {
            size = currentPosition;
        }
        endWriting();
        return data.length;
    }

    public InputStream getInputStream() {
        return null; // TODO
    }

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

        // A call to this method on empty stream (for example after truncate()) will be handled separately for performace reasons.
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
                    currentPositionInChunk = 0;
                    currentChunkIndex++;
                    allocateNextChunk();
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

    public int getSize() {
        return size;
    }

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

    public void setBytes(byte[] bytes) {
        // TODO;
    }

    private void performDelayedSeek() {
        if (seekToPosition == -1) {
            return;
        }

        // TODO
    }

    private void startWriting() {
        if (currentChunkIndex == -1 || currentPositionInChunk >= chunkList.get(currentChunkIndex).length) {
            allocateNextChunk();
            currentPositionInChunk = 0;
            currentChunkIndex++;
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

    private void allocateNextChunk() {
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

        byte[] chunk = new byte[nextChunkSize];
        chunkList.add(chunk);
    }

}
