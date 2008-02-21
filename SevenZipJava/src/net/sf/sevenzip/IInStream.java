package net.sf.sevenzip;

public interface IInStream extends SequentialInStream {
	/**
	 * 
	 */
	public int seek(long offset, int seekOrigin, long [] newPositionOneElementArray);
}
