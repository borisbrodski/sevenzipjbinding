package net.sf.sevenzipjbinding.junit.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.SevenZipException;

public class ZipInStream implements IInStream {

    private final ZipEntry zipEntry;
    private long size;
    private long absoluteShouldOffset;
    private long absoluteIsOffset;
    private InputStream inputStream;
    private final ZipFile zip;

    public ZipInStream(ZipFile zip, ZipEntry zipEntry) throws IOException {
        this.zip = zip;
        this.zipEntry = zipEntry;
        size = zipEntry.getSize();
        absoluteShouldOffset = 0L;
        absoluteIsOffset = 0L;
        inputStream = zip.getInputStream(zipEntry);
    }

    public synchronized long seek(long offset, int seekOrigin) throws SevenZipException {
        switch (seekOrigin) {
        case SEEK_SET:
            absoluteShouldOffset = offset;
            break;
        case SEEK_CUR:
            absoluteShouldOffset += offset;
            break;
        case SEEK_END:
            absoluteShouldOffset = size + offset;
            break;
        default:
            throw new RuntimeException("Seek: unknown origin: " + seekOrigin);
        }
        return absoluteShouldOffset;
    }

    public synchronized int read(byte[] data) throws SevenZipException {
        if (absoluteShouldOffset >= size) {
            return 0;
        }
        try {
            if (!seekToShouldPos()) {
                return 0;
            }
            int read;
            try {
                read = inputStream.read(data);
            } catch (IOException e) {
                throw new SevenZipException(e);
            }
            if (read < 1) {
                return 0;
            }
            absoluteIsOffset += read;
            absoluteShouldOffset += read;
            return read;
        } catch (Exception e) {
            throw new SevenZipException(e);
        }
    }

    private boolean seekToShouldPos() throws IOException {
        if (absoluteShouldOffset < absoluteIsOffset) {
            inputStream.close();
            inputStream = zip.getInputStream(zipEntry);
            absoluteIsOffset = 0;
        }
        if (absoluteShouldOffset > absoluteIsOffset) {
            byte[] buffer = new byte[1024 * 16];
            while (absoluteIsOffset < absoluteShouldOffset) {
                long toRead = absoluteShouldOffset - absoluteIsOffset;
                if (toRead > buffer.length) {
                    toRead = buffer.length;
                }
                int read = inputStream.read(buffer, 0, (int) toRead);
                if (read < 1) {
                    return false;
                }
                absoluteIsOffset += read;
            }
        }
        return true;
    }

    public synchronized void close() throws IOException {
        inputStream.close();
        zip.close();
    }
}
