package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to update existing 7z archives.<br>
 * The standard way of getting the implementation of this interface is to use
 * {@link IInArchive#getConnectedOutArchive()} method like this:<br>
 * <br>
 * 
 * <pre>
 *  {@link IInArchive} inArchive = {@link SevenZip}.openInArchive(unknownArchiveFormat, inStream);
 *  {@link IOutUpdateArchive}{@code <}{@link IOutItemCallbackBase}> outArchive = inArchive.openOutArchive();
 *  
 *  if (outArchive instanceof {@link IOutUpdateArchive7z}) {
 *    {@link IOutUpdateArchive7z} outUpdateArchive7z = ({@link IOutUpdateArchive7z})outArchive;
 *    ...
 *  }
 *  
 *  ...
 *  
 *  inArchive.close();
 * </pre>
 * 
 * <i>Note</i>: if the archive format is known, then there is more simple way of updating. See {@link IOutUpdateArchive}
 * for more details.
 * 
 * @see IOutUpdateArchive
 * @see IInArchive
 * @see IOutItemCallbackBase
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public interface IOutUpdateArchive7z extends IOutUpdateArchive<IOutItemCallback7z>, //
        IOutFeatureSetLevel, //
        IOutFeatureSetSolid, //
        IOutFeatureSetMultithreading {
}
