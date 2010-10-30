package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutArchiveZip;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * TODO
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 * 
 */
public class OutArchiveZipImpl extends OutArchiveImpl implements IOutArchiveZip {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) throws SevenZipException {
        nativeSetLevel(compressionLevel);
    }

}
