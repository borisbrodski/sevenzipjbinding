package net.sf.sevenzipjbinding;

import java.io.Closeable;
import java.io.IOException;

/**
 * The central interface to create new archives. To update an existing archive open it first and then use
 * {@link IInArchive#getConnectedOutArchive()} to get an instance of the {@link IOutUpdateArchive} interface.<br>
 * <br>
 * There are two ways of getting an implementation of this interface:
 * <ul>
 * <li><b>use {@link SevenZip#openOutArchive(ArchiveFormat)}</b><br>
 * The method returns an instance of the {@link IOutCreateArchive}{@code <}{@link IOutItemAllFormats}{@code >} interface
 * allowing creation of a new archive of any supported archive format. To get all currently supported formats see the
 * 'compression' column of the {@link ArchiveFormat}-JavaDoc. The user can check whether the current archive format
 * supports a particular configuration method by making an <code>instanceof</code> check, like this:
 *
 * <pre>
 *  IOutCreateArchive{@code <}IOutItemAllFormats{@code >} outArchive = SevenZip.openOutArchive(myArchiveFormat);
 *  if (outArchive instanceof IOutFeatureSetLevel) {
 *      ((IOutFeatureSetLevel)outArchive).setLevel(myLevel);
 *  }
 *
 *  ...
 *
 *  outArchive.close();
 * </pre>
 *
 * <li><b> use one of the <code>SevenZip.openOutArchiveXxx</code></b><br>
 * Those methods provide implementations of the corresponding archive-format-specific interfaces. Those interfaces
 * contain all supported configuration methods for the selected archive format. No cast or <code>instanceof</code> check
 * is needed in this case.<br>
 * For example, the method {@link SevenZip#openOutArchive7z()} returns an implementation of the
 * {@link IOutCreateArchive7z} interface with all configuration methods, supported by the 7z format, like
 * {@link IOutFeatureSetLevel#setLevel(int)}.
 *
 * <pre>
 * IOutCreateArchive7z outArchive7z = SevenZip.openOutArchive7z();
 * outArchive7z.setLevel(myLevel);
 *
 * ...
 *
 * outArchive7z.close();
 * </pre>
 *
 * </ul>
 *
 * <i>NOTE:</i> Each instance should be closed using {@link IOutArchive#close()} method.
 *
 * @param <T>
 *            the type of the corresponding archive item data class (out item), like {@link IOutItem7z} or
 *            {@link IOutItemZip}. Use {@link IOutItemAllFormats} interface to support all available archive formats.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 *
 */
public interface IOutCreateArchive<T extends IOutItemBase> extends IOutArchiveBase, Closeable {
    /**
     * Close the archive and free the native resources. Afterwards every other method throws a
     * {@link SevenZipException} ("OutArchive closed"). If another thread is still inside
     * {@link #createArchive(ISequentialOutStream, int, IOutCreateCallback)}, <code>close()</code> waits for it to
     * return. <code>close()</code> must not be called from a callback of this archive (on the thread running the
     * creation): it throws a {@link SevenZipException} instead of deadlocking.
     *
     * @throws IOException
     *             7-Zip or 7-Zip-JBinding error occurs (a {@link SevenZipException})
     */
    public void close() throws IOException;

    /**
     * Create new archive. To update an existing archive open it first and then use
     * {@link IInArchive#getConnectedOutArchive()} to get an instance of the {@link IOutUpdateArchive} interface.<br>
     * <br>
     * The <code>outCreateCallback</code> is designed to provide necessary information about new archive items and to
     * receive information about the progress of the operation.<br>
     * <br>
     * <i>Note:</i> the 7z format requires a seekable {@link IOutStream} (instead of a plain
     * {@link ISequentialOutStream}) and reports a {@link SevenZipException} otherwise. Zip, Tar, GZip, BZip2 and XZ
     * are written sequentially if the stream isn't an {@link IOutStream}.
     *
     * @param outStream
     *            output stream to receive the new archive. An implementation of the {@link IOutStream} interface is
     *            required for the 7z format.
     * @param numberOfItems
     *            number of items in the new archive
     * @param outCreateCallback
     *            callback object to exchange information about the archive create operation.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public void createArchive(ISequentialOutStream outStream, int numberOfItems,
            IOutCreateCallback<? extends T> outCreateCallback) throws SevenZipException;

    /**
     * Return the archive format of this out-archive instance
     *
     * @return the archive format of this out-archive instance
     */
    public ArchiveFormat getArchiveFormat();
}
