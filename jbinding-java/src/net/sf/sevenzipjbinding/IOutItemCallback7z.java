package net.sf.sevenzipjbinding;

import java.util.Date;

public interface IOutItemCallback7z extends IOutItemCallbackBase {
    /**
     * @see PropID#IS_ANTI
     * 
     * @return <code>false</code>, if not sure
     * 
     * @throws SevenZipException
     */
    public boolean isAnti() throws SevenZipException;

    public long getSize() throws SevenZipException;

    public String getPath() throws SevenZipException;

    public Integer getAttributes() throws SevenZipException;

    public Date getModificationTime() throws SevenZipException;

    public boolean isDir() throws SevenZipException;
}
