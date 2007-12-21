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
		System.out.println("read()");
		if (data.length == 0) {
			processedSizeOneElementArray[0] = 0;
			return 0;
		}
		
		try {
			processedSizeOneElementArray[0] = inputStream.read(data);
			return 0; // Ok
		} catch (IOException e) {
			e.printStackTrace();
			return 1; // Error
		}
	}

}
