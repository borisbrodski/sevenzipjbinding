package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to update an existing archive. The standard way to get the implementation is to
 * use {@link IInArchive#getConnectedOutArchive()} method like this:<br>
 * <br>
 *
 * <pre>
 *  {@link IInArchive} inArchive = {@link SevenZip}.openInArchive({@link ArchiveFormat#SEVEN_ZIP}, inStream);
 *  {@link IOutUpdateArchive}{@code <}{@link IOutItemAllFormats}{@code >} outArchive = inArchive.getConnectedOutArchive();
 *
 *  if (outArchive instanceof {@link IOutFeatureSetLevel}) {
 *      (({@link IOutFeatureSetLevel})outArchive).setLevel(myLevel);
 *  }
 *
 *  outArchive.updateItems(...);
 *  ...
 *
 *  inArchive.close();
 * </pre>
 *
 * <br>
 * No explicit closing is necessary. A connected out-archive gets closed automatically when the corresponding in-archive
 * gets closed.
 *
 * @param <T>
 *            the type of the corresponding archive item data class (out item), like {@link IOutItem7z} or
 *            {@link IOutItemZip}. Use {@link IOutItemAllFormats} interface to support all available archive formats.
 *
 * @see IInArchive
 * @see IOutItemBase
 * @see IOutItemAllFormats
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 *
 */
public interface IOutUpdateArchive<T extends IOutItemBase> extends IOutArchiveBase {

    /**
     * Update items in archive (actually creating a new one based on the old one).
     *
     * @param outStream
     *            output stream to get the new archive
     * @param numberOfItems
     *            number of items in the new archive
     * @param outCreateCallback
     *            create call back object to provide more information for archive update operation.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error occurs. Use {@link SevenZipException#printStackTraceExtended()} to get
     *             stack traces of this SevenZipException and of all thrown 'caused by' exceptions.
     */
    public void updateItems(ISequentialOutStream outStream, int numberOfItems, IOutCreateCallback<T> outCreateCallback)
            throws SevenZipException;

    /**
     * Return the archive format of this out-archive instance
     *
     * @return the archive format of this out-archive instance
     */
    public ArchiveFormat getArchiveFormat();

    /**
     * Get connected {@link IInArchive} for update operations.
     *
     * @return connected {@link IInArchive} for update operations
     */
    public IInArchive getConnectedInArchive();
}
