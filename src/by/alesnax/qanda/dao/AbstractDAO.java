package by.alesnax.qanda.dao;

import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.dao.pool.WrappedConnection;
import by.alesnax.qanda.entity.Entity;

/**
 * Created by alesnax on 04.12.2016.
 */
public abstract class AbstractDAO<K, T extends Entity> {
    protected WrappedConnection connection;

    public AbstractDAO(WrappedConnection connection) {
        this.connection = connection;
    }

    public abstract T findEntityById(K id) throws DAOException;

}
