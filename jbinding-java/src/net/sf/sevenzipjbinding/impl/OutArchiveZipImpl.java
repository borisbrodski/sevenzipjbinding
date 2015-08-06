package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutItemZip;

/**
 * Zip specific archive create and update class.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public class OutArchiveZipImpl extends OutArchiveImpl<IOutItemZip> implements IOutCreateArchiveZip {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) {
        featureSetLevel(compressionLevel);
    }

}
