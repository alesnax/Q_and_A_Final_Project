package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.dao.impl.ModeratorDAOImpl;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.service.ModeratorService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by alesnax on 17.12.2016.
 *
 */
public class ModeratorServiceImpl implements ModeratorService {
    private static Logger logger = LogManager.getLogger(ModeratorServiceImpl.class);


    @Override
    public PaginatedList<Friend> findAllUsers(int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> allUsers = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = new ModeratorDAOImpl(connection);
            allUsers = moderatorDAO.takeAllUsers(startUser, usersPerPage);
            if(startUser > allUsers.getTotalCount()){
                startUser = 0;
                allUsers = moderatorDAO.takeAllUsers(startUser, usersPerPage);
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
        return allUsers;
    }

    @Override
    public PaginatedList<Ban> findBannedUsersById(int userId, int startBan, int bansPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Ban> currentBans = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = new ModeratorDAOImpl(connection);
            currentBans = moderatorDAO.takeCurrentBansByModeratorId(userId, startBan, bansPerPage);
            if(startBan > currentBans.getTotalCount()){
                startBan = 0;
                currentBans = moderatorDAO.takeCurrentBansByModeratorId(userId, startBan, bansPerPage);
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
        return currentBans;

    }

    @Override
    public void stopUserBan(int banId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = new ModeratorDAOImpl(connection);
            moderatorDAO.updateBansStatusFinished(banId);
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
    public PaginatedList<Complaint> findComplaintsByModeratorId(int userId, int startComplaint, int complaintsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Complaint> complaints = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = new ModeratorDAOImpl(connection);
            complaints = moderatorDAO.takeComplaintsByModeratorId(userId, startComplaint, complaintsPerPage);
            if(startComplaint > complaints.getTotalCount()){
                startComplaint = 0;
                complaints = moderatorDAO.takeComplaintsByModeratorId(userId, startComplaint, complaintsPerPage);
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
        return complaints;
    }

    @Override
    public void addComplaintDecision(int moderatorId, int complaintPostId, int complaintAuthorId, String decision, int status) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = new ModeratorDAOImpl(connection);
            if(status == 0){
                moderatorDAO.updateComplaintStatusToApproved(moderatorId, complaintPostId, complaintAuthorId, decision);
            } else if(status == 1){
                moderatorDAO.updateComplaintStatusToCancelled(moderatorId, complaintPostId, complaintAuthorId, decision);
            } else {
                throw new ServiceException("Wrong expected parameter of complaint status!");
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
    }

    @Override
    public void correctCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String categoryStatus) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            ModeratorDAOImpl moderatorDAO = new ModeratorDAOImpl(connection);
            moderatorDAO.updateCategoryInfo(categoryId, titleEn, titleRu, descriptionEn, descriptionRu, categoryStatus);
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

}
