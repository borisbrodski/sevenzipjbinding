package net.sf.sevenzip.impl;

import net.sf.sevenzip.ExtractAskMode;
import net.sf.sevenzip.ExtractOperationResult;
import net.sf.sevenzip.IArchiveExtractCallback;
import net.sf.sevenzip.ICryptoGetTextPassword;
import net.sf.sevenzip.ISequentialOutStream;
import net.sf.sevenzip.ISevenZipInArchive;
import net.sf.sevenzip.PropID;
import net.sf.sevenzip.PropertyInfo;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.simple.ISimpleInArchive;
import net.sf.sevenzip.simple.impl.SimpleInArchiveImpl;

/**
 * Implementation of {@link ISevenZipInArchive}.
 * 
 * @author Boris Brodski
 * 
 */
public class InArchiveImpl implements ISevenZipInArchive {
	private static final class ExtractSlowCryptoCallback extends ExtractSlowCallback implements ICryptoGetTextPassword {

		private String password;

		public ExtractSlowCryptoCallback(ISequentialOutStream sequentialOutStream, String password) {
			super(sequentialOutStream);
			this.password = password;
		}

		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return password;
		}
	}

	private static class ExtractSlowCallback implements IArchiveExtractCallback {
		ISequentialOutStream sequentialOutStreamParam;

		public ExtractSlowCallback(ISequentialOutStream sequentialOutStream) {
			this.sequentialOutStreamParam = sequentialOutStream;
		}

		public void setTotal(long total) {
		}

		public void setCompleted(long completeValue) {
		}

		public void setOperationResult(ExtractOperationResult extractOperationResult) {
		}

		public boolean prepareOperation(ExtractAskMode extractAskMode) {
			return true;
		}

		public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
			return extractAskMode.equals(ExtractAskMode.EXTRACT) ? sequentialOutStreamParam : null;
		}
	}

	@SuppressWarnings("unused")
	private int sevenZipArchiveInstance;

	@SuppressWarnings("unused")
	private int sevenZipArchiveInStreamInstance;

	/**
	 * {@inheritDoc}
	 */
	public void extract(int[] indices, boolean testMode, IArchiveExtractCallback extractCallback)
			throws SevenZipException {

		nativeExtract(indices, testMode, extractCallback);
	}

	/**
	 * {@inheritDoc}
	 */
	public void extractSlow(int index, ISequentialOutStream outStream) throws SevenZipException {
		nativeExtract(new int[] { index }, false, new ExtractSlowCallback(outStream));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void extractSlow(int index, ISequentialOutStream outStream, String password) throws SevenZipException {
		nativeExtract(new int[] { index }, false, new ExtractSlowCryptoCallback(outStream, password));
	}

	private native void nativeExtract(int[] indices, boolean testMode, IArchiveExtractCallback extractCallback)
			throws SevenZipException;

	private native Object nativeGetArchiveProperty(int propID) throws SevenZipException;

	/**
	 * {@inheritDoc}
	 */
	public Object getArchiveProperty(PropID propID) throws SevenZipException {

		return nativeGetArchiveProperty(propID.getPropIDIndex());
	}

	private native String nativeGetStringArchiveProperty(int propID) throws SevenZipException;

	/**
	 * {@inheritDoc}
	 */
	public String getStringArchiveProperty(PropID propID) throws SevenZipException {
		return nativeGetStringArchiveProperty(propID.getPropIDIndex());
	}

	private native PropertyInfo nativeGetArchivePropertyInfo(int index);

	/**
	 * {@inheritDoc}
	 */
	public PropertyInfo getArchivePropertyInfo(PropID propID) throws SevenZipException {
		return nativeGetArchivePropertyInfo(propID.getPropIDIndex());
	}

	private native int nativeGetNumberOfArchiveProperties() throws SevenZipException;

	/**
	 * {@inheritDoc}
	 */
	public int getNumberOfArchiveProperties() throws SevenZipException {
		return nativeGetNumberOfArchiveProperties();
	}

	private native int nativeGetNumberOfProperties() throws SevenZipException;

	/**
	 * {@inheritDoc}
	 */
	public int getNumberOfProperties() throws SevenZipException {
		return nativeGetNumberOfProperties();
	}

	private native PropertyInfo nativeGetPropertyInfo(int index) throws SevenZipException;

	/**
	 * {@inheritDoc}
	 */
	public PropertyInfo getPropertyInfo(PropID propID) throws SevenZipException {
		return nativeGetPropertyInfo(propID.getPropIDIndex());
	}

	private native void nativeClose() throws SevenZipException;

	/**
	 * {@inheritDoc}
	 */
	public void close() throws SevenZipException {
		nativeClose();
	}

	private native int nativeGetNumberOfItems() throws SevenZipException;

	/**
	 * {@inheritDoc}
	 */
	public int getNumberOfItems() throws SevenZipException {
		return nativeGetNumberOfItems();
	}

	private native Object nativeGetProperty(int index, int propID);

	/**
	 * {@inheritDoc}
	 */
	public Object getProperty(int index, PropID propID) throws SevenZipException {
		return nativeGetProperty(index, propID.getPropIDIndex());
	}

	private native String nativeGetStringProperty(int index, int propID);

	/**
	 * {@inheritDoc}
	 */
	public String getStringProperty(int index, PropID propID) throws SevenZipException {
		return nativeGetStringProperty(index, propID.getPropIDIndex());
	}

	/**
	 * {@inheritDoc}
	 */
	public ISimpleInArchive getSimpleInterface() {
		return new SimpleInArchiveImpl(this);
	}
}
