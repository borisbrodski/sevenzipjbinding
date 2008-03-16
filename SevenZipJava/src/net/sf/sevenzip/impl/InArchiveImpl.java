package net.sf.sevenzip.impl;

import net.sf.sevenzip.IArchiveExtractCallback;
import net.sf.sevenzip.IInArchive;
import net.sf.sevenzip.PropID;
import net.sf.sevenzip.PropertyInfo;
import net.sf.sevenzip.SevenZip;
import net.sf.sevenzip.SevenZipException;

public class InArchiveImpl implements IInArchive {
	static {
		SevenZip.initSevenZipNativeLibrary();
	}
	private int sevenZipArchiveInstance;

	public void extract(int[] indices, boolean testMode,
			IArchiveExtractCallback extractCallback) throws SevenZipException {

		nativeExtract(indices, testMode, extractCallback);
	}

	private native void nativeExtract(int[] indices, boolean testMode,
			IArchiveExtractCallback extractCallback) throws SevenZipException;

	private native Object nativeGetArchiveProperty(int propID)
			throws SevenZipException;

	public Object getArchiveProperty(PropID propID) throws SevenZipException {

		return nativeGetArchiveProperty(propID.getPropIDIndex());
	}

	private native PropertyInfo nativeGetArchivePropertyInfo(int index);

	public PropertyInfo getArchivePropertyInfo(int index)
			throws SevenZipException {
		return nativeGetArchivePropertyInfo(index);
	}

	private native int nativeGetNumberOfArchiveProperties()
			throws SevenZipException;

	public int getNumberOfArchiveProperties() throws SevenZipException {
		return nativeGetNumberOfArchiveProperties();
	}

	private native int nativeGetNumberOfProperties() throws SevenZipException;

	public int getNumberOfProperties() throws SevenZipException {
		return nativeGetNumberOfProperties();
	}

	private native PropertyInfo nativeGetPropertyInfo(int index)
			throws SevenZipException;

	public PropertyInfo getPropertyInfo(int index) throws SevenZipException {
		return nativeGetPropertyInfo(index);
	}

	private native void nativeClose() throws SevenZipException;

	public void close() throws SevenZipException {
		nativeClose();
	}

	private native int nativeGetNumberOfItems() throws SevenZipException;

	public int getNumberOfItems() throws SevenZipException {
		return nativeGetNumberOfItems();
	}

	private native Object nativeGetProperty(int index, int propID);

	public Object getProperty(int index, PropID propID)
			throws SevenZipException {
		return nativeGetProperty(index, propID.getPropIDIndex());
	}

	public void test() {
		System.out.println("Object: " + sevenZipArchiveInstance);

	}

}
