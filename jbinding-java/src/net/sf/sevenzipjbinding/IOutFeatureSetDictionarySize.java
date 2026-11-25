package net.sf.sevenzipjbinding;

/**
 * Feature interface for the setting 'dictionary size' of the compression method. Use
 * {@link SevenZip#openOutArchive7z()} or {@link IInArchive#getConnectedOutArchive()} to get implementation of this
 * interface.
 *
 * @see IOutCreateArchive7z
 * @see IOutUpdateArchive7z
 * @see IOutFeatureSetLevel
 *
 * @author Drakeet
 * @since 26.06-2.4
 */
public interface IOutFeatureSetDictionarySize {

    /**
     * Set the dictionary size of the main compression method, for example LZMA2 for 7z archives. Without this setting
     * the dictionary size is derived from the compression level (see {@link IOutFeatureSetLevel#setLevel(int)}). Both
     * settings can be combined: the compression level still controls the other parameters of the method, while the
     * dictionary size set here replaces the one implied by the level.
     * <p>
     * A smaller dictionary needs less memory to compress and to extract, and with LZMA2 it also allows a single large
     * file to be split into more blocks that are compressed in parallel. 7-Zip may still use a smaller dictionary if
     * the data to compress is smaller than the dictionary. LZMA2 stores the size in the archive rounded up to 2^n or
     * 3*2^n bytes, 4 KiB at least.
     * With compression level 0 (no compression) the dictionary size is ignored. A value that the compression method
     * does not support is reported as a {@link SevenZipException} by the operation that creates or updates the
     * archive.
     *
     * @param dictionarySize
     *            dictionary size in bytes, <code>-1</code> - use default (derived from the compression level)
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There is no guarantee that no further callback methods will be called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>IInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     * @throws IllegalArgumentException
     *             if the dictionary size is neither positive nor <code>-1</code>
     */
    public void setDictionarySize(long dictionarySize) throws SevenZipException;
}
