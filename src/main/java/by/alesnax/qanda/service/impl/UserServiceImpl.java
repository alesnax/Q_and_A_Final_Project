package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.DAOException;
import by.alesnax.qanda.dao.impl.DAOFactory;
import by.alesnax.qanda.dao.impl.UserDAOImpl;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.entity.UserStatistics;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.service.ServiceDuplicatedInfoException;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * UserService contains list of implemented methods to provide linking between command
 * and DAO layers. Methods processes data before calling DAO layer and process result of returned parameters
 * from DAO layer before sending back to command layer.
 *
 * @author Aliaksandr Nakhankou
 */
class UserServiceImpl implements UserService {
    private static Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private static Random rand = new Random();

    /**
     * parameters for new password generation
     */
    private static final int RANDOM_LENGTH_MIN = 15;
    private static final int RANDOM_RANGE = 15;
    private static final int CHAR_MIN_LOWERCASE = 97;
    private static final int CHAR_MIN_UPPERCASE = 65;
    private static final int CHAR_MIN_DIGIT = 48;
    private static final int CHAR_RANGE_LETTER = 26;
    private static final int CHAR_RANGE_DIGIT = 10;

    private static final String SALT = "d18740cfe2d04b014513a2c6e5f9b84b599d596d";

    /**
     * takes user info from DAO
     *
     * @param userId        id of finding user
     * @param sessionUserId id of session user
     * @return user
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public User findUserById(int userId, int sessionUserId) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            user = userDAO.takeUserById(userId, sessionUserId);
            if (user != null) {
                UserStatistics statistics = userDAO.findUserStatistics(user.getId());
                user.setStatistics(statistics);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return user;
    }

    /**
     * takes list of users who marked as following by user from DAO layer
     *
     * @param userId       id of session user
     * @param startUser    number of first item to be taken from database
     * @param usersPerPage number of items to be taken from database
     * @return container with list of users who marked as following and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Friend> findFriends(int userId, int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> friends = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            friends = userDAO.takeFollowingUsers(userId, startUser, usersPerPage);
            if (startUser > friends.getTotalCount()) {
                startUser = 0;
                friends = userDAO.takeFollowingUsers(userId, startUser, usersPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return friends;
    }

    /**
     * takes list of follower users from DAO layer
     *
     * @param userId       id of session user
     * @param startUser    number of first item to be taken from database
     * @param usersPerPage number of items to be taken from database
     * @return container with list of follower users and pagination parameters
     * @throws ServiceException ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Friend> findFollowers(int userId, int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> followers = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            followers = userDAO.takeFollowers(userId, startUser, usersPerPage);
            if (startUser > followers.getTotalCount()) {
                startUser = 0;
                followers = userDAO.takeFollowers(userId, startUser, usersPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return followers;
    }

    /**
     * send parameters of user id to DAO to be deleted from followers table
     *
     * @param removedUserId id of user to be removed from follower status
     * @param userId        id who wants to remove note about following user
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void removeUserFromFollowing(int removedUserId, int userId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            userDAO.removeUserFromFriends(removedUserId, userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    /**
     * method sends id of users to DAO to insert new note about following user
     *
     * @param followingUserId id of following user
     * @param userId          id of user who follows other
     * @throws ServiceException if exception while processing SQL query and connection will be caught or if note has already exists in the database
     */
    @Override
    public void addFollower(int followingUserId, int userId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            userDAO.addFollower(followingUserId, userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAODuplicatedInfoException e) {
            throw new ServiceDuplicatedInfoException(e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    /**
     * method send user info to DAO to be updated
     *
     * @return updated user info
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public User changeUserInfo(int userId, String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            user = userDAO.updateUserInfo(userId, login, name, surname, email, bDay, bMonth, bYear, sex, country, city, status, keyWordType, keyWordValue);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAODuplicatedInfoException e) {
            throw new ServiceDuplicatedInfoException(e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return user;
    }

    /**
     * sends old and new password to DAO layer for updating
     *
     * @param userId    id of user
     * @param password1 old password
     * @param password2 new password
     * @return true if updated, false if old password doesn't match with database password
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public boolean changePassword(int userId, String password1, String password2) throws ServiceException {
        WrappedConnection connection = null;
        boolean updated = false;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
          //  password1 = encryptPassword(password1);
          //  password2 = encryptPassword(password2);
            updated = userDAO.updatePassword(userId, password1, password2);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return updated;
    }

    /**
     * sends new path of user's avatar to DAO layer
     *
     * @param userId     user id
     * @param avatarPath path to avatar
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void uploadUserAvatar(int userId, String avatarPath) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            userDAO.updateAvatar(userId, avatarPath);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }

    }

    /**
     * sends value of updated language to DAO
     *
     * @param userId   id of user
     * @param language new value of user language
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void changeUserLanguage(int userId, String language) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            userDAO.updateUserLanguage(userId, language);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    /**
     * takes list of best users from DAO layer
     *
     * @param startUser    number of first item to be taken from database
     * @param usersPerPage number of items to be taken from database
     * @return container with list of best users and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Friend> findBestUsers(int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> bestUsers = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            bestUsers = userDAO.takeBestUsers(startUser, usersPerPage);
            if (startUser > bestUsers.getTotalCount()) {
                startUser = 0;
                bestUsers = userDAO.takeBestUsers(startUser, usersPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return bestUsers;
    }

    /**
     * method generates new password and calls DAO method for updating if key word type and its value match
     * eith value in database
     *
     * @param email        user email
     * @param keyWordType  type of word for password recovering
     * @param keyWordValue value of key word
     * @return new password if keyword password was changed, null otherwise
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public String recoverPassword(String email, String keyWordType, String keyWordValue) throws ServiceException {
        WrappedConnection connection = null;
        String changedPassword = generateNewPassword();
      //  String password = encryptPassword(changedPassword);
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            boolean changed = userDAO.updateUserPassWordByKeyword(changedPassword, email, keyWordType, keyWordValue);// temp variant
            if (!changed) {
                changedPassword = null;
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return changedPassword;
    }

    /**
     * @param userId   if of deleted user
     * @param password of deleted user
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public boolean deleteAccount(int userId, String password) throws ServiceException {
        WrappedConnection connection = null;
        boolean deleted = false;
       // password = encryptPassword(password);
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            deleted = userDAO.updateUserStateToDeleted(userId, password);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return deleted;
    }

    /**
     * sends new user info to DAO layer for creating note about user
     *
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void registerNewUser(String login, String password, String name, String surname, String email, String bDay,
                                String bMonth, String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws ServiceException {
        WrappedConnection connection = null;
       // password = encryptPassword(password);
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            userDAO.registerNewAccount(login, password, name, surname, email, bDay, bMonth, bYear, sex, country, city, status, keyWordType, keyWordValue);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAODuplicatedInfoException e) {
            throw new ServiceDuplicatedInfoException(e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    /**
     * sends user's email and password to DAO for authorisation checking
     *
     * @param email    of user
     * @param password of user
     * @return user info if password and email matchs, null otherwise
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public User userAuthorization(String email, String password) throws ServiceException {
        WrappedConnection connection = null;
        User user = null;
       // password = encryptPassword(password);
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            UserDAOImpl userDAO = DAOFactory.getInstance().getUserDAO(connection);
            user = userDAO.userAuthorization(email, password);
            if (user != null) {
                UserStatistics statistics = userDAO.findUserStatistics(user.getId());
                user.setStatistics(statistics);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return user;
    }

    /**
     * generates new password when user forget it
     *
     * @return value of new password
     */
    private String generateNewPassword() {
        StringBuilder sb = new StringBuilder();
        int passLen = RANDOM_LENGTH_MIN + rand.nextInt(RANDOM_RANGE);

        for (int i = 0; i < passLen; i++) {
            int choice = rand.nextInt(3) + 1;
            if (choice == 1) {
                sb.append((char) (CHAR_MIN_UPPERCASE + rand.nextInt(CHAR_RANGE_LETTER)));
            } else if (choice == 2) {
                sb.append((char) (CHAR_MIN_LOWERCASE + rand.nextInt(CHAR_RANGE_LETTER)));
            } else {
                sb.append((char) (CHAR_MIN_DIGIT + rand.nextInt(CHAR_RANGE_DIGIT)));
            }
        }

        return sb.toString();
    }

    /**
     * encrypt password with using salt and double sha1 encrypting
     *
     * @return value of encrypted password
     */
    private String encryptPassword(String password) {
        return DigestUtils.sha1Hex(DigestUtils.sha1Hex(password + SALT));
    }
}
