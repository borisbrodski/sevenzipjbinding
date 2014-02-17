package net.sf.sevenzipjbinding;

/**
 * Default (empty) implementation of the {@link IArchiveUpdateCallback}.<br/>
 * <br/>
 * The concept of adapters allows compact implementations of the interfaces. Inheriting from an adapter you can
 * implement only essential methods leaving all other methods with the default implementation.
 * 
 * TODO Implement adapters for almost all other interfaces
 * 
 * @author Boris Brodski
 * 
 */
public class ArchiveUpdateCallbackAdapter implements IArchiveUpdateCallback {

    /**
     * {@inheritDoc}
     */
    public Object getProperty(int index, PropID propID) throws SevenZipException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ISequentialInStream getStream(int index) throws SevenZipException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setOperationResult(boolean operationResultOk) throws SevenZipException {
    }

    /**
     * {@inheritDoc}
     */
    public void setTotal(long total) throws SevenZipException {
    }

    /**
     * {@inheritDoc}
     */
    public void setCompleted(long completeValue) throws SevenZipException {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNewData(int index) throws SevenZipException {
        return false;
    }

    public boolean isNewProperties(int index) throws SevenZipException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int getOldArchiveItemIndex(int index) throws SevenZipException {
        return -1;
    }
}
