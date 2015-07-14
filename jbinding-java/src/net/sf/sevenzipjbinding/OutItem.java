package net.sf.sevenzipjbinding;

public abstract class OutItem {
    private ISequentialInStream stream;

    /**
     * Return sequential in-stream for the archive item to read and compress the content.<br>
     * <br>
     * <i>Note:</i> {@link ISequentialInStream} interface doesn't require closing, but in most real world applications a
     * special care should be taken to properly close corresponding implementation objects.
     * 
     * @return sequential in-stream
     */
    public ISequentialInStream getStream() {
        return stream;
    }

    /**
     * Set sequential in-stream for the archive item to read and compress the content.<br>
     * <br>
     * <i>Note:</i> {@link ISequentialInStream} interface doesn't require closing, but in most real world applications a
     * special care should be taken to properly close corresponding implementation objects.
     * 
     * @param stream
     *            sequential in-stream
     */
    public void setStream(ISequentialInStream stream) {
        this.stream = stream;
    }

}
