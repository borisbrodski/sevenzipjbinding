package net.sf.sevenzipjbinding;

/**
 * Interface used to provide a password to the archive engine (for opening or extracting encrypted archives).
 * 
 * @author Boris Brodski
 * @since 4.65-1
 */
public interface ICryptoGetTextPassword {
    /**
     * Returns the password to be used by the archive engine.
     *
     * @return the password
     *
     * 
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There is no guarantee that no further callback methods will be called. The
     *             first and last thrown exceptions will be saved and thrown later on from the originally called method
     *             such as <code>IInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>. Up to
     *             four exceptions depending on the situation can be saved for further analysis. See
     *             {@link SevenZipException} and {@link SevenZipException#printStackTraceExtended()} for details.
     */
    public String cryptoGetTextPassword() throws SevenZipException;
}
