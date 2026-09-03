package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to create new Xz archives.<br>
 * Standard way to get implementation is to use {@link SevenZip#openOutArchiveXz()}. See {@link IOutCreateArchive}
 * -JavaDoc for more information.
 *
 * <i>NOTE:</i> Each instance should be closed using {@link IOutArchive#close()} method.
 *
 * @see IOutCreateArchive
 * @see ArchiveFormat#XZ
 *
 * @author Boris Brodski
 * @since 23.01
 */
public interface IOutCreateArchiveXz extends IOutCreateArchive<IOutItemXz>, //
        IOutFeatureSetLevel {
}
