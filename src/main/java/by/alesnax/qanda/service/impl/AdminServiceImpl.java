package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.DAOException;
import by.alesnax.qanda.dao.impl.AdminDAOImpl;
import by.alesnax.qanda.dao.impl.DAOFactory;
import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class contains list of implemented methods to provide linking between command
 * and DAO layers. Methods processes data before calling DAO layer and process result of returned parameters
 * from DAO layer before sending back to command layer.
 * Methods processes operations related with user with ADMIN role.
 *
 * @author Aliaksandr Nakhankou
 */
class AdminServiceImpl implements AdminService {
    private static Logger logger = LogManager.getLogger(AdminServiceImpl.class);

    /**
     * method takes managing users from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param startUser    number of first item to be taken from database
     * @param usersPerPage number of items to be taken from database
     * @return container with list of admins and moderators and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Friend> findManagingUsers(int startUser, int usersPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Friend> management = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = DAOFactory.getInstance().getAdminDAO(connection);
            management = adminDAO.takeManagingUsers(startUser, usersPerPage);
            if (startUser > management.getTotalCount()) {
                startUser = 0;
                management = adminDAO.takeManagingUsers(startUser, usersPerPage);
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
        return management;
    }

    /**
     * method takes user bans from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param startBan    number of first item to be taken from database
     * @param bansPerPage number of items to be taken from database
     * @return container with list of user bans and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Ban> findAllBans(int startBan, int bansPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Ban> allCurrentBans = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = DAOFactory.getInstance().getAdminDAO(connection);
            allCurrentBans = adminDAO.takeAllCurrentBans(startBan, bansPerPage);
            if (startBan > allCurrentBans.getTotalCount()) {
                startBan = 0;
                allCurrentBans = adminDAO.takeAllCurrentBans(startBan, bansPerPage);
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
        return allCurrentBans;
    }

    /**
     * method takes complaints from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param startComplaint    number of first item to be taken from database
     * @param complaintsPerPage number of items to be taken from database
     * @return container with list of user complaints and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Complaint> findComplaints(int startComplaint, int complaintsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Complaint> complaints = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = DAOFactory.getInstance().getAdminDAO(connection);
            complaints = adminDAO.takeAllComplaints(startComplaint, complaintsPerPage);
            if (startComplaint > complaints.getTotalCount()) {
                startComplaint = 0;
                complaints = adminDAO.takeAllComplaints(startComplaint, complaintsPerPage);
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
     * method calls DAO method for updating user role
     *
     * @param login of user for changing role
     * @param role  new user's role
     * @return true if user role was changed, false otherwise
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public boolean changeUserRole(String login, String role) throws ServiceException {
        WrappedConnection connection = null;
        boolean changed = false;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = DAOFactory.getInstance().getAdminDAO(connection);
            changed = adminDAO.updateUserStatus(login, role);
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
        return changed;
    }

    /**
     * method calls method from DAO for adding new category data
     *
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void createNewCategory(int userId, String titleEn, String titleRu, String descriptionEn, String descriptionRu) throws ServiceException {
        WrappedConnection connection = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = DAOFactory.getInstance().getAdminDAO(connection);
            adminDAO.addNewCategory(userId, titleEn, titleRu, descriptionEn, descriptionRu);
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
     * method calls DAO method to update category status to 'closed'
     *
     * @param catId id of category
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void closeCategory(int catId) throws ServiceException {
        WrappedConnection connection = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = DAOFactory.getInstance().getAdminDAO(connection);
            adminDAO.updateCategoryStatusToClosed(catId);
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
     * @return true if category updated, false otherwise
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public boolean correctCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String login, String categoryStatus) throws ServiceException {
        WrappedConnection connection = null;
        boolean updated = false;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            AdminDAOImpl adminDAO = DAOFactory.getInstance().getAdminDAO(connection);
            updated = adminDAO.updateCategoryInfo(categoryId, titleEn, titleRu, descriptionEn, descriptionRu, login, categoryStatus);
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
}
