package net.sf.sevenzipjbinding.impl;

import java.io.IOException;
import java.io.InputStream;

import net.sf.sevenzipjbinding.ISequentialInStream;

/**
 * Implementation of {@link ISequentialInStream} based on {@link InputStream}.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public class SequentialInStreamImpl implements ISequentialInStream {
	private InputStream inputStream;

	/**
	 * Create instance of {@link SequentialInStreamImpl} based on {@link InputStream}
	 * 
	 * @param inputStream
	 *            input stream to use
	 */
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

	/**
	 * Get underlaying input stream
	 * 
	 * @return input stream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
}
