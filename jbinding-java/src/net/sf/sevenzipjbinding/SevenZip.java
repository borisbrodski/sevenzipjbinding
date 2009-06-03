package net.sf.sevenzipjbinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

/**
 * 7-Zip interface entry point class. Opens archive and returns implementation of {@link ISevenZipInArchive}
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public class SevenZip {
	private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";
	private static final String PROPERTY_SEVENZIPJBINDING_LIBNAME = "sevenzipjbinding.libname";
	private static final String SEVENZIPJBINDING_LIB_PROPERTIES_FILENAME = "/sevenzipjbinding-lib.properties";

	private static final class ArchiveOpenCryptoCallback implements IArchiveOpenCallback, ICryptoGetTextPassword {
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

	private static boolean needInitialization = false;
	private static boolean initializationSuccessful = false;
	private static RuntimeException autoinitializationException = null;

	/**
	 * Hide default constructor
	 */
	private SevenZip() {

	}

	private static native String nativeInitSevenZipLibrary();

	/**
	 * Initialize native SevenZipJBinding library assuming <code>sevenzipjbinding-<i>Platform</i>.jar</code> on the
	 * class path. The platform depended library will to copied to the temporary directory and loaded into JVM using
	 * {@link System#load(String)}
	 * 
	 * @throws SevenZipNativeInitializationException
	 *             indicated problems finding a native library, coping it into the temporary directory or loading it.
	 */
	public static void initSevenZipFromPlatformJAR() throws SevenZipNativeInitializationException {
		initSevenZipFromPlatformJAR(null);
	}

	/**
	 * Initialize native SevenZipJBinding library assuming <code>sevenzipjbinding-<i>Platform</i>.jar</code> on the
	 * class path. The platform depended library will to copied to the temporary directory and loaded into JVM using
	 * {@link System#load(String)}
	 * 
	 * @param tmpDirectory
	 *            path to the <i>tmp</i> directory. This directory must be writable.
	 * 
	 * @throws SevenZipNativeInitializationException
	 *             indicated problems finding a native library, coping it into the temporary directory or loading it.
	 */
	public static void initSevenZipFromPlatformJAR(String tmpDirectory) throws SevenZipNativeInitializationException {
		needInitialization = false;
		if (initializationSuccessful) {
			return;
		}

		// Load 'sevenzipjbinding-lib.properties'
		InputStream sevenZipJBindingLibProperties = SevenZip.class
				.getResourceAsStream(SEVENZIPJBINDING_LIB_PROPERTIES_FILENAME);
		if (sevenZipJBindingLibProperties == null) {
			throwInitException("error loading property file '" + SEVENZIPJBINDING_LIB_PROPERTIES_FILENAME
					+ "' from a jar-file 'sevenzipjbinding-<Platform>.jar'. Is the jar-file not in the class path?");
		}

		Properties properties = new Properties();
		try {
			properties.load(sevenZipJBindingLibProperties);
		} catch (IOException e) {
			throwInitException("error loading property file '" + SEVENZIPJBINDING_LIB_PROPERTIES_FILENAME
					+ "' from a jar-file 'sevenzipjbinding-<Platform>.jar'");
		}

		String libname = properties.getProperty(PROPERTY_SEVENZIPJBINDING_LIBNAME);
		if (libname == null) {
			throwInitException("property file '" + SEVENZIPJBINDING_LIB_PROPERTIES_FILENAME
					+ "' from a jar-file 'sevenzipjbinding-<Platform>.jar' don't contain the property named '"
					+ PROPERTY_SEVENZIPJBINDING_LIBNAME + "'");
		}

		String tmpDirectoryToUse;
		if (tmpDirectory != null) {
			tmpDirectoryToUse = tmpDirectory;
		} else {
			tmpDirectoryToUse = System.getProperty(SYSTEM_PROPERTY_TMP);
		}

		if (tmpDirectoryToUse == null) {
			throwInitException("can't determinte tmp directory. Use may use -D" + SYSTEM_PROPERTY_TMP
					+ "=<path to tmp dir> parameter for jvm to fix this.");
		}

		File tmpDirFile = new File(tmpDirectoryToUse);
		if (!tmpDirFile.exists() || !tmpDirFile.isDirectory()) {
			throwInitException("invalid tmp directory '" + tmpDirectory + "'");
		}

		if (!tmpDirFile.canWrite()) {
			throwInitException("can't create files in '" + tmpDirFile.getAbsolutePath() + "'");
		}

		String libTmpName = generateLibTempName(libname);
		File libTmpFile = new File(tmpDirFile.getAbsolutePath() + File.separatorChar + libTmpName);

		InputStream libInputStream = SevenZip.class.getResourceAsStream("/" + libname);
		if (libInputStream == null) {
			throwInitException("error loading native library '" + SEVENZIPJBINDING_LIB_PROPERTIES_FILENAME
					+ "' from a jar-file 'sevenzipjbinding-<Platform>.jar'.");
		}

		copyLibraryToFS(libTmpFile, libInputStream);
		initLibraryFromFileIntern(libTmpFile.getAbsolutePath());
	}

	/**
	 * Initialize SevenZipJBinding native library from a file
	 * 
	 * @param sevenZipJBindingNativeLibraryFullpath
	 *            full path (including file name) of the native library to be loaded into JVM
	 * 
	 * @throws SevenZipNativeInitializationException
	 *             indicated problems finding a native library, coping it into the temporary directory or loading it.
	 */
	public static void initLibraryFromFile(String sevenZipJBindingNativeLibraryFullpath)
			throws SevenZipNativeInitializationException {
		needInitialization = false;
		if (initializationSuccessful) {
			return;
		}

		initLibraryFromFileIntern(sevenZipJBindingNativeLibraryFullpath);
	}

	private static void initLibraryFromFileIntern(String sevenZipJBindingNativeLibraryFullpath)
			throws SevenZipNativeInitializationException {
		System.load(sevenZipJBindingNativeLibraryFullpath);
		String errorMessage = nativeInitSevenZipLibrary();
		if (errorMessage != null) {
			throw new SevenZipNativeInitializationException("Error initializing 7-Zip-JBinding: " + errorMessage);
		}
		initializationSuccessful = true;
	}

	/**
	 * Open archive of type <code>archiveFormat</code> from the input stream <code>inStream</code> using 'archive open
	 * call back' listener <code>archiveOpenCallback</code>. To open archive from the file, use
	 * {@link RandomAccessFileInStream}.
	 * 
	 * @param archiveFormat
	 *            format of archive
	 * @param inStream
	 *            input stream to open archive from
	 * @param archiveOpenCallback
	 *            archive open callback listenter to use
	 * @return implementation of {@link ISevenZipInArchive} which represents opened archive.
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception message for more information.
	 */
	public static ISevenZipInArchive openInArchive(ArchiveFormat archiveFormat, IInStream inStream,
			IArchiveOpenCallback archiveOpenCallback) throws SevenZipException {
		ensureLibraryIsInitialized();
		return nativeOpenArchive(archiveFormat.getMethodName(), inStream, archiveOpenCallback);
	}

	/**
	 * Open archive of type <code>archiveFormat</code> from the input stream <code>inStream</code> using 'archive open
	 * callback' listener <code>archiveOpenCallback</code>. To open archive from the file, use
	 * {@link RandomAccessFileInStream}.
	 * 
	 * @param archiveFormat
	 *            format of archive
	 * @param inStream
	 *            input stream to open archive from
	 * @param passwordForOpen
	 *            password to use. Warning: this password will not be used to extract item from archive but only to open
	 *            archive. (7-zip format supports crypted filename)
	 * @return implementation of {@link ISevenZipInArchive} which represents opened archive.
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception message for more information.
	 */
	public static ISevenZipInArchive openInArchive(ArchiveFormat archiveFormat, IInStream inStream,
			String passwordForOpen) throws SevenZipException {
		ensureLibraryIsInitialized();
		return nativeOpenArchive(archiveFormat.getMethodName(), inStream,
				new ArchiveOpenCryptoCallback(passwordForOpen));
	}

	/**
	 * Open archive of type <code>archiveFormat</code> from the input stream <code>inStream</code>. To open archive from
	 * the file, use {@link RandomAccessFileInStream}.
	 * 
	 * @param archiveFormat
	 *            format of archive
	 * @param inStream
	 *            input stream to open archive from
	 * @return implementation of {@link ISevenZipInArchive} which represents opened archive.
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception message for more information.
	 */
	public static ISevenZipInArchive openInArchive(ArchiveFormat archiveFormat, IInStream inStream)
			throws SevenZipException {
		ensureLibraryIsInitialized();
		return nativeOpenArchive(archiveFormat.getMethodName(), inStream, null);
	}

	private static void ensureLibraryIsInitialized() {
		if (!needInitialization) {
			try {
				initSevenZipFromPlatformJAR();
			} catch (SevenZipNativeInitializationException exception) {
				autoinitializationException = new RuntimeException(
						"SevenZipJBinding couldn't be initialized automaticly using initialization "
								+ "from platform depended JAR and the default temporary directory. Please, "
								+ "make sure the correct 'sevenzipjbinding-<Platform>.jar' file is "
								+ "in the class path or consider initializing SevenZipJBinding manualy using one of "
								+ "the offered initialization methods: 'net.sf.sevenzipjbinding.SevenZip.init*()'",
						exception);
				throw autoinitializationException;
			}
		}
		if (!initializationSuccessful) {
			throw new RuntimeException("SevenZipJBinding wasn't initialized successfully last time.",
					autoinitializationException);
		}
	}

	private static void throwInitException(String message) throws SevenZipNativeInitializationException {
		throwInitException(null, message);
	}

	private static void throwInitException(Exception exception, String message)
			throws SevenZipNativeInitializationException {
		throw new SevenZipNativeInitializationException("Error loading SevenZipJBinding native library into JVM: "
				+ message + " [You may also try different SevenZipJBinding initialization methods "
				+ "'net.sf.sevenzipjbinding.SevenZip.init*()' in order to solve this problem] ", exception);
	}

	private static void copyLibraryToFS(File toLibTmpFile, InputStream fromLibInputStream) {
		FileOutputStream libTmpOutputStream = null;
		try {
			libTmpOutputStream = new FileOutputStream(toLibTmpFile);
			byte[] buffer = new byte[65536];
			while (true) {
				int read = fromLibInputStream.read(buffer);
				if (read > 0) {
					libTmpOutputStream.write(buffer, 0, read);
				} else {
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SevenZipJBinding native library: "
					+ "can't copy native library out of a resource file to the temporary location: '"
					+ toLibTmpFile.getAbsolutePath() + "'", e);
		} finally {
			try {
				fromLibInputStream.close();
			} catch (IOException e) {
				// Ignore errors here
			}
			try {
				if (fromLibInputStream != null) {
					libTmpOutputStream.close();
				}
			} catch (IOException e) {
				// Ignore errors here
			}
		}
	}

	private static String generateLibTempName(String libname) {
		String randomPostfix = "-" + new Random().nextInt(10000000);
		if (libname.indexOf('.') == -1) {
			return libname + randomPostfix;
		}
		return libname.replaceFirst("\\.", randomPostfix + ".");
	}

	private static native ISevenZipInArchive nativeOpenArchive(String formatName, IInStream inStream,
			IArchiveOpenCallback archiveOpenCallback) throws SevenZipException;

	/**
	 * Tests native library initialization status of SevenZipJBinding
	 * 
	 * @return <code>true</code>
	 */
	public static boolean isInitialized() {
		return initializationSuccessful;
	}
}
