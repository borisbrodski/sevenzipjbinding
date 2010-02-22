package net.sf.sevenzipjbinding;

/**
 * SevenZip core exception.
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public class SevenZipException extends Exception {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final long serialVersionUID = 42L;

    private Throwable causeLastThrown;
    private Throwable causeFirstPotentialThrown;
    private Throwable causeLastPotentialThrown;

    /**
     * Constructs a new exception with <code>null</code> as its detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause(Throwable)}.
     */
    public SevenZipException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this
     * exception's detail message.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt>
     *            value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 1.4
     */
    public SevenZipException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to {@link #initCause(Throwable)}.
     * 
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *            method.
     */
    public SevenZipException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail message of
     * <tt>cause</tt>). This constructor is useful for exceptions that are little more than wrappers for other
     * throwables (for example, <code>PrivilegedActionException</code>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt>
     *            value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 1.4
     */
    public SevenZipException(Throwable cause) {
        super(cause);
    }

    /**
     * This setter will be used by native code
     */
    @SuppressWarnings("unused")
    private void setCauseLastThrown(Throwable causeLastThrown) {
        this.causeLastThrown = causeLastThrown;
    }

    /**
     * This setter will be used by native code
     */
    @SuppressWarnings("unused")
    private void setCauseFirstPotentialThrown(Throwable causeFirstPotentialThrown) {
        this.causeFirstPotentialThrown = causeFirstPotentialThrown;
    }

    /**
     * This setter will be used by native code
     */
    @SuppressWarnings("unused")
    private void setCauseLastPotentialThrown(Throwable causeLastPotentialThrown) {
        this.causeLastPotentialThrown = causeLastPotentialThrown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        printToStringBuilder("", stringBuilder);
        return stringBuilder.toString();
    }

    private void printToStringBuilder(String prefix, StringBuilder stringBuilder) {
        String message = super.getMessage();
        if (message == null) {
            stringBuilder.append("No message");
        } else {
            stringBuilder.append(message);
        }

        Throwable causeFirstThrown = getCause();

        if (causeFirstThrown != null) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(prefix);
            stringBuilder.append("Caused by (first thrown): ");
            printMessageToStringBuilder(prefix, stringBuilder, causeFirstThrown);
        }

        if (causeLastThrown != null) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(prefix);
            stringBuilder.append("Caused by (last thrown): ");
            printMessageToStringBuilder(prefix, stringBuilder, causeLastThrown);
        }

        if (causeFirstPotentialThrown != null) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(prefix);
            stringBuilder.append("Caused by (first potential thrown): ");
            printMessageToStringBuilder(prefix, stringBuilder, causeFirstPotentialThrown);
        }

        if (causeLastPotentialThrown != null) {
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(prefix);
            stringBuilder.append("Caused by (last potential thrown): ");
            printMessageToStringBuilder(prefix, stringBuilder, causeLastPotentialThrown);
        }
    }

    private void printMessageToStringBuilder(String prefix, StringBuilder stringBuilder, Throwable causeLastThrown) {
        if (causeLastThrown instanceof SevenZipException) {
            ((SevenZipException) causeLastThrown).printToStringBuilder(prefix + "  ", stringBuilder);
        } else {
            stringBuilder.append(causeLastThrown.getMessage());
        }
    }

    /**
     * Get first thrown exception as a cause for this exception.
     */
    @Override
    public Throwable getCause() {
        return super.getCause();
    }

    /**
     * Get last thrown exception as a cause for this exception.
     * 
     * @return <code>null</code> if no other (different to {@link #getCause()}) exception known as a cause for this
     *         exception.
     */
    public Throwable getCauseLastThrown() {
        return causeLastThrown;
    }

    /**
     * Get the last thrown exception that was the potential cause for this exception. The potential cause is an
     * exception thrown in another thread where it's not possible to determine whether the exception has caused the
     * current exception or not.
     * 
     * @return <code>null</code> if no such exception was thrown.
     */
    public Throwable getCauseLastPotentialThrown() {
        return causeLastPotentialThrown;
    }

    /**
     * Get the first thrown exception that was the potential cause for this exception. The potential cause is an
     * exception thrown in another thread where it's not possible to determine whether the exception has caused the
     * current exception or not.
     * 
     * @return <code>null</code> if no such exception was thrown.
     */
    public Throwable getCauseFirstPotentialThrown() {
        return causeFirstPotentialThrown;
    }
}
