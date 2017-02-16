package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.pool.WrappedConnection;

/**
 * Singleton that returns instance for getting implementations of DAO classes
 *
 * @author Aliaksandr Nakhankou
 */

public class DAOFactory {
    private static final DAOFactory INSTANCE = new DAOFactory();

    private AdminDAOImpl adminDAO = new AdminDAOImpl();
    private ModeratorDAOImpl moderatorDAO = new ModeratorDAOImpl();
    private PostDAOImpl postDAO = new PostDAOImpl();
    private UserDAOImpl userDAO = new UserDAOImpl();

    public static DAOFactory getInstance() {
        return INSTANCE;
    }

    public AdminDAOImpl getAdminDAO(WrappedConnection connection) {
        adminDAO.setConnection(connection);
        return adminDAO;
    }

    public ModeratorDAOImpl getModeratorDAO(WrappedConnection connection) {
        moderatorDAO.setConnection(connection);
        return moderatorDAO;
    }

    public PostDAOImpl getPostDAO(WrappedConnection connection) {
        postDAO.setConnection(connection);
        return postDAO;
    }

    public UserDAOImpl getUserDAO(WrappedConnection connection) {
        userDAO.setConnection(connection);
        return userDAO;
    }
}
