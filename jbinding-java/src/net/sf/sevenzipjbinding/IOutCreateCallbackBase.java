package net.sf.sevenzipjbinding;

/**
 * Base interface to provide information needed to create a new archive or to update existing archive.
 * 
 * @see IOutCreateCallback
 * @see IOutUpdateCallback
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutCreateCallbackBase extends IProgress {

    /**
     * Notify about last archive create/update operation result.
     * 
     * @param operationResultOk
     *            <code>true</code> - last archive create/update operation was a success, <code>false</code> - last
     *            archive update operation failed.
     * @throws SevenZipException
     *             7-Zip or 7-Zip-JBinding error. Check exception message for more information.
     */
    public void setOperationResult(boolean operationResultOk) throws SevenZipException;
}
