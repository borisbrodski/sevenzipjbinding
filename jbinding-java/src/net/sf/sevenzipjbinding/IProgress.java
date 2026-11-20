package net.sf.sevenzipjbinding;

/**
 * This interface provides progress information of a process.
 * <p>
 * <b>Threading:</b> 7-Zip-JBinding may invoke the methods of this interface on threads created by 7-Zip, for example
 * during multithreaded 7z compression or LZMA2 decompression, not only on the thread that called the binding, and
 * different items may be processed on different threads. Don't rely on the identity of the calling thread or on
 * thread-confined resources such as {@link ThreadLocal} values, a JDBC connection bound to the caller or a UI toolkit
 * thread. Such a thread gets the context class loader of the thread that called 7-Zip-JBinding, so code that resolves
 * classes or resources through it (service loaders, logging, dependency injection) works as on the calling thread.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public interface IProgress {
    /**
     * Set total amount of work
     * 
     * @param total
     *            amount of work
     * 
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There is no guarantee that no further callback methods will be called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>IInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     */
    public void setTotal(long total) throws SevenZipException;

    /**
     * Set current amount of completed work
     * 
     * @param complete
     *            amount of completed work
     * 
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There is no guarantee that no further callback methods will be called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>IInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     */
    public void setCompleted(long complete) throws SevenZipException;
}
