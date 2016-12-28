package by.alesnax.qanda.dao.impl;

/**
 * Created by alesnax on 12.12.2016.
 */
public class DAODuplicatedInfoException extends DAOException {
    private static final long serialVersionUID = 1L;

    public DAODuplicatedInfoException(String message) {
        super(message);
    }

    public DAODuplicatedInfoException(String message, Exception e) {
        super(message, e);
    }
}
