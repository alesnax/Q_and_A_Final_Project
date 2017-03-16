package by.alesnax.qanda.dao;

/**
 * Thrown to indicate that normal processing of SQL statement was
 * interrupted or SQL connection problems were occurred.
 *
 * @author  Aliaksandr Nakhankou
 * @see Exception
 */
public class DAOException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a <code>DAOException</code> with the
     * specified detail message.
     *
     * @param   message   the detail message.
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>DAOException</code> with the
     * specified detail message and caught exception.
     *
     * @param   message   the detail message.
     * @param e is thrown exception
     */
    public DAOException(String message, Exception e) {
        super(message, e);
    }

}
