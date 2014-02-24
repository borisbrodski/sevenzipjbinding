package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutItemCallbackGZip;
import net.sf.sevenzipjbinding.IOutUpdateArchiveGZip;

/**
 * TODO
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 * 
 */
public class OutArchiveGZipImpl extends OutArchiveImpl<IOutItemCallbackGZip> implements IOutUpdateArchiveGZip {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) {
        featureSetLevel(compressionLevel);
    }

}
