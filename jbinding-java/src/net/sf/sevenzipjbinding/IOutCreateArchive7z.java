package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to create new 7-zip archives.<br>
 * Standard way to get implementation is to use {@link SevenZip#openOutArchive7z()}. See {@link IOutCreateArchive}
 * -JavaDoc for more information.
 * 
 * <i>NOTE:</i> Each instance should be closed using {@link IOutArchive#close()} method.
 * 
 * @see IOutCreateArchive
 * @see ArchiveFormat#SEVEN_ZIP
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public interface IOutCreateArchive7z extends IOutCreateArchive<OutItem7z>, //
        IOutFeatureSetLevel, //
        IOutFeatureSetSolid, //
        IOutFeatureSetMultithreading {
}
