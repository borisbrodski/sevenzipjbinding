package net.sf.sevenzip;

public interface SequentialInStream {
	/**
	  * Out: if <code>data.length</code> != 0,
	  * return_value = 0 and
	  * (<code>processedSizeOneElementArray</code> == 0),
	  * then there are no more bytes in stream.
	  * <br><br>
	  * if (data.length > 0) && there are bytes in stream, 
	  * this function must read at least 1 byte.
	  * <br><br>
	  * This function is allowed to read less than number
	  * of remaining bytes in stream.
	  * You must call Read function in loop, if you need exact amount of data
	  */
	public int read(byte [] data, int [] processedSizeOneElementArray);
}
