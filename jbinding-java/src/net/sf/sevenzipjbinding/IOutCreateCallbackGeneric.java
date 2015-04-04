package net.sf.sevenzipjbinding;

/**
 * Interface to provide information needed to create a new archive. This is the generic version querying item properties
 * through the {@link #getProperty(int, PropID)} method.
 * 
 * @author Boris Brodski
 * @version 9.04-2.00
 */
public interface IOutCreateCallbackGeneric extends IOutCreateCallbackBase {
    public Object getProperty(int index, PropID propID) throws SevenZipException;

}
