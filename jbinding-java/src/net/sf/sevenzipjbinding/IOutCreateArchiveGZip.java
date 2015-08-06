package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to create new GZip archives.<br>
 * Standard way to get implementation is to use {@link SevenZip#openOutArchiveGZip()}. See {@link IOutCreateArchive}
 * -JavaDoc for more information.
 * 
 * <i>NOTE:</i> Each instance should be closed using {@link IOutArchive#close()} method.
 * 
 * @see IOutCreateArchive
 * @see ArchiveFormat#GZIP
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public interface IOutCreateArchiveGZip extends IOutCreateArchive<IOutItemGZip>, //
        IOutFeatureSetLevel {
}
