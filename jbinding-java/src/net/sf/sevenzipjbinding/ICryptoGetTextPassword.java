package net.sf.sevenzipjbinding;

/**
 * Interface to provide password to the archive engine
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public interface ICryptoGetTextPassword {
    /**
     * Returns password
     * 
     * @return password
     * 
     * 
     * @throws SevenZipException
     *             in error case. If this method ends with an exception, the current operation will be reported to 7-Zip
     *             as failed. There are no guarantee, that there are no further call back methods will be called. The
     *             first thrown exception will be saved and thrown late on from the first called 7-Zip-JBinding main
     *             method, such as <code>ISevenZipInArchive.extract()</code> or <code>SevenZip.openInArchive()</code>.
     */
    public String cryptoGetTextPassword() throws SevenZipException;
}
