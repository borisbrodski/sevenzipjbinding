package net.sf.sevenzipjbinding.junit.tools;

import java.io.IOException;
import java.util.Random;

import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * Generates random content with different entropy.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class RandomContext implements IInStream {

    private long currentPosition;
    private final long size;
    private long step;

    public RandomContext(long size, long entropy) {
        this.size = size;
        currentPosition = 0;
        if (entropy > 0) {
            step = size / entropy;
        } else {
            step = 0;
        }
    }

    /**
     * ${@inheritDoc}
     */
    public synchronized int read(byte[] data) throws SevenZipException {
        byte[] buffer = new byte[1];
        for (int i = 0; i < data.length; i++, currentPosition++) {
            if (currentPosition >= size) {
                return i; // EOF
            }
            long section = step == 0 ? 1 : currentPosition / step;
            if (section % 2 == 0) {
                new Random(currentPosition).nextBytes(buffer);
            } else {
                Random sectionRandom = new Random(section * 1000 + size);
                switch (sectionRandom.nextInt(3)) {
                case 0:
                    sectionRandom.nextBytes(buffer);
                    break;
                case 1:
                    sectionRandom.nextBytes(buffer);
                    buffer[0] = (byte) (buffer[0] + currentPosition);
                    break;
                case 2:
                    sectionRandom.nextBytes(buffer);
                    buffer[0] = (byte) (buffer[0] - currentPosition * 7);
                    break;
                default:
                    throw new IllegalStateException("Invalid switch value");
                }
            }
            data[i] = buffer[0];
        }

        return data.length;
    }

    /**
     * ${@inheritDoc}
     */
    public synchronized long seek(long offset, int seekOrigin) throws SevenZipException {
        switch (seekOrigin) {
        case SEEK_SET:
            currentPosition = offset;
            break;

        case SEEK_CUR:
            currentPosition = currentPosition + offset;
            break;

        case SEEK_END:
            currentPosition = size + offset;
            break;

        default:
            throw new SevenZipException("Seek: unknown origin: " + seekOrigin);
        }
        return currentPosition;
    }

    public long getSize() {
        return size;
    }

    public void rewind() {
        currentPosition = 0;
    }

    public void close() throws IOException {
    }
}