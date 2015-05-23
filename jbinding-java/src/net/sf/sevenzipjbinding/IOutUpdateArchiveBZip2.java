package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to update existing Bzip2 archives.<br>
 * The standard way to get implementation is to use {@link SevenZip#openOutArchive(ArchiveFormat)} like this:<br>
 * <br>
 * 
 * <pre>
 *  {@link IOutUpdateArchive7z} outArchive = {@link SevenZip}.openOutArchive({@link IOutUpdateArchive7z}.class);
 * </pre>
 * 
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public interface IOutUpdateArchiveBZip2 extends IOutUpdateArchive<IOutItemCallbackBZip2>, //
        IOutFeatureSetLevel {
}
