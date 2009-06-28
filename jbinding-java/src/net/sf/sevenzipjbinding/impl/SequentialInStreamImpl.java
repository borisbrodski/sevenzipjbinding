package net.sf.sevenzipjbinding.impl;

import java.io.IOException;
import java.io.InputStream;

import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZipException;

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
	public int read(byte[] data) throws SevenZipException {
		if (data.length == 0) {
			return 0;
		}

		try {
			int read = inputStream.read(data);
			if (read == -1) {
				return 0;
			} else {
				return read;
			}
		} catch (IOException e) {
			throw new SevenZipException("Error reading input stream", e);
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
