package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutArchiveBZip2;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * TODO
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 * 
 */
public class OutArchiveBZip2Impl extends OutArchiveImpl implements IOutArchiveBZip2 {

    /**
     * {@inheritDoc}
     */
    public void setLevel(int compressionLevel) throws SevenZipException {
        nativeSetLevel(compressionLevel);
    }

}
