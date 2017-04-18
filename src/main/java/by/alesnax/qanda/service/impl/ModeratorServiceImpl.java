package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.DAOException;
import by.alesnax.qanda.dao.impl.DAOFactory;
import by.alesnax.qanda.dao.impl.ModeratorDAOImpl;
import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class contains list of implemented methods to provide linking between command
 * and DAO layers. Methods processes data before calling DAO layer and process result of returned parameters
 * from DAO layer before sending back to command layer.
 * Methods processes operations related with user with MODERATOR or ADMIN role.
 *
 * @author Aliaksandr Nakhankou
 */
class ModeratorServiceImpl implements ModeratorService {
    private static Logger logger = LogManager.getLogger(ModeratorServiceImpl.class);

    /**
     * method takes info about users from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param startUser    number of first item to be taken from database
     * @param usersPerPage number of items to be taken from database
     * @return container with list of users and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Friend> findAllUsers(int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> allUsers = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = DAOFactory.getInstance().getModeratorDAO(connection);
            allUsers = moderatorDAO.takeAllUsers(startUser, usersPerPage);
            if (startUser > allUsers.getTotalCount()) {
                startUser = 0;
                allUsers = moderatorDAO.takeAllUsers(startUser, usersPerPage);
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
        return allUsers;
    }

    /**
     * method takes list of bans by definite moderator from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param userId      id of moderator who banned users
     * @param startBan    number of first item to be taken from database
     * @param bansPerPage number of items to be taken from database
     * @return container with list of bans and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Ban> findBannedUsersById(int userId, int startBan, int bansPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Ban> currentBans = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = DAOFactory.getInstance().getModeratorDAO(connection);
            currentBans = moderatorDAO.takeCurrentBansByModeratorId(userId, startBan, bansPerPage);
            if (startBan > currentBans.getTotalCount()) {
                startBan = 0;
                currentBans = moderatorDAO.takeCurrentBansByModeratorId(userId, startBan, bansPerPage);
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
        return currentBans;

    }

    /**
     * method send to DAO method id of ban for updating date of ban end to current date
     *
     * @param banId id of updating ban
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void stopUserBan(int banId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = DAOFactory.getInstance().getModeratorDAO(connection);
            moderatorDAO.updateBansStatusFinished(banId);
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
     * @param userId            moderator id
     * @param startComplaint    number of first item to be taken from database
     * @param complaintsPerPage number of items to be taken from database
     * @return container with list of complaints and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Complaint> findComplaintsByModeratorId(int userId, int startComplaint, int complaintsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Complaint> complaints = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = DAOFactory.getInstance().getModeratorDAO(connection);
            complaints = moderatorDAO.takeComplaintsByModeratorId(userId, startComplaint, complaintsPerPage);
            if (startComplaint > complaints.getTotalCount()) {
                startComplaint = 0;
                complaints = moderatorDAO.takeComplaintsByModeratorId(userId, startComplaint, complaintsPerPage);
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
        return complaints;
    }

    /**
     * sends to DAO method parameters of complaint decision
     *
     * @throws ServiceException if exception while processing SQL query and connection will be caught or wrong status param
     */
    @Override
    public void addComplaintDecision(int moderatorId, int complaintPostId, int complaintAuthorId, String decision, int status) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = DAOFactory.getInstance().getModeratorDAO(connection);
            if (status == 0) {
                moderatorDAO.updateComplaintStatusToApproved(moderatorId, complaintPostId, complaintAuthorId, decision);
            } else if (status == 1) {
                moderatorDAO.updateComplaintStatusToCancelled(moderatorId, complaintPostId, complaintAuthorId, decision);
            } else {
                throw new ServiceException("Wrong expected parameter of complaint status!");
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
    }

    /**
     * sends category data to DAO method to update category info
     *
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void correctCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String categoryStatus) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = DAOFactory.getInstance().getModeratorDAO(connection);
            moderatorDAO.updateCategoryInfo(categoryId, titleEn, titleRu, descriptionEn, descriptionRu, categoryStatus);
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
}
