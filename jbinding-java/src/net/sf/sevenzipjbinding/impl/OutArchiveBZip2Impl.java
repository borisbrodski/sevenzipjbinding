package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutCreateArchiveBZip2;
import net.sf.sevenzipjbinding.IOutItemBZip2;
import net.sf.sevenzipjbinding.IOutUpdateArchiveBZip2;

/**
 * BZip2 specific archive create and update class.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public class OutArchiveBZip2Impl extends OutArchiveImpl<IOutItemBZip2> implements IOutCreateArchiveBZip2,
        IOutUpdateArchiveBZip2 {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) {
        featureSetLevel(compressionLevel);
    }

}
