package net.sf.sevenzipjbinding.impl;

import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.IOutUpdateCallback;
import net.sf.sevenzipjbinding.IOutUpdateCallbackGeneric;
import net.sf.sevenzipjbinding.SevenZipException;

class OutUpdateGenericCallbackWrapper extends OutCreateGenericCallbackWrapper implements
        IOutUpdateCallback<IOutItemCallback> {

    private IOutUpdateCallbackGeneric outUpdateCallbackGeneric;

    OutUpdateGenericCallbackWrapper(IOutUpdateCallbackGeneric outUpdateCallbackGeneric) {
        super(outUpdateCallbackGeneric);
        this.outUpdateCallbackGeneric = outUpdateCallbackGeneric;
    }

    public boolean isNewData(int index) throws SevenZipException {
        return outUpdateCallbackGeneric.isNewData(index);
    }

    public boolean isNewProperties(int index) throws SevenZipException {
        return outUpdateCallbackGeneric.isNewProperties(index);
    }

    public int getOldArchiveItemIndex(int index) throws SevenZipException {
        return outUpdateCallbackGeneric.getOldArchiveItemIndex(index);
    }
}
