package net.sf.sevenzip.impl;

import java.io.IOException;
import java.io.InputStream;

import net.sf.sevenzip.ISequentialInStream;
import net.sf.sevenzip.SevenZip;

public class SequentialInStreamImpl implements ISequentialInStream {
	static {
		SevenZip.initSevenZipNativeLibrary();
	}

	private InputStream inputStream;

	public SequentialInStreamImpl(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * {@inheritDoc}
	 */
	public int read(byte[] data, int[] processedSizeOneElementArray) {
		if (data.length == 0) {
			processedSizeOneElementArray[0] = 0;
			return 0;
		}

		try {
			int read = inputStream.read(data);
			if (read == -1) {
				processedSizeOneElementArray[0] = 0;
			} else {
				processedSizeOneElementArray[0] = read;
			}
			return 0; // Ok
		} catch (IOException e) {
			e.printStackTrace();
			return 1; // Error
		}
	}

	public InputStream getInputStream() {
		return inputStream;
	}
}
