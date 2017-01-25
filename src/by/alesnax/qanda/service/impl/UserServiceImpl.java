package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.impl.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.dao.impl.UserDAOImpl;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.entity.UserStatistics;
import by.alesnax.qanda.service.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

/**
 * Created by alesnax on 05.12.2016.
 *
 */
public class UserServiceImpl implements UserService {
    private static Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private static Random rand = new Random();

    private static final int RANDOM_LENGTH_MIN = 15;
    private static final int RANDOM_RANGE = 15;
    private static final int CHAR_MIN_LOWERCASE = 97;
    private static final int CHAR_MIN_UPPERCASE = 65;
    private static final int CHAR_MIN_DIGIT = 48;
    private static final int CHAR_RANGE_LETTER = 26;
    private static final int CHAR_RANGE_DIGIT = 10;

    @Override
    public User findUserById(int userId, int sessionUserId) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            user = userDAO.takeUserById(userId, sessionUserId);
            if(user != null){
                UserStatistics statistics = userDAO.findUserStatistics(user.getId());
                user.setStatistics(statistics);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return user;
    }

    @Override
    public PaginatedList<Friend> findFriends(int userId, int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> friends = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            friends = userDAO.takeFriends(userId, startUser, usersPerPage);
            if(startUser > friends.getTotalCount()){
                startUser = 0;
                friends = userDAO.takeFriends(userId, startUser, usersPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return friends;
    }

    @Override
    public PaginatedList<Friend> findFollowers(int userId, int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> followers = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            followers = userDAO.takeFollowers(userId, startUser, usersPerPage);
            if(startUser > followers.getTotalCount()){
                startUser = 0;
                followers = userDAO.takeFollowers(userId, startUser, usersPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return followers;
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public User changeUserInfo(int userId, String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            user = userDAO.updateUserInfo(userId, login, name, surname, email, bDay, bMonth, bYear, sex, country, city, status, keyWordType, keyWordValue);
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public PaginatedList<Friend> findBestUsers(int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> bestUsers = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            bestUsers = userDAO.takeBestUsers(startUser, usersPerPage);
            if(startUser > bestUsers.getTotalCount()){
                startUser = 0;
                bestUsers = userDAO.takeBestUsers(startUser, usersPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return bestUsers;
    }

    @Override
    public String recoverPassword(String email, String keyWordType, String keyWordValue) throws ServiceException {
        WrappedConnection connection = null;
        String changedPassword = generateNewPassword();
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            boolean changed = userDAO.updateUserPassWordByKeyword(changedPassword, email, keyWordType, keyWordValue);
            if(!changed){
                changedPassword = null;
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return changedPassword;
    }


    @Override
    public void registerNewUser(String login, String password, String name, String surname, String email, String bDay,
                                String bMonth, String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            userDAO.registerNewAccount(login, password, name, surname, email, bDay, bMonth, bYear, sex, country, city, status, keyWordType, keyWordValue);
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
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
            if(user != null){
                UserStatistics statistics = userDAO.findUserStatistics(user.getId());
                user.setStatistics(statistics);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return user;
    }

    private String generateNewPassword() {
        StringBuilder sb = new StringBuilder();
        int passLen = RANDOM_LENGTH_MIN + rand.nextInt(RANDOM_RANGE);

        int choice = 0;
        for (int i = 0; i < passLen; i++) {
            choice = rand.nextInt(3) + 1;
            if(choice == 1){
                sb.append((char)(CHAR_MIN_UPPERCASE + rand.nextInt(CHAR_RANGE_LETTER)));
            } else if(choice == 2){
                sb.append((char)(CHAR_MIN_LOWERCASE + rand.nextInt(CHAR_RANGE_LETTER)));
            } else {
                sb.append((char)(CHAR_MIN_DIGIT + rand.nextInt(CHAR_RANGE_DIGIT)));
            }
        }

        return  sb.toString();
    }

}
