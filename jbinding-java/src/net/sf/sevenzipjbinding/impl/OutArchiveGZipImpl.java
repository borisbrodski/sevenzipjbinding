package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutCreateArchiveGZip;
import net.sf.sevenzipjbinding.IOutItemCallbackGZip;
import net.sf.sevenzipjbinding.IOutUpdateArchiveGZip;

/**
 * GZip specific archive create and update class.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public class OutArchiveGZipImpl extends OutArchiveImpl<IOutItemCallbackGZip> implements IOutCreateArchiveGZip,
        IOutUpdateArchiveGZip {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) {
        featureSetLevel(compressionLevel);
    }

}
