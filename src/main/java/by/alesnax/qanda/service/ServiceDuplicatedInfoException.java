package by.alesnax.qanda.service;

/**
 * Thrown to indicate that entity with definite
 * parameters has already existed in database.
 *
 * @author Aliaksandr Nakhankou
 * @see ServiceException
 */
public class ServiceDuplicatedInfoException extends ServiceException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a <code>ServiceDuplicatedInfoException</code> with the
     * specified detail message.
     *
     * @param message the detail message.
     */
    public ServiceDuplicatedInfoException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>ServiceDuplicatedInfoException</code> with the
     * specified detail message and caught exception.
     *
     * @param message the detail message.
     * @param e       is thrown exception
     */
    public ServiceDuplicatedInfoException(String message, Exception e) {
        super(message, e);
    }

    /**
     * Constructs a <code>ServiceDuplicatedInfoException</code> with caught exception.
     *
     * @param e is thrown exception
     */
    public ServiceDuplicatedInfoException(Exception e) {
        super(e);
    }
}
