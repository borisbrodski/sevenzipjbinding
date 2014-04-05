package net.sf.sevenzipjbinding;

import java.util.Date;

public interface IOutItemCallbackGZip extends IOutItemCallbackBase {
    public long getSize() throws SevenZipException;

    public String getPath() throws SevenZipException;

    public Date getModificationTime() throws SevenZipException;

}
