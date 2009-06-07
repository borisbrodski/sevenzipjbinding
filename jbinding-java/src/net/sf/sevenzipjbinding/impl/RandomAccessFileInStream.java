package net.sf.sevenzipjbinding.impl;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.IInStream;

/**
 * Implementation of {@link IInStream} using {@link RandomAccessFile}.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public class RandomAccessFileInStream implements IInStream {
	private final RandomAccessFile randomAccessFile;
	private Throwable lastThrownException;

	/**
	 * Constructs instance of the class from random access file.
	 * 
	 * @param randomAccessFile
	 *            random access file to use
	 */
	public RandomAccessFileInStream(RandomAccessFile randomAccessFile) {
		this.randomAccessFile = randomAccessFile;
	}

	/**
	 * {@inheritDoc}
	 */
	public int seek(long offset, int seekOrigin, long[] newPositionOneElementArray) {
		// System.out.println("java-seek(" + offset + ", " + seekOrigin + ")");
		try {
			switch (seekOrigin) {
			case SEEK_SET:
				randomAccessFile.seek(offset);
				break;

			case SEEK_CUR:
				randomAccessFile.seek(randomAccessFile.getFilePointer() + offset);
				break;

			case SEEK_END:
				randomAccessFile.seek(randomAccessFile.length() + offset);
				break;

			default:
				throw new RuntimeException("Seek: unknown origin");
			}

			newPositionOneElementArray[0] = randomAccessFile.getFilePointer();
			// System.out.println(", CurrPos: " +
			// newPositionOneElementArray[0]);
		} catch (Throwable e) {
			lastThrownException = e;
			return 1;
		}

		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public int read(byte[] data, int[] processedSizeOneElementArray) {
		// System.out.print("Reading " + data.length + " bytes, ");
		try {
			int read = randomAccessFile.read(data);
			if (read == -1) {
				processedSizeOneElementArray[0] = 0;
			} else {
				processedSizeOneElementArray[0] = read;
			}
			// System.out.println("was read: " +
			// processedSizeOneElementArray[0]);
		} catch (Throwable e) {
			lastThrownException = e;
			return 1;
		}

		return 0;
	}

	/**
	 * Closes random access file. After this call no more methods should be called.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		randomAccessFile.close();
	}

	/**
	 * Return last thrown exception during read or seek operation.
	 * 
	 * @return last thrown exception or <code>null</code>, if no exceptions was thrown.
	 */
	public Throwable getLastThrownException() {
		return lastThrownException;
	}
}
