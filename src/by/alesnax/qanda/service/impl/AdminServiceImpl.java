package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.impl.AdminDAOImpl;
import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.service.AdminService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 *
 */
public class AdminServiceImpl implements AdminService {
    private static Logger logger = LogManager.getLogger(AdminServiceImpl.class);

    @Override
    public PaginatedList<Friend> findManagingUsers(int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> management = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = new AdminDAOImpl(connection);
            management = adminDAO.takeManagingUsers(startUser, usersPerPage);
            if(startUser > management.getTotalCount()){
                startUser = 0;
                management = adminDAO.takeManagingUsers(startUser, usersPerPage);
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
        return management;
    }

    @Override
    public PaginatedList<Ban> findAllBans(int startBan, int bansPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Ban> allCurrentBans = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = new AdminDAOImpl(connection);
            allCurrentBans = adminDAO.takeAllCurrentBans(startBan, bansPerPage);
            if(startBan > allCurrentBans.getTotalCount()){
                startBan = 0;
                allCurrentBans = adminDAO.takeAllCurrentBans(startBan, bansPerPage);
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
        return allCurrentBans;
    }

    @Override
    public PaginatedList<Complaint> findComplaints(int startComplaint, int complaintsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Complaint> complaints = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = new AdminDAOImpl(connection);
            complaints = adminDAO.takeAllComplaints(startComplaint, complaintsPerPage);
            if(startComplaint > complaints.getTotalCount()){
                startComplaint = 0;
                complaints = adminDAO.takeAllComplaints(startComplaint, complaintsPerPage);
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
    public boolean changeUserRole(String login, String role) throws ServiceException {
        WrappedConnection connection = null;
        boolean changed = false;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = new AdminDAOImpl(connection);
            changed = adminDAO.updateUserStatus(login, role);
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
        return changed;
    }

    @Override
    public void createNewCategory(int userId, String titleEn, String titleRu, String descriptionEn, String descriptionRu) throws ServiceException {
        WrappedConnection connection = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = new AdminDAOImpl(connection);
            adminDAO.addNewCategory(userId, titleEn, titleRu, descriptionEn, descriptionRu);
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
    public void closeCategory(int catId) throws ServiceException {
        WrappedConnection connection = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = new AdminDAOImpl(connection);
            adminDAO.updateCategoryStatusToClosed(catId);
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
    public boolean correctCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String login, String categoryStatus) throws ServiceException {
        WrappedConnection connection = null;
        boolean updated = false;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = new AdminDAOImpl(connection);
            updated = adminDAO.updateCategoryInfo(categoryId, titleEn, titleRu, descriptionEn, descriptionRu, login, categoryStatus);
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
        return updated;
    }
}
