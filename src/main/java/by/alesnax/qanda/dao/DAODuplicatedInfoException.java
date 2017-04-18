package by.alesnax.qanda.dao;

/**
 * Thrown to indicate that row with definite
 * key or unique attributes has already existed in database.
 *
 * @author  Aliaksandr Nakhankou
 * @see DAOException
 */
public class DAODuplicatedInfoException extends DAOException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a <code>DAODuplicatedInfoException</code> with the
     * specified detail message.
     *
     * @param   message   the detail message.
     */
    public DAODuplicatedInfoException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>DAODuplicatedInfoException</code> with the
     * specified detail message and caught exception.
     *
     * @param   message   the detail message.
     * @param e is thrown exception
     */
    public DAODuplicatedInfoException(String message, Exception e) {
        super(message, e);
    }
}
