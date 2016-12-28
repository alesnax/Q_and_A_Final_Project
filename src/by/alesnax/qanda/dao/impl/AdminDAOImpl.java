package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.AdminDAO;
import by.alesnax.qanda.dao.pool.WrappedConnection;
import by.alesnax.qanda.entity.User;

/**
 * Created by alesnax on 05.12.2016.
 */
public class AdminDAOImpl extends AbstractDAO <Integer, User> implements AdminDAO{
    public AdminDAOImpl(WrappedConnection connection) {
        super(connection);
    }

    @Override
    public User findEntityById(Integer id) {
        return null;
    }
}
