package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to update existing GZip archives.<br>
 * The standard way of getting the implementation of this interface is to use
 * {@link IInArchive#getConnectedOutArchive()} method like this:<br>
 * <br>
 * 
 * <pre>
 *  {@link IInArchive} inArchive = {@link SevenZip}.openInArchive(null, inStream);
 *  {@link IOutUpdateArchive}{@code <}{@link IOutItemBase}> outArchive = inArchive.openOutArchive();
 *  
 *  if (outArchive instanceof {@link IOutUpdateArchiveGZip}) {
 *    {@link IOutUpdateArchiveGZip} outUpdateArchiveGZip = ({@link IOutUpdateArchiveGZip})outArchive;
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
 * @version 9.13-2.0
 */
public interface IOutUpdateArchiveGZip extends IOutUpdateArchive<IOutItemGZip>, //
        IOutFeatureSetLevel {
}
