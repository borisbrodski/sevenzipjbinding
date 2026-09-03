package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutCreateArchiveXz;
import net.sf.sevenzipjbinding.IOutItemXz;
import net.sf.sevenzipjbinding.IOutUpdateArchiveXz;

/**
 * Xz specific archive create and update class.
 *
 * @author Boris Brodski
 * @since 23.01
 */
public class OutArchiveXzImpl extends OutArchiveImpl<IOutItemXz> implements IOutCreateArchiveXz,
        IOutUpdateArchiveXz {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) {
        featureSetLevel(compressionLevel);
    }

}
