package net.sf.sevenzipjbinding;

import java.util.Date;

public interface IOutItemCallbackTar extends IOutItemCallbackBase {
    public Integer getPosixAttributes() throws SevenZipException;

    public String getPath() throws SevenZipException;

    public boolean isDir() throws SevenZipException;

    public Date getModificationTime() throws SevenZipException;

    public String getUser() throws SevenZipException;

    public String getGroup() throws SevenZipException;

    public long getSize() throws SevenZipException;
}
