package net.sf.sevenzipjbinding;

/**
 * The interface provides functionality to create or update gzip archives.<br>
 * TODO Split creation and update. <br>
 * Standard way to get implementation is to use {@link SevenZip}.<br>
 * <br>
 * The last call should be a call of the method {@link ISevenZipInArchive#close()}. After this call no more Methods
 * should be called. TODO Remove this one.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public interface IOutArchiveGZip extends IFeatureSetLevel {
}
