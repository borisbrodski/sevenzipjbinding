package net.sf.sevenzip.test;

import net.sf.sevenzip.ISequentialOutStream;

public class TestOutputStream implements ISequentialOutStream {

	@Override
	public int write(byte[] data) {
		System.out.println("WRITE: '" + new String(data) + "'");
		return data.length;
	}

}
