package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.impl.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.dao.impl.UserDAOImpl;
import by.alesnax.qanda.dao.pool.ConnectionPool;
import by.alesnax.qanda.dao.pool.ConnectionPoolException;
import by.alesnax.qanda.dao.pool.WrappedConnection;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.service.UserService;


/**
 * Created by alesnax on 05.12.2016.
 */
public class UserServiceImpl implements UserService {


    @Override
    public User findUserById(int userId) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
        try {

            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            user = userDAO.findEntityById(userId);
            if (user != null) {
                // добавление постов записей и так далее иначе просто нулевой юзер
            }
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
            if (user != null) {
                // добавление постов записей и так далее иначе просто нулевой юзер
            }
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
