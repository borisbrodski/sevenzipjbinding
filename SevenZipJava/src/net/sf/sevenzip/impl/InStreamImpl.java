package net.sf.sevenzip.impl;

import java.io.RandomAccessFile;

import net.sf.sevenzip.IInStream;
import net.sf.sevenzip.SevenZip;

public class InStreamImpl implements IInStream {
	static {
		SevenZip.initSevenZipNativeLibrary();
	}

	private final RandomAccessFile randomAccessFile;

	public InStreamImpl(RandomAccessFile randomAccessFile) {
		this.randomAccessFile = randomAccessFile;
	}

	public int seek(long offset, int seekOrigin,
			long[] newPositionOneElementArray) {
		// System.out.println("java-seek(" + offset + ", " + seekOrigin + ")");
		try {
			switch (seekOrigin) {
			case SEEK_SET:
				randomAccessFile.seek(offset);
				break;

			case SEEK_CUR:
				randomAccessFile.seek(randomAccessFile.getFilePointer()
						+ offset);
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
			e.printStackTrace();
			return 1;
		}

		return 0;
	}

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
			e.printStackTrace();
			return 1;
		}

		return 0;
	}
}
