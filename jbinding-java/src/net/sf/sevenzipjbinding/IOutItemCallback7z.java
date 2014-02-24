package net.sf.sevenzipjbinding;

public interface IOutItemCallback7z extends IOutItemCallbackBase {
    public boolean isAnti(int index) throws SevenZipException;

    public long getSize(int index) throws SevenZipException;

    public String getPath(int index) throws SevenZipException;

}
