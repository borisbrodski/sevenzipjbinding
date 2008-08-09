package net.sf.sevenzip;

/**
 * Interface to provide password to the archive engine
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public interface ICryptoGetTextPassword {
	/**
	 * Returns password
	 * 
	 * @return password
	 * 
	 * @throws SevenZipException
	 *             7-Zip or 7-Zip-JBinding intern error occur. Check exception
	 *             message for more information.
	 */
	public String cryptoGetTextPassword() throws SevenZipException;
}
