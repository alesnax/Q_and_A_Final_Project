package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.AdminDAO;
import by.alesnax.qanda.dao.DAOException;
import by.alesnax.qanda.entity.*;
import by.alesnax.qanda.pagination.PaginatedList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements AdminDAO interface and extends AbstractDAO class.
 * Implements all methods of AdminDAO. Processes operations of
 * manipulating with information that stores in databases and  related with session user role 'ADMIN'.
 * Methods of classes sends SQL statements to the database and
 * get result as ResultSet of objects or number of processed rows in database.
 *
 * @author Aliaksandr Nakhankou
 * @see by.alesnax.qanda.dao.AbstractDAO
 * @see by.alesnax.qanda.dao.AdminDAO
 */
public class AdminDAOImpl extends AbstractDAO<Integer, User> implements AdminDAO {
    private static Logger logger = LogManager.getLogger(AdminDAOImpl.class);

    /**
     * SQL query that selects users' information and their rates where user's roles are ADMIN or MODERATOR,
     * finds limited list of users.
     */
    private static final String SQL_SELECT_MANAGING_USERS = "SELECT sql_calc_found_rows users.id AS user_id, users.surname, users.name,  " +
            "users.avatar, users.role, users.login, users.status AS u_status, AVG(rates.value) AS rate\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=users.id)\n" +
            "WHERE users.state = 'active' AND users.role!='user' GROUP BY users.id ORDER BY role, surname, name LIMIT ?,?;";

    /**
     * SQL query that selects all users, that are currently have banned status, selects limited list of blocked users
     */
    private static final String SQL_SELECT_ALL_CURRENTLY_BANNED_USERS = "SELECT sql_calc_found_rows bans.id AS ban_id, bans.cause, bans.posts_id, bans.start, bans.end, " +
            "users.id AS user_id, users.avatar, users.role, users.login,  \n" +
            "bans.users_admin_id AS moderator_id, admins.login AS moderator_login, admins.avatar AS moderator_avatar, admins.role AS moderator_role\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id JOIN bans ON (users.id = bans.users_id AND current_timestamp() < bans.end) \n" +
            "JOIN users AS admins ON admins.id=bans.users_admin_id\n" +
            "WHERE users.state = 'active' GROUP BY users.id ORDER BY bans.end DESC LIMIT ?,?;";

    /**
     * SQL query that selects information about user complaints, processing moderator, selects limited list of complaints
     */
    private static final String SQL_SELECT_ALL_COMPLAINTS = "SELECT sql_calc_found_rows posts_id, users_id, authors.login, authors.avatar, authors.role,  description, published_time, \n" +
            "complaints.status, processed_time, decision, moderator_id, coalesce(moder.login, 0) AS moderator_login, moder.role AS moderator_role, moder.avatar AS moderator_avatar \n" +
            "FROM complaints LEFT JOIN users AS authors ON users_id=authors.id LEFT JOIN users AS moder ON moderator_id=moder.id\n" +
            "ORDER BY published_time DESC LIMIT ?,?;";

    /**
     * SQL query that updates role of user, except ADMIN role
     */
    private static final String SQL_UPDATE_USER_ROLE = "UPDATE users SET role=? WHERE login=?  AND role!='admin';";

    /**
     * SQL query that updates status of category to CLOSED by category id
     */
    private static final String SQL_UPDATE_CATEGORY_TO_CLOSED = "UPDATE categories SET status='closed' WHERE id=?;";

    /**
     * SQL query that inserts new category
     */
    private static final String SQL_INSERT_NEW_CATEGORY = "INSERT INTO categories (`users_id`, `title_en`, `title_ru`, `description_en`, `description_ru`) VALUES (?, ?, ?, ?, ?);";

    /**
     * SQL query that selects number of found rows while previous statement.
     * Statement should be located in one transaction with previous one.
     */
    private static final String SQL_SELECT_FOUND_ROWS = "SELECT FOUND_ROWS();";

    /**
     * SQL query that selects user id by login, checks if user with such login exists, for updating category in
     * the same transaction
     */
    private static final String SQL_SELECT_USER_ID_BY_LOGIN = "SELECT id FROM users WHERE login=? AND role!='user';";

    /**
     * SQL query for updating information about category by user with ADMIN role
     */
    private static final String SQL_UPDATE_CATEGORY_INFO = "UPDATE categories SET users_id=(SELECT id FROM users WHERE login=?), title_en=?, title_ru=?, description_en=?, description_ru=?, status=? WHERE `id`=?;";

    /**
     * Names of attributes processing in SQL statements related with user information
     */
    private static final String USER_ID = "user_id";
    private static final String LOGIN = "login";
    private static final String SURNAME = "surname";
    private static final String NAME = "name";
    private static final String ROLE = "role";
    private static final String AVATAR = "avatar";
    private static final String USER_STATUS = "u_status";
    private static final String USER_RATE = "rate";

    /**
     * Names of attributes processing in SQL statements related with complaints, bans and moderator information
     */
    private static final String BAN_ID = "ban_id";
    private static final String CAUSE = "cause";
    private static final String POST_ID = "posts_id";
    private static final String START = "start";
    private static final String END = "end";
    private static final String MODERATOR_ID = "moderator_id";
    private static final String MODERATOR_LOGIN = "moderator_login";
    private static final String MODERATOR_AVATAR = "moderator_avatar";
    private static final String MODERATOR_ROLE = "moderator_role";
    private static final String DESCRIPTION = "description";
    private static final String PUBLISHED_TIME = "published_time";
    private static final String PROCESSED_TIME = "processed_time";
    private static final String DECISION = "decision";
    private static final String COMPLAINT_STATUS = "status";
    private static final String AUTHOR_ID = "users_id";

    /**
     * Constructs AdminDAOImpl class, used in package by DAOFactory
     */
    AdminDAOImpl(){}

    /**
     * method finds entity by id, not implemented for AdminDAOImpl, throws UnsupportedOperationException if called
     */
    @Override
    public User findEntityById(Integer id) throws DAOException {
        throw new UnsupportedOperationException();
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about users with ADMIN and MODERATOR role, and statement for selecting total rows count of statement
     *
     * @param startUser number of first selected row
     * @param usersPerPage number of selected rows
     * @return container with list of admins and moderators, pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Friend> takeManagingUsers(int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> management = new PaginatedList<>();
        List<Friend> items = null;

        PreparedStatement selectManagingUsersStatement = null;
        ResultSet managingUsersResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectManagingUsersStatement = connection.prepareStatement(SQL_SELECT_MANAGING_USERS);
            selectManagingUsersStatement.setInt(1, startUser);
            selectManagingUsersStatement.setInt(2, usersPerPage);
            managingUsersResultSet = selectManagingUsersStatement.executeQuery();

            if (managingUsersResultSet.next()) {
                items = new ArrayList<>();
                managingUsersResultSet.beforeFirst();
                Friend user;
                while (managingUsersResultSet.next()) {
                    user = new Friend();
                    user.setLogin(managingUsersResultSet.getString(LOGIN));
                    user.setId(managingUsersResultSet.getInt(USER_ID));
                    user.setSurname(managingUsersResultSet.getString(SURNAME));
                    user.setName(managingUsersResultSet.getString(NAME));
                    user.setRole(Role.fromValue(managingUsersResultSet.getString(ROLE)));
                    user.setAvatar(managingUsersResultSet.getString(AVATAR));
                    user.setUserStatus(managingUsersResultSet.getString(USER_STATUS));
                    user.setUserRate(managingUsersResultSet.getDouble(USER_RATE));
                    items.add(user);
                }
            }

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(foundRowsResultSet.next()){
                management.setItemsPerPage(usersPerPage);
                management.setTotalCount(foundRowsResultSet.getInt(1));
                management.setItemStart(startUser);
                management.setItems(items);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectManagingUsersStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return management;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about user's bans, and statement for selecting total rows count of statement
     *
     * @param startBan number of first selected row
     * @param bansPerPage number of selected rows
     * @return container with list of bans, pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Ban> takeAllCurrentBans(int startBan, int bansPerPage) throws DAOException {
        PaginatedList<Ban> allBannedUsers = new PaginatedList<>();
        List<Ban> items = null;

        PreparedStatement selectBannedUsersStatement = null;
        ResultSet bannedUsersResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectBannedUsersStatement = connection.prepareStatement(SQL_SELECT_ALL_CURRENTLY_BANNED_USERS);
            selectBannedUsersStatement.setInt(1, startBan);
            selectBannedUsersStatement.setInt(2, bansPerPage);
            bannedUsersResultSet = selectBannedUsersStatement.executeQuery();

            if (bannedUsersResultSet.next()) {
                items = new ArrayList<>();
                bannedUsersResultSet.beforeFirst();
                Ban ban;
                while (bannedUsersResultSet.next()) {
                    ban = new Ban();
                    ban.setId(bannedUsersResultSet.getInt(BAN_ID));
                    ban.setCause(bannedUsersResultSet.getString(CAUSE));
                    ban.setPostId(bannedUsersResultSet.getInt(POST_ID));
                    ban.setStart(bannedUsersResultSet.getTimestamp(START));
                    ban.setEnd(bannedUsersResultSet.getTimestamp(END));

                    ShortUser user = new User();
                    user.setId(bannedUsersResultSet.getInt(USER_ID));
                    user.setLogin(bannedUsersResultSet.getString(LOGIN));
                    user.setRole(Role.fromValue(bannedUsersResultSet.getString(ROLE)));
                    user.setAvatar(bannedUsersResultSet.getString(AVATAR));
                    ban.setUser(user);

                    ShortUser moderator = new ShortUser();
                    moderator.setId(bannedUsersResultSet.getInt(MODERATOR_ID));
                    moderator.setLogin(bannedUsersResultSet.getString(MODERATOR_LOGIN));
                    moderator.setRole(Role.fromValue(bannedUsersResultSet.getString(MODERATOR_ROLE)));
                    moderator.setAvatar(bannedUsersResultSet.getString(MODERATOR_AVATAR));
                    ban.setModerator(moderator);

                    items.add(ban);
                }
            }

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(foundRowsResultSet.next()){
                allBannedUsers.setTotalCount(foundRowsResultSet.getInt(1));
                allBannedUsers.setItemStart(startBan);
                allBannedUsers.setItems(items);
                allBannedUsers.setItemsPerPage(bansPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectBannedUsersStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return allBannedUsers;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about user's complaints, and statement for selecting total rows count of statement
     *
     * @param startComplaint number of first selected row
     * @param complaintsPerPage number of selected rows
     * @return container with list of complaints, pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Complaint> takeAllComplaints(int startComplaint, int complaintsPerPage) throws DAOException {
        PaginatedList<Complaint> complaints = new PaginatedList<>();
        List<Complaint> items = null;

        PreparedStatement selectComplaintsStatement = null;
        ResultSet complaintsResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectComplaintsStatement = connection.prepareStatement(SQL_SELECT_ALL_COMPLAINTS);
            selectComplaintsStatement.setInt(1, startComplaint);
            selectComplaintsStatement.setInt(2, complaintsPerPage);
            complaintsResultSet = selectComplaintsStatement.executeQuery();

            if (complaintsResultSet.next()) {
                items = new ArrayList<>();
                complaintsResultSet.beforeFirst();
                Complaint complaint;
                while (complaintsResultSet.next()) {
                    complaint = new Complaint();
                    complaint.setPostId(complaintsResultSet.getInt(POST_ID));
                    complaint.setDescription(complaintsResultSet.getString(DESCRIPTION));
                    complaint.setPublishedTime(complaintsResultSet.getTimestamp(PUBLISHED_TIME));
                    complaint.setProcessedTime(complaintsResultSet.getTimestamp(PROCESSED_TIME));
                    complaint.setDecision(complaintsResultSet.getString(DECISION));
                    complaint.setStatus(Complaint.ComplaintStatus.fromValue(complaintsResultSet.getString(COMPLAINT_STATUS)));

                    ShortUser author = new User();
                    author.setId(complaintsResultSet.getInt(AUTHOR_ID));
                    author.setLogin(complaintsResultSet.getString(LOGIN));
                    author.setRole(Role.fromValue(complaintsResultSet.getString(ROLE)));
                    author.setAvatar(complaintsResultSet.getString(AVATAR));
                    complaint.setUser(author);

                    if (complaintsResultSet.getInt(MODERATOR_ID) != 0) {
                        ShortUser moderator = new ShortUser();
                        moderator.setId(complaintsResultSet.getInt(MODERATOR_ID));
                        moderator.setLogin(complaintsResultSet.getString(MODERATOR_LOGIN));
                        moderator.setRole(Role.fromValue(complaintsResultSet.getString(MODERATOR_ROLE)));
                        moderator.setAvatar(complaintsResultSet.getString(MODERATOR_AVATAR));
                        complaint.setModerator(moderator);
                    }
                    items.add(complaint);
                }
            }

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(foundRowsResultSet.next()){
                complaints.setItemsPerPage(complaintsPerPage);
                complaints.setTotalCount(foundRowsResultSet.getInt(1));
                complaints.setItems(items);
                complaints.setItemStart(startComplaint);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectComplaintsStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return complaints;
    }

    /**
     * creates PreparedStatement for updating user's role
     *
     * @param login user 's nickname
     * @param role user's new role
     * @return true if role was updated for this login, false otherwise
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public boolean updateUserStatus(String login, String role) throws DAOException {
        boolean updated = false;
        PreparedStatement updateUserRoleStatement = null;

        try {
            updateUserRoleStatement = connection.prepareStatement(SQL_UPDATE_USER_ROLE);
            updateUserRoleStatement.setString(1, role);
            updateUserRoleStatement.setString(2, login);
            int count = updateUserRoleStatement.executeUpdate();
            if (count == 1) {
                updated = true;
            }

        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(updateUserRoleStatement);
        }
        return updated;
    }

    /**
     * creates new PreparedStatement that inserts info about new category
     *
     * @param userId id of category moderator
     * @param titleEn english title of category
     * @param titleRu russian title of category
     * @param descriptionEn english description of category
     * @param descriptionRu russian description of category
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void addNewCategory(int userId, String titleEn, String titleRu, String descriptionEn, String descriptionRu) throws DAOException {
        PreparedStatement insertCategoryStatement = null;

        try {
            insertCategoryStatement = connection.prepareStatement(SQL_INSERT_NEW_CATEGORY);
            insertCategoryStatement.setInt(1, userId);
            insertCategoryStatement.setString(2, titleEn);
            insertCategoryStatement.setString(3, titleRu);
            insertCategoryStatement.setString(4, descriptionEn);
            insertCategoryStatement.setString(5, descriptionRu);
            insertCategoryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(insertCategoryStatement);
        }
    }

    /**
     * creates statement that updates status of category to CLOSED
     *
     * @param categoryId id of closing category
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void updateCategoryStatusToClosed(int categoryId) throws DAOException {
        PreparedStatement closeCategoryStatement = null;
        try {
            closeCategoryStatement = connection.prepareStatement(SQL_UPDATE_CATEGORY_TO_CLOSED);
            closeCategoryStatement.setInt(1, categoryId);
            closeCategoryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(closeCategoryStatement);
        }

    }

    /**
     * creates statement for updating category information by ADMIN
     *
     * @param categoryId id of category
     * @param titleEn english title of category
     * @param titleRu russian title of category
     * @param descriptionEn english description of category
     * @param descriptionRu russian description of category
     * @param login moderator nickname
     * @param categoryStatus updated status of category
     * @return true if category with updating id was found and updated, false otherwise
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public boolean updateCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String login, String categoryStatus) throws DAOException {
        boolean updated = false;
        PreparedStatement selectUserIdStatement = null;
        PreparedStatement updateCategoryStatement = null;
        ResultSet userIdResultSet;

        try {
            connection.setAutoCommit(false);
            selectUserIdStatement = connection.prepareStatement(SQL_SELECT_USER_ID_BY_LOGIN);
            selectUserIdStatement.setString(1, login);
            userIdResultSet = selectUserIdStatement.executeQuery();
            if(userIdResultSet.next()){
               updateCategoryStatement = connection.prepareStatement(SQL_UPDATE_CATEGORY_INFO);
                updateCategoryStatement.setString(1, login);
                updateCategoryStatement.setString(2, titleEn);
                updateCategoryStatement.setString(3, titleRu);
                updateCategoryStatement.setString(4, descriptionEn);
                updateCategoryStatement.setString(5, descriptionRu);
                updateCategoryStatement.setString(6, categoryStatus.toLowerCase());
                updateCategoryStatement.setInt(7, Integer.parseInt(categoryId));
                updateCategoryStatement.executeUpdate();
                updated = true;
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectUserIdStatement);
            connection.closeStatement(updateCategoryStatement);
        }
        return updated;
    }
}
