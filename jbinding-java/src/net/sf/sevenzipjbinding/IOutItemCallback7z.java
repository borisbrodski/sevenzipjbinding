package net.sf.sevenzipjbinding;

import java.util.Date;

public interface IOutItemCallback7z extends IOutItemCallbackBase {
    public boolean isAnti() throws SevenZipException;

    public long getSize() throws SevenZipException;

    public String getPath() throws SevenZipException;

    public Integer getAttributes() throws SevenZipException;

    public Date getModificationTime() throws SevenZipException;

    public boolean isDir() throws SevenZipException;
}
