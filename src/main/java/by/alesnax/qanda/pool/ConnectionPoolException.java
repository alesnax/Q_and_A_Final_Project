package by.alesnax.qanda.pool;

/**
 * An exception that provides information on a database access
 * error or other errors.
 *
 * @see Exception
 * @author Aliaksandr Nakhankou
 */
public class ConnectionPoolException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a <code>ConnectionPoolException</code> object with a given
     * <code>message</code>.
     *
     * @param message the underlying message for this <code>ConnectionPoolException</code>
     */
    public ConnectionPoolException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>ConnectionPoolException</code> object with a given
     * <code>message</code>, and caught SQLException.
     *
     * @param message the underlying message for this <code>ConnectionPoolException</code>
     * @param e       caught and thrown Exception
     */
    public ConnectionPoolException(String message, Exception e) {
        super(message, e);
    }

}
