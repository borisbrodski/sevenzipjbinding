package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutArchiveGZip;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * TODO
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 * 
 */
public class OutArchiveGZipImpl extends OutArchiveImpl implements IOutArchiveGZip {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) throws SevenZipException {
        nativeSetLevel(compressionLevel);
    }

}
