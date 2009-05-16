package net.sf.sevenzip;

import net.sf.sevenzip.impl.RandomAccessFileInStream;

/**
 * 7-Zip interface entry point class. Opens archive and returns implementation
 * of {@link ISevenZipInArchive}
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public class SevenZip {
	private static final class ArchiveOpenCryptoCallback implements
			IArchiveOpenCallback, ICryptoGetTextPassword {

		private final String passwordForOpen;

		public ArchiveOpenCryptoCallback(String passwordForOpen) {
			this.passwordForOpen = passwordForOpen;
		}

		@Override
		public void setCompleted(Long files, Long bytes) {
		}

		@Override
		public void setTotal(Long files, Long bytes) {
		}

		@Override
		public String cryptoGetTextPassword() throws SevenZipException {
			return passwordForOpen;
		}
	}

	private static boolean initialized = false;

	/**
	 * Hide default constructor
	 */
	private SevenZip() {

	}

	static {
		initSevenZipNativeLibrary();
	}

	private static native String nativeInitSevenZipLibrary();

	/**
	 * Ensure, that 7-Zip JBinding was intialized.
	 */
	public static void initSevenZipNativeLibrary() {
		if (initialized) {
			return;
		}
		initialized = true;

		System.load("/home/boris/Coding/SevenZipJBinding/SevenZipJBinding-build/jbinding-cpp/lib7-Zip-JBinding.so");
		String errorMessage = nativeInitSevenZipLibrary();
		if (errorMessage != null) {
			throw new RuntimeException("Error initializing 7-Zip-JBinding: "
					+ errorMessage);
		}
	}

	private static native ISevenZipInArchive nativeOpenArchive(String formatName,
			IInStream inStream, IArchiveOpenCallback archiveOpenCallback)
			throws SevenZipException;

	/**
	 * Open archive of type <code>archiveFormat</code> from the input stream
	 * <code>inStream</code> using 'archive open callback' listener
	 * <code>archiveOpenCallback</code>. To open archive from the file, use
	 * {@link RandomAccessFileInStream}.
	 * 
	 * @param archiveFormat
	 *            format of archive
	 * @param inStream
	 *            input stream to open archive from
	 * @param archiveOpenCallback
	 *            archive open callback listenter to use
	 * @return implementation of {@link ISevenZipInArchive} which represents
	 *         opened archive.
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public static ISevenZipInArchive openInArchive(ArchiveFormat archiveFormat,
			IInStream inStream, IArchiveOpenCallback archiveOpenCallback)
			throws SevenZipException {
		return nativeOpenArchive(archiveFormat.getMethodName(), inStream,
				archiveOpenCallback);
	}

	/**
	 * Open archive of type <code>archiveFormat</code> from the input stream
	 * <code>inStream</code> using 'archive open callback' listener
	 * <code>archiveOpenCallback</code>. To open archive from the file, use
	 * {@link RandomAccessFileInStream}.
	 * 
	 * @param archiveFormat
	 *            format of archive
	 * @param inStream
	 *            input stream to open archive from
	 * @param passwordForOpen
	 *            password to use. Warning: this password will not be used to
	 *            extract item from archive but only to open archive. (7-zip
	 *            format supports crypted filename)
	 * @return implementation of {@link ISevenZipInArchive} which represents
	 *         opened archive.
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public static ISevenZipInArchive openInArchive(ArchiveFormat archiveFormat,
			IInStream inStream, String passwordForOpen)
			throws SevenZipException {
		return nativeOpenArchive(archiveFormat.getMethodName(), inStream,
				new ArchiveOpenCryptoCallback(passwordForOpen));
	}

	/**
	 * Open archive of type <code>archiveFormat</code> from the input stream
	 * <code>inStream</code>. To open archive from the file, use
	 * {@link RandomAccessFileInStream}.
	 * 
	 * @param archiveFormat
	 *            format of archive
	 * @param inStream
	 *            input stream to open archive from
	 * @return implementation of {@link ISevenZipInArchive} which represents
	 *         opened archive.
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public static ISevenZipInArchive openInArchive(ArchiveFormat archiveFormat,
			IInStream inStream) throws SevenZipException {
		return nativeOpenArchive(archiveFormat.getMethodName(), inStream, null);
	}
}
