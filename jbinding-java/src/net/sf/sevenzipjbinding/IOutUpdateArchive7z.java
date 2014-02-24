package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to update existing 7-zip archives.<br>
 * Standard way to get implementation is to use {@link SevenZip#openOutArchive(ArchiveFormat)} like this:<br>
 * <br>
 * 
 * <pre>
 *  {@link IOutUpdateArchive7z} outArchive = {@link SevenZip}.openOutArchive({@link IOutUpdateArchive7z}.class);
 * </pre>
 * 
 * <i>NOTE:</i> Each instance should be closed using {@link IOutArchive#close()} method.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public interface IOutUpdateArchive7z extends IOutUpdateArchive<IOutItemCallback7z>, //
        IOutFeatureSetLevel, //
        IOutFeatureSetSolid, //
        IOutFeatureSetMultithreading {
}
