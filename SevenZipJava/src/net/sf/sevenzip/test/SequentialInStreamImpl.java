package net.sf.sevenzip.test;

import java.io.IOException;
import java.io.InputStream;

import net.sf.sevenzip.SequentialInStream;

public class SequentialInStreamImpl implements SequentialInStream {

	private InputStream inputStream;
	
	public SequentialInStreamImpl(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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

}
