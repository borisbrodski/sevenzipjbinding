package net.sf.sevenzipjbinding;

public interface IOutItemCallbackBZip2 extends IOutItemCallbackBase {
    /**
     * Get property {@link PropID#SIZE} of the created or updated archive item. See {@link PropID#SIZE} for details.
     * 
     * @see PropID#SIZE
     * @return size
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public long getSize() throws SevenZipException;
}
