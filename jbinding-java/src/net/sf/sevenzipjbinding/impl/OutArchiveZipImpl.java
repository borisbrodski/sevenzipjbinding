package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutCreateArchiveZip;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.IOutStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

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

    public void createArchive(IOutStream outStream, int numberOfItems,
            IOutCreateCallback<? extends IOutItemZip> outCreateCallback) throws SevenZipException {
        createArchive((ISequentialOutStream) outStream, numberOfItems, outCreateCallback);
    }

}
