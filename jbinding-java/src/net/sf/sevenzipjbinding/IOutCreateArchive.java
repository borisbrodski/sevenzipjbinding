package net.sf.sevenzipjbinding;

import java.io.Closeable;

/**
 * The central interface to create new archives. To update an existing archive open it first and then use
 * {@link IInArchive#getConnectedOutArchive()} to get an instance of the {@link IOutUpdateArchive} interface.<br>
 * <br>
 * The are two ways to get an implementation of this interface:
 * <ul>
 * <li><b>use {@link SevenZip#openOutArchive(ArchiveFormat)}</b><br>
 * The method returns an instance of the {@link IOutCreateArchive}{@code <}{@link IOutItemAllFormats}{@code >} interface
 * allowing creation of an archive of any supported archive format. To get all currently supported formats see the
 * 'compression' column of the {@link ArchiveFormat} -JavaDoc. The user can check, whether the current archive format
 * supports a particular configuration method by making an <code>instanceof</code> check, like this:
 * 
 * <pre>
 *  IOutCreateArchive{@code<}IOutItemCallback> outArchive = SevenZip.openOutArchive(myArchiveFormat);
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
 * Those methods provide implementations of corresponding archive format specific interfaces. Those interfaces contain
 * all supported configuration methods for selected archive format. No cast or <code>instanceof</code> check are needed
 * in this case.<br>
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
 * @since 2.0
 * 
 */
public interface IOutCreateArchive<T extends IOutItemBase> extends Closeable {

    /**
     * Create new archive. To update an existing archive open it first and then use
     * {@link IInArchive#getConnectedOutArchive()} to get an instance of the {@link IOutUpdateArchive} interface.<br>
     * <br>
     * The <code>outCreateCallback</code> is designed to get necessary information about archive items and provide
     * information about progress of the operation.<br>
     * <br>
     * <i>Note:</i> some archive formats (like Zip) require an implementation of {@link IOutStream} instead of the
     * {@link ISequentialOutStream}.
     * 
     * @param outStream
     *            output stream to get the new archive (implements {@link IOutStream} or {@link ISequentialOutStream}
     *            depending on the archive format)
     * @param numberOfItems
     *            number of items in the new archive
     * @param outCreateCallback
     *            callback object to exchange information about archive create operation.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void createArchive(ISequentialOutStream outStream, int numberOfItems,
            IOutCreateCallback<? extends T> outCreateCallback) throws SevenZipException;

    /**
     * Return archive format used with this instance of {@link IOutStream}
     * 
     * @return archive format used with this instance of {@link IOutStream}
     */
    public ArchiveFormat getArchiveFormat();
}
