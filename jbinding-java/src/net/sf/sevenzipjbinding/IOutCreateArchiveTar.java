package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to create new Tar archives.<br>
 * Standard way to get implementation is to use {@link SevenZip#openOutArchiveTar()}. See {@link IOutCreateArchive}
 * -JavaDoc for more information.
 * 
 * <i>NOTE:</i> Each instance should be closed using {@link IOutArchive#close()} method.
 * 
 * @see IOutCreateArchive
 * @see ArchiveFormat#TAR
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public interface IOutCreateArchiveTar extends IOutCreateArchive<IOutItemTar> {
}
