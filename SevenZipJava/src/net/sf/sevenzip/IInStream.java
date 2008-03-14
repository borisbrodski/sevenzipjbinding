package net.sf.sevenzip;

public interface IInStream extends ISequentialInStream {
	public static final int SEEK_SET = 0;
	public static final int SEEK_CUR = 1;
	public static final int SEEK_END = 2;

	/**
	 * 
	 */
	public int seek(long offset, int seekOrigin, long [] newPositionOneElementArray);
}
