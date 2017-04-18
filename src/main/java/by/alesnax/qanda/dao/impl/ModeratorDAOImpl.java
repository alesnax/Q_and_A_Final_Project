package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.DAOException;
import by.alesnax.qanda.dao.ModeratorDAO;
import by.alesnax.qanda.entity.*;
import by.alesnax.qanda.pagination.PaginatedList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Implements ModeratorDAO interface and extends AbstractDAO class.
 * Implements all methods of ModeratorDAO. Processes operations of
 * manipulating with information that stores in databases and  related with session user role 'MODERATOR' and 'ADMIN'.
 * Methods of classes sends SQL statements to the database and
 * get result as ResultSet of objects or number of processed rows in database.
 *
 * @author Aliaksandr Nakhankou
 * @see by.alesnax.qanda.dao.AbstractDAO
 * @see by.alesnax.qanda.dao.ModeratorDAO
 */
public class ModeratorDAOImpl extends AbstractDAO<Integer, User> implements ModeratorDAO {
    private static Logger logger = LogManager.getLogger(ModeratorDAOImpl.class);

    /**
     * SQL query that selects information about all users and their rates,
     * finds limited list of users.
     */
    private static final String SQL_SELECT_ALL_USERS = "SELECT sql_calc_found_rows users.id AS user_id, users.surname, users.name,  users.avatar, users.role, users.login, users.state, users.status AS u_status, AVG(rates.value) AS rate\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=users.id)\n" +
            "WHERE users.state='active' GROUP BY users.id ORDER BY surname, name LIMIT ?,?;";

    /**
     * SQL query that selects users, that are currently have banned status and banned for post
     * moderated by definite moderator. Selects limited list of blocked users.
     */
    private static final String SQL_SELECT_CURRENTLY_BANNED_USERS = "SELECT sql_calc_found_rows bans.id AS ban_id, bans.cause, bans.posts_id, bans.start, bans.end, " +
            "users.id AS user_id, users.avatar, users.role, users.login,  \n" +
            "bans.users_admin_id AS moderator_id, admins.login AS moderator_login, admins.avatar AS moderator_avatar, admins.role AS moderator_role\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id JOIN bans ON (users.id = bans.users_id AND current_timestamp() < bans.end) \n" +
            "JOIN users AS admins ON admins.id=bans.users_admin_id\n" +
            "WHERE users.state = 'active' AND bans.users_admin_id=? GROUP BY users.id ORDER BY bans.end DESC LIMIT ?,?";

    /**
     * SQL query that updates end time of ban to current time? in other words query stops action of ban
     */
    private static final String SQL_UPDATE_BAN_END_TO_CURRENT_TIME = "UPDATE bans SET end= current_timestamp() WHERE id=?;";

    /**
     *
     */
    private static final String SQL_SELECT_COMPLAINTS_BY_MODERATOR = "SELECT sql_calc_found_rows posts_id, complaints.users_id, authors.login, authors.avatar, authors.role,  description, complaints.published_time, \n" +
            "complaints.status, processed_time, decision, moderator_id, moder.login AS moderator_login, moder.role AS moderator_role, moder.avatar AS moderator_avatar \n" +
            "FROM complaints LEFT JOIN users AS authors ON users_id=authors.id JOIN posts ON posts.id=posts_id JOIN categories ON (categories.id=posts.category_id AND categories.users_id=?)\n" +
            "LEFT JOIN users AS moder ON moderator_id=moder.id\n" +
            "ORDER BY complaints.published_time DESC LIMIT ?,?;";

    /**
     * SQL query that updates status of complaint to 'cancelled'
     */
    private static final String SQL_UPDATE_COMPLAINT_STATUS_TO_CANCELLED = "UPDATE complaints SET status='cancelled', processed_time=CURRENT_TIMESTAMP, moderator_id=?, decision=? WHERE posts_id=? and users_id=?;";

    /**
     * SQL query that updates status of complaint to 'approved'
     */
    private static final String SQL_UPDATE_COMPLAINT_STATUS_TO_APPROVED = "UPDATE complaints SET status='approved', processed_time=CURRENT_TIMESTAMP, moderator_id=?, decision=? WHERE posts_id=? and users_id=?;";

    /**
     * SQL query that creates new ban row
     */
    private static final String SQL_INSERT_NEW_BAN = "INSERT INTO bans (`users_id`, `users_admin_id`, `posts_id`, `cause`, `end`) VALUES ((SELECT users_id FROM posts WHERE id=?), ?, ?, ?, ?);";

    /**
     * SQL query that selects number of found rows while previous statement.
     * Statement should be located in one transaction with previous one.
     */
    private static final String SQL_SELECT_FOUND_ROWS = "SELECT FOUND_ROWS();";

    /**
     * SQL query for updating information about category by moderator
     */
    private static final String SQL_UPDATE_CATEGORY_INFO = "UPDATE categories SET title_en=?, title_ru=?, description_en=?, description_ru=?, status=? WHERE `id`=?;";

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

    private static final int DEFAULT_BAN_DAYS = 3;

    /**
     * Constructs ModeratorDAOImpl class, used in package by DAOFactory
     */
    ModeratorDAOImpl(){}

    /**
     * method finds entity by id, not implemented for ModeratorDAOImpl, throws UnsupportedOperationException if called
     */
    @Override
    public User findEntityById(Integer id) throws DAOException {
        throw new UnsupportedOperationException();
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about users and statement for selecting total rows count of statement
     *
     * @param startUser number of first selected row
     * @param usersPerPage number of selected rows
     * @return container with list of users and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Friend> takeAllUsers(int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> allUsers = new PaginatedList<>();
        List<Friend> items = null;

        PreparedStatement selectUsersStatement = null;
        ResultSet usersResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectUsersStatement = connection.prepareStatement(SQL_SELECT_ALL_USERS);
            selectUsersStatement.setInt(1, startUser);
            selectUsersStatement.setInt(2, usersPerPage);
            usersResultSet = selectUsersStatement.executeQuery();

            if (usersResultSet.next()) {
                items = new ArrayList<>();
                usersResultSet.beforeFirst();
                Friend user;
                while (usersResultSet.next()) {
                    user = new Friend();
                    user.setId(usersResultSet.getInt(USER_ID));
                    user.setLogin(usersResultSet.getString(LOGIN));
                    user.setSurname(usersResultSet.getString(SURNAME));
                    user.setName(usersResultSet.getString(NAME));
                    user.setAvatar(usersResultSet.getString(AVATAR));
                    user.setRole(Role.fromValue(usersResultSet.getString(ROLE)));
                    user.setUserStatus(usersResultSet.getString(USER_STATUS));
                    user.setUserRate(usersResultSet.getDouble(USER_RATE));
                    items.add(user);
                }
            }

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                allUsers.setTotalCount(foundRowsResultSet.getInt(1));
                allUsers.setItemStart(startUser);
                allUsers.setItemsPerPage(usersPerPage);
                allUsers.setItems(items);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectUsersStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return allUsers;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about user's bans, and statement for selecting total rows count of statement
     *
     * @param userId id of moderator
     * @param startBan number of first selected row
     * @param bansPerPage number of selected rows
     * @return container with list of bans and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Ban> takeCurrentBansByModeratorId(int userId, int startBan, int bansPerPage) throws DAOException {
        PaginatedList<Ban> allBannedUsers = new PaginatedList<>();
        List<Ban> items;

        PreparedStatement selectBannedUsersStatement = null;
        ResultSet bannedUsersResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectBannedUsersStatement = connection.prepareStatement(SQL_SELECT_CURRENTLY_BANNED_USERS);
            selectBannedUsersStatement.setInt(1, userId);
            selectBannedUsersStatement.setInt(2, startBan);
            selectBannedUsersStatement.setInt(3, bansPerPage);
            bannedUsersResultSet = selectBannedUsersStatement.executeQuery();

            items = createBansFromResultSet(bannedUsersResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                allBannedUsers.setItems(items);
                allBannedUsers.setItemsPerPage(bansPerPage);
                allBannedUsers.setTotalCount(foundRowsResultSet.getInt(1));
                allBannedUsers.setItemStart(startBan);
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
     * creates PreparedStatement that updates end time of ban to current time (finishing term of ban)
     *
     * @param banId ban id
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void updateBansStatusFinished(int banId) throws DAOException {
        PreparedStatement stopBanStatement = null;
        try {
            stopBanStatement = connection.prepareStatement(SQL_UPDATE_BAN_END_TO_CURRENT_TIME);
            stopBanStatement.setInt(1, banId);
            stopBanStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(stopBanStatement);
        }
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about user's complaints, and statement for selecting total rows count of statement
     *
     * @param userId moderator's id
     * @param startComplaint  number of first selected row
     * @param complaintsPerPage number of selected rows
     * @return container with list of complaints and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Complaint> takeComplaintsByModeratorId(int userId, int startComplaint, int complaintsPerPage) throws DAOException {
        PaginatedList<Complaint> complaints = new PaginatedList<>();
        List<Complaint> items = null;

        PreparedStatement selectComplaintsStatement = null;
        ResultSet complaintsResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectComplaintsStatement = connection.prepareStatement(SQL_SELECT_COMPLAINTS_BY_MODERATOR);
            selectComplaintsStatement.setInt(1, userId);
            selectComplaintsStatement.setInt(2, startComplaint);
            selectComplaintsStatement.setInt(3, complaintsPerPage);
            complaintsResultSet = selectComplaintsStatement.executeQuery();

            if (complaintsResultSet.next()) {
                items = new ArrayList<>();
                complaintsResultSet.beforeFirst();
                Complaint complaint;
                while (complaintsResultSet.next()) {
                    complaint = new Complaint();
                    complaint.setPostId(complaintsResultSet.getInt(POST_ID));
                    complaint.setDescription(complaintsResultSet.getString(DESCRIPTION));
                    complaint.setProcessedTime(complaintsResultSet.getTimestamp(PROCESSED_TIME));
                    complaint.setPublishedTime(complaintsResultSet.getTimestamp(PUBLISHED_TIME));
                    complaint.setDecision(complaintsResultSet.getString(DECISION));
                    complaint.setStatus(Complaint.ComplaintStatus.fromValue(complaintsResultSet.getString(COMPLAINT_STATUS)));

                    ShortUser author = new User();
                    author.setId(complaintsResultSet.getInt(AUTHOR_ID));
                    author.setLogin(complaintsResultSet.getString(LOGIN));
                    author.setRole(Role.fromValue(complaintsResultSet.getString(ROLE)));
                    author.setAvatar(complaintsResultSet.getString(AVATAR));
                    complaint.setUser(author);

                    ShortUser moderator = new ShortUser();
                    moderator.setId(complaintsResultSet.getInt(MODERATOR_ID));
                    moderator.setLogin(complaintsResultSet.getString(MODERATOR_LOGIN));
                    String moderatorRole = complaintsResultSet.getString(MODERATOR_ROLE);
                    if (moderatorRole != null) {
                        moderator.setRole(Role.fromValue(complaintsResultSet.getString(MODERATOR_ROLE)));
                    }
                    moderator.setAvatar(complaintsResultSet.getString(MODERATOR_AVATAR));
                    complaint.setModerator(moderator);

                    items.add(complaint);
                }
            }

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                complaints.setItemsPerPage(complaintsPerPage);
                complaints.setItems(items);
                complaints.setTotalCount(foundRowsResultSet.getInt(1));
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
     * creates PreparedStatement for updating complaint status to UPDATED with decision and
     * second statement that creates ban row of post author
     *
     * @param moderatorId id of moderator who made decision
     * @param complaintPostId id of post that is cause of complaint
     * @param complaintAuthorId id of complaint creator
     * @param decision complaint decision of moderator
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void updateComplaintStatusToApproved(int moderatorId, int complaintPostId, int complaintAuthorId, String decision) throws DAOException {
        PreparedStatement approveComplaintStatement = null;
        PreparedStatement insertBanStatement = null;
        try {
            connection.setAutoCommit(false);
            approveComplaintStatement = connection.prepareStatement(SQL_UPDATE_COMPLAINT_STATUS_TO_APPROVED);
            approveComplaintStatement.setInt(1, moderatorId);
            approveComplaintStatement.setString(2, decision);
            approveComplaintStatement.setInt(3, complaintPostId);
            approveComplaintStatement.setInt(4, complaintAuthorId);
            approveComplaintStatement.executeUpdate();


            Calendar currentDate = new GregorianCalendar();
            currentDate.set(Calendar.DAY_OF_MONTH, new GregorianCalendar().get(Calendar.DAY_OF_MONTH) + DEFAULT_BAN_DAYS);
            Timestamp end = new Timestamp(currentDate.getTimeInMillis());

            insertBanStatement = connection.prepareStatement(SQL_INSERT_NEW_BAN);
            insertBanStatement.setInt(1, complaintPostId);
            insertBanStatement.setInt(2, moderatorId);
            insertBanStatement.setInt(3, complaintPostId);
            insertBanStatement.setString(4, decision);
            insertBanStatement.setTimestamp(5, end);
            insertBanStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(approveComplaintStatement);
            connection.closeStatement(insertBanStatement);
        }
    }

    /**
     * creates PreparedStatement for updating complaint status to CANCELLED with moderator's decision
     *
     * @param moderatorId id of moderator who made decision
     * @param complaintPostId id of post that is cause of complaint
     * @param complaintAuthorId id of complaint creator
     * @param decision complaint decision of moderator
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void updateComplaintStatusToCancelled(int moderatorId, int complaintPostId, int complaintAuthorId, String decision) throws DAOException {
        PreparedStatement cancelComplaintStatement = null;
        try {
            cancelComplaintStatement = connection.prepareStatement(SQL_UPDATE_COMPLAINT_STATUS_TO_CANCELLED);
            cancelComplaintStatement.setInt(1, moderatorId);
            cancelComplaintStatement.setString(2, decision);
            cancelComplaintStatement.setInt(3, complaintPostId);
            cancelComplaintStatement.setInt(4, complaintAuthorId);
            cancelComplaintStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(cancelComplaintStatement);
        }
    }

    /**
     * creates statement for updating category information by MODERATOR
     *
     * @param categoryId id of category
     * @param titleEn english title of category
     * @param titleRu russian title of category
     * @param descriptionEn english description of category
     * @param descriptionRu russian description of category
     * @param categoryStatus updated status of category
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void updateCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String categoryStatus) throws DAOException {
        PreparedStatement updateCategoryStatement = null;
        try {
            updateCategoryStatement = connection.prepareStatement(SQL_UPDATE_CATEGORY_INFO);
            updateCategoryStatement.setString(1, titleEn);
            updateCategoryStatement.setString(2, titleRu);
            updateCategoryStatement.setString(3, descriptionEn);
            updateCategoryStatement.setString(4, descriptionRu);
            updateCategoryStatement.setString(5, categoryStatus.toLowerCase());
            updateCategoryStatement.setInt(6, Integer.parseInt(categoryId));
            updateCategoryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(updateCategoryStatement);
        }
    }

    /**
     * method for processing selecting list of bans
     *
     * @param bansResultSet processed ResultSet
     * @return list of bans
     * @throws SQLException if exception while processing SQL statement and connection will be caught
     */
    private List<Ban> createBansFromResultSet(ResultSet bansResultSet) throws SQLException {
        List<Ban> bans = null;
        if (bansResultSet.next()) {
            bans = new ArrayList<>();
            bansResultSet.beforeFirst();
            Ban ban;
            while (bansResultSet.next()) {
                ban = new Ban();
                ban.setId(bansResultSet.getInt(BAN_ID));
                ban.setPostId(bansResultSet.getInt(POST_ID));
                ban.setCause(bansResultSet.getString(CAUSE));
                ban.setStart(bansResultSet.getTimestamp(START));
                ban.setEnd(bansResultSet.getTimestamp(END));

                ShortUser user = new User();
                user.setId(bansResultSet.getInt(USER_ID));
                user.setLogin(bansResultSet.getString(LOGIN));
                user.setRole(Role.fromValue(bansResultSet.getString(ROLE)));
                user.setAvatar(bansResultSet.getString(AVATAR));
                ban.setUser(user);

                ShortUser moderator = new ShortUser();
                moderator.setId(bansResultSet.getInt(MODERATOR_ID));
                moderator.setLogin(bansResultSet.getString(MODERATOR_LOGIN));
                moderator.setRole(Role.fromValue(bansResultSet.getString(MODERATOR_ROLE)));
                moderator.setAvatar(bansResultSet.getString(MODERATOR_AVATAR));
                ban.setModerator(moderator);

                bans.add(ban);
            }
        }
        return bans;
    }
}
