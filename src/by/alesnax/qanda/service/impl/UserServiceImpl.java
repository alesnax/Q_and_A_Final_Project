package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.impl.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.dao.impl.UserDAOImpl;
import by.alesnax.qanda.dao.pool.ConnectionPool;
import by.alesnax.qanda.dao.pool.ConnectionPoolException;
import by.alesnax.qanda.dao.pool.WrappedConnection;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.service.UserService;

import java.util.List;


/**
 * Created by alesnax on 05.12.2016.
 */
public class UserServiceImpl implements UserService {


    @Override
    public User findUserById(int userId, int sessionUserId) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            user = userDAO.takeUserById(userId, sessionUserId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return user;
    }

    @Override
    public List<Friend> findFriends(int userId) throws ServiceException {
        WrappedConnection connection = null;
        List<Friend> friends = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            friends = userDAO.takeFriends(userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return friends;
    }

    @Override
    public void removeUserFromFollowing(int removedUserId, int userId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            userDAO.removeUserFromFriends(removedUserId, userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public void addFollower(int followingUserId, int userId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            userDAO.addFollower(followingUserId, userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAODuplicatedInfoException e) {
            throw new ServiceDuplicatedInfoException(e.getMessage(), e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public User changeUserInfo(int userId, String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            user = userDAO.updateUserInfo(userId, login, name, surname, email, bDay, bMonth, bYear, sex, country, city, status);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAODuplicatedInfoException e) {
            throw new ServiceDuplicatedInfoException(e.getMessage(), e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return user;
    }

    @Override
    public boolean changePassword(int userId, String password1, String password2) throws ServiceException {
        WrappedConnection connection = null;
        boolean updated = false;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            updated = userDAO.updatePassword(userId, password1, password2);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        }  catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return updated;
    }

    @Override
    public void uploadUserAvatar(int userId, String avatarPath) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            userDAO.updateAvatar(userId, avatarPath);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        }  catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }

    }

    @Override
    public void changeUserLanguage(int userId, String language) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            userDAO.updateUserLanguage(userId, language);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        }  catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public void registerNewUser(String login, String password, String name, String surname, String email, String bDay,
                                String bMonth, String bYear, String sex, String country, String city, String status) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            userDAO.registerNewAccount(login, password, name, surname, email, bDay, bMonth, bYear, sex, country, city, status);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAODuplicatedInfoException e) {
            throw new ServiceDuplicatedInfoException(e.getMessage(), e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public User userAuthorization(String email, String password) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            user = userDAO.userAuthorization(email, password);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return user;
    }
}
