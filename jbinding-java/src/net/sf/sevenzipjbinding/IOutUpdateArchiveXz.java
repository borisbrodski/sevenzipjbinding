package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to update existing Xz archives.<br>
 * The standard way of getting the implementation of this interface is to use
 * {@link IInArchive#getConnectedOutArchive()} method like this:<br>
 * <br>
 *
 * <pre>
 *  {@link IInArchive} inArchive = {@link SevenZip}.openInArchive(null, inStream);
 *  {@link IOutUpdateArchive}{@code <}{@link IOutItemAllFormats}{@code >} outArchive = inArchive.getConnectedOutArchive();
 *
 *  if (outArchive instanceof {@link IOutUpdateArchiveXz}) {
 *    {@link IOutUpdateArchiveXz} outUpdateArchiveXz = ({@link IOutUpdateArchiveXz})outArchive;
 *    ...
 *  }
 *
 *  outArchive.updateItems(...);
 *
 *  ...
 *
 *  inArchive.close();
 * </pre>
 *
 * @see IOutUpdateArchive
 * @see IInArchive
 * @see IOutItemBase
 * @see IOutItemAllFormats
 *
 * @author Boris Brodski
 * @since 23.01-2.2
 */
public interface IOutUpdateArchiveXz extends IOutUpdateArchive<IOutItemXz>, //
        IOutFeatureSetLevel {
}
