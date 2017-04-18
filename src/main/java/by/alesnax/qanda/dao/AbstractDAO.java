package by.alesnax.qanda.dao;

import by.alesnax.qanda.entity.Entity;
import by.alesnax.qanda.pool.WrappedConnection;

/**
 * The root class in the DAO hierarchy. DAO classes processes operations of
 * manipulating with information that stores in databases. Methods of classes sends
 * SQL statements to the database and get result as ResultSet of objects or number of
 * processed rows in database.
 *
 * @param <K> the id of manipulated entity
 * @param <T> type of returned elements
 * @author Aliaksandr Nakhankou
 */
public abstract class AbstractDAO<K, T extends Entity> {

    /**
     * A connection (session) with a specific
     * database. SQL statements are executed and results are returned
     * within the context of a connection.
     */
    protected WrappedConnection connection;

    /**
     * Constructs DAO class
     */
    protected AbstractDAO() {
    }

    public void setConnection(WrappedConnection connection) {
        this.connection = connection;
    }

    /**
     * should be implemented for sending SQL statement that searches for object with
     * definite id
     *
     * @param id entity id
     * @return found out entity or null if object wasn't found
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    public abstract T findEntityById(K id) throws DAOException;

}
