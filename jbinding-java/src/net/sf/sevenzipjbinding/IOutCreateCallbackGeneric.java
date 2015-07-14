package net.sf.sevenzipjbinding;

/**
 * Interface to provide information needed to create a new archive. Extends {@link IOutCreateCallbackBase} adding
 * archive item property generic callback. For non-generic archive item callback see {@link IOutCreateCallback}
 * interface.
 * 
 * @see IOutCreateCallback
 * @see IOutCreateCallbackBase
 * @see IOutUpdateCallback
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutCreateCallbackGeneric extends IOutCreateCallbackBase {
    /**
     * Return sequential in-stream for the archive item with index <code>index</code> to read and compress the content
     * of the item.
     * 
     * @param index
     *            index of the item to read content of (starting from 0)
     * @return sequential in-stream pointed to the content of the archive item with index <code>index</code>
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public ISequentialInStream getStream(int index) throws SevenZipException;

    /**
     * Get the value of the property <code>propID</code> of the archive item with id <code>index</code>.
     * 
     * @param index
     *            index of the item get properties (0-based)
     * @param propID
     *            property to get
     * @return value of the property or <code>null</code> if the property <code>propID</code> for the current archive
     *         format is optional.
     * 
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public Object getProperty(int index, PropID propID) throws SevenZipException;

}
