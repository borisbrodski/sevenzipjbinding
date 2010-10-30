package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutArchiveSevenZip;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * TODO
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 * 
 */
public class OutArchiveSevenZipImpl extends OutArchiveImpl implements IOutArchiveSevenZip {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) throws SevenZipException {
        nativeSetLevel(compressionLevel);
    }

}
