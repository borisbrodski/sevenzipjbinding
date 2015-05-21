package net.sf.sevenzipjbinding;

import java.io.Closeable;

/**
 * The central interface to create new archives.<br>
 * <br>
 * The are two ways to get an implementation of this interface:
 * <ul>
 * <li><b>use {@link SevenZip#openOutArchive(ArchiveFormat)}</b><br>
 * The method returns an instance of the generic {@link IOutCreateArchive} interface allowing creation of an archive of
 * any supported format. For currently supported formats see the 'compression' column of the {@link ArchiveFormat}
 * -JavaDoc. The user can check, whether the current archive format supports a particular configuration method by making
 * an <code>instanceof</code> check, like this:
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
 * @param <E>
 *            the type of the callback used in the non-generic create archive method
 *            {@link #createArchive(ISequentialOutStream, int, IOutCreateCallback)}. See {@link IOutItemCallback7z} for
 *            an example.<br>
 *            <i>Note:</i> the type <code>E</code> is not relevant, if the generic create archive method
 *            {@link #createArchive(ISequentialOutStream, int, IOutCreateCallbackGeneric)} used.
 * 
 * @author Boris Brodski
 * @since 2.0
 * 
 */
public interface IOutCreateArchive<E extends IOutItemCallbackBase> extends Closeable {

    /**
     * Create new archive. To update an existing archive open it first and then use
     * {@link IInArchive#getConnectedOutArchive()} to get an instance of {@link IOutUpdateArchive}.<br>
     * <br>
     * The <code>outCreateCallback</code> is designed to provide archive item properties by implementing multiple
     * getter-like methods. To provide item properties in a more generic way using a single method
     * {@link IOutCreateCallbackGeneric#getProperty(int, PropID)} use
     * {@link #createArchive(ISequentialOutStream, int, IOutCreateCallbackGeneric)}.
     * 
     * @param outStream
     *            output stream to get the new archive
     * @param numberOfItems
     *            number of items in the new archive
     * @param outCreateCallback
     *            create call back object to provide information for archive create/update operations. Should implements
     *            {@link IOutItemCallback} or one of the specified <code>IOutItemCallback*</code> interfaces.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void createArchive(ISequentialOutStream outStream, int numberOfItems,
            IOutCreateCallback<? extends E> outCreateCallback) throws SevenZipException;

    /**
     * Create new archive. To update an existing archive open it first and then use
     * {@link IInArchive#getConnectedOutArchive()} to get an instance of {@link IOutUpdateArchive}.<br>
     * <br>
     * Provide an implementation of the {@link IOutCreateCallbackGeneric#getProperty(int, PropID)} method to passed
     * archive item properties in a generic way. Alternatively the method
     * {@link #createArchive(ISequentialOutStream, int, IOutCreateCallback)} can to used to pass archive item properties
     * implementing one getter-like method per property. See {@link IOutItemCallback} for more information.
     * 
     * @see IOutItemCallback
     * 
     * @param outStream
     *            output stream to get the new archive
     * @param numberOfItems
     *            number of items in the new archive
     * @param outCreateCallbackGeneric
     *            create call back object to provide more information for archive create/update operations.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occur. Check exception message for more information.
     */
    public void createArchive(ISequentialOutStream outStream, int numberOfItems,
            IOutCreateCallbackGeneric outCreateCallbackGeneric) throws SevenZipException;

    /**
     * Return archive format used with this instance of {@link IOutStream}
     * 
     * @return archive format used with this instance of {@link IOutStream}
     */
    public ArchiveFormat getArchiveFormat();
}
