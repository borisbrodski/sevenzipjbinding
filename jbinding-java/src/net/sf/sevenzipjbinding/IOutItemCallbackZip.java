package net.sf.sevenzipjbinding;

import java.util.Date;

public interface IOutItemCallbackZip extends IOutItemCallbackBase {
    /**
     * Return size of the item <code>index</code> in bytes.
     * 
     * @param index
     *            index of an archive item to retrieve value from (starting from 0)
     * @return size of the archive item, <code>-1</code> if not applicable (for example, for directories)
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public long getSize(int index) throws SevenZipException;

    public Integer getAttributes(int index) throws SevenZipException;

    public String getPath(int index) throws SevenZipException;

    public boolean isDir(int index) throws SevenZipException;

    public boolean isNtfsTime(int index) throws SevenZipException;

    public Date getModificationTime(int index) throws SevenZipException;

    public Date getLastAccessTime(int index) throws SevenZipException;

    public Date getCreationTime(int index) throws SevenZipException;
}
