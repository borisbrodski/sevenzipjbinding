package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutItemCallbackZip;

/**
 * Zip specific archive create and update class.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public class OutArchiveZipImpl extends OutArchiveImpl<IOutItemCallbackZip> implements IOutCreateArchiveZip {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) {
        featureSetLevel(compressionLevel);
    }

}
