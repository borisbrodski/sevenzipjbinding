package net.sf.sevenzipjbinding;

import java.util.Date;

public interface IOutItemCallbackZip extends IOutItemCallbackBase {
    /**
     * Return size of the item <code>index</code> in bytes.
     * 
     * @return size of the archive item, <code>-1</code> if not applicable (for example, for directories)
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public long getSize() throws SevenZipException;

    public Integer getAttributes() throws SevenZipException;

    public String getPath() throws SevenZipException;

    public boolean isDir() throws SevenZipException;

    public Date getModificationTime() throws SevenZipException;

    public Date getLastAccessTime() throws SevenZipException;

    public Date getCreationTime() throws SevenZipException;
}
