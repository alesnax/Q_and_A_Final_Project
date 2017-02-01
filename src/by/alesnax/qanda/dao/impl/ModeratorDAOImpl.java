package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.ModeratorDAO;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.entity.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by alesnax on 13.01.2017.
 */

public class ModeratorDAOImpl extends AbstractDAO<Integer, User> implements ModeratorDAO {

    private static final String SQL_SELECT_ALL_USERS = "SELECT sql_calc_found_rows users.id AS user_id, users.surname, users.name,  users.avatar, users.role, users.login, users.state, users.status AS u_status, AVG(rates.value) AS rate\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=users.id)\n" +
            "WHERE users.state='active' GROUP BY users.id ORDER BY surname, name LIMIT ?,?;";

    private static final String SQL_SELECT_CURRENTLY_BANNED_USERS = "SELECT sql_calc_found_rows bans.id AS ban_id, bans.cause, bans.posts_id, bans.start, bans.end, " +
            "users.id AS user_id, users.avatar, users.role, users.login,  \n" +
            "bans.users_admin_id AS moderator_id, admins.login AS moderator_login, admins.avatar AS moderator_avatar, admins.role AS moderator_role\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id JOIN bans ON (users.id = bans.users_id AND current_timestamp() < bans.end) \n" +
            "JOIN users AS admins ON admins.id=bans.users_admin_id\n" +
            "WHERE users.state = 'active' AND bans.users_admin_id=? GROUP BY users.id ORDER BY bans.end DESC LIMIT ?,?";

    private static final String SQL_UPDATE_BAN_END_TO_CURRENT_TIME = "UPDATE bans SET end= current_timestamp() WHERE id=?;";

    private static final String SQL_SELECT_COMPLAINTS_BY_MODERATOR = "SELECT sql_calc_found_rows posts_id, complaints.users_id, authors.login, authors.avatar, authors.role,  description, complaints.published_time, \n" +
            "complaints.status, processed_time, decision, moderator_id, moder.login AS moderator_login, moder.role AS moderator_role, moder.avatar AS moderator_avatar \n" +
            "FROM complaints LEFT JOIN users AS authors ON users_id=authors.id JOIN posts ON posts.id=posts_id JOIN categories ON (categories.id=posts.category_id AND categories.users_id=?)\n" +
            "LEFT JOIN users AS moder ON moderator_id=moder.id\n" +
            "ORDER BY complaints.published_time DESC LIMIT ?,?;";

    private static final String SQL_UPDATE_COMPLAINT_STATUS_TO_CANCELLED = "UPDATE complaints SET status='cancelled', processed_time=CURRENT_TIMESTAMP, moderator_id=?, decision=? WHERE posts_id=? and users_id=?;";

    private static final String SQL_UPDATE_COMPLAINT_STATUS_TO_APPROVED = "UPDATE complaints SET status='approved', processed_time=CURRENT_TIMESTAMP, moderator_id=?, decision=? WHERE posts_id=? and users_id=?;";

    private static final String SQL_INSERT_NEW_BAN = "INSERT INTO bans (`users_id`, `users_admin_id`, `posts_id`, `cause`, `end`) VALUES ((SELECT users_id FROM posts WHERE id=?), ?, ?, ?, ?);";

    private static final String SQL_SELECT_FOUND_ROWS = "SELECT FOUND_ROWS();";

    private static final String SQL_UPDATE_CATEGORY_INFO = "UPDATE categories SET title_en=?, title_ru=?, description_en=?, description_ru=?, status=? WHERE `id`=?;";

    private static final String USER_ID = "user_id";
    private static final String LOGIN = "login";
    private static final String SURNAME = "surname";
    private static final String NAME = "name";
    private static final String ROLE = "role";
    private static final String AVATAR = "avatar";

    private static final String USER_STATUS = "u_status";
    private static final String USER_RATE = "rate";

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


    public ModeratorDAOImpl(WrappedConnection connection) {
        super(connection);
    }

    @Override
    public User findEntityById(Integer id) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaginatedList<Friend> takeAllUsers(int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> allUsers = new PaginatedList<>();
        List<Friend> items = null;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_ALL_USERS);
            st.setInt(1, startUser);
            st.setInt(2, usersPerPage);
            rs = st.executeQuery();

            if (rs.next()) {
                items = new ArrayList<>();
                rs.beforeFirst();
                Friend user;
                while (rs.next()) {
                    user = new Friend();
                    user.setId(rs.getInt(USER_ID));
                    user.setLogin(rs.getString(LOGIN));
                    user.setSurname(rs.getString(SURNAME));
                    user.setName(rs.getString(NAME));
                    user.setAvatar(rs.getString(AVATAR));
                    user.setRole(Role.fromValue(rs.getString(ROLE)));
                    user.setUserStatus(rs.getString(USER_STATUS));
                    user.setUserRate(rs.getDouble(USER_RATE));
                    items.add(user);
                }
            }

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (rs1.next()) {
                allUsers.setTotalCount(rs1.getInt(1));
                allUsers.setItemStart(startUser);
                allUsers.setItemsPerPage(usersPerPage);
                allUsers.setItems(items);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return allUsers;
    }

    @Override
    public PaginatedList<Ban> takeCurrentBansByModeratorId(int userId, int startBan, int bansPerPage) throws DAOException {
        PaginatedList<Ban> allBannedUsers = new PaginatedList<>();
        List<Ban> items;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_CURRENTLY_BANNED_USERS);
            st.setInt(1, userId);
            st.setInt(2, startBan);
            st.setInt(3, bansPerPage);
            rs = st.executeQuery();

            items = createBansFromResultSet(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (rs1.next()) {
                allBannedUsers.setItems(items);
                allBannedUsers.setItemsPerPage(bansPerPage);
                allBannedUsers.setTotalCount(rs1.getInt(1));
                allBannedUsers.setItemStart(startBan);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return allBannedUsers;
    }

    @Override
    public void updateBansStatusFinished(int banId) throws DAOException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(SQL_UPDATE_BAN_END_TO_CURRENT_TIME);
            st.setInt(1, banId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public PaginatedList<Complaint> takeComplaintsByModeratorId(int userId, int startComplaint, int complaintsPerPage) throws DAOException {
        PaginatedList<Complaint> complaints = new PaginatedList<>();
        List<Complaint> items = null;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_COMPLAINTS_BY_MODERATOR);
            st.setInt(1, userId);
            st.setInt(2, startComplaint);
            st.setInt(3, complaintsPerPage);
            rs = st.executeQuery();

            if (rs.next()) {
                items = new ArrayList<>();
                rs.beforeFirst();
                Complaint complaint;
                while (rs.next()) {
                    complaint = new Complaint();
                    complaint.setPostId(rs.getInt(POST_ID));
                    complaint.setDescription(rs.getString(DESCRIPTION));
                    complaint.setProcessedTime(rs.getTimestamp(PROCESSED_TIME));
                    complaint.setPublishedTime(rs.getTimestamp(PUBLISHED_TIME));
                    complaint.setDecision(rs.getString(DECISION));
                    complaint.setStatus(Complaint.ComplaintStatus.fromValue(rs.getString(COMPLAINT_STATUS)));

                    ShortUser author = new User();
                    author.setId(rs.getInt(AUTHOR_ID));
                    author.setLogin(rs.getString(LOGIN));
                    author.setRole(Role.fromValue(rs.getString(ROLE)));
                    author.setAvatar(rs.getString(AVATAR));
                    complaint.setUser(author);

                    ShortUser moderator = new ShortUser();
                    moderator.setId(rs.getInt(MODERATOR_ID));
                    moderator.setLogin(rs.getString(MODERATOR_LOGIN));
                    String moderatorRole = rs.getString(MODERATOR_ROLE);
                    if (moderatorRole != null) {
                        moderator.setRole(Role.fromValue(rs.getString(MODERATOR_ROLE)));
                    }
                    moderator.setAvatar(rs.getString(MODERATOR_AVATAR));
                    complaint.setModerator(moderator);

                    items.add(complaint);
                }
            }

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (rs1.next()) {
                complaints.setItemsPerPage(complaintsPerPage);
                complaints.setItems(items);
                complaints.setTotalCount(rs1.getInt(1));
                complaints.setItemStart(startComplaint);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return complaints;

    }

    @Override
    public void updateComplaintStatusToApproved(int moderatorId, int complaintPostId, int complaintAuthorId, String decision) throws DAOException {
        PreparedStatement st = null;
        PreparedStatement st1 = null;
        try {
            connection.setAutoCommit(false);
            st = connection.prepareStatement(SQL_UPDATE_COMPLAINT_STATUS_TO_APPROVED);
            st.setInt(1, moderatorId);
            st.setString(2, decision);
            st.setInt(3, complaintPostId);
            st.setInt(4, complaintAuthorId);
            st.executeUpdate();


            java.util.Date date = new Date();
            date.setDate(date.getDate() + 3);
            Timestamp end = new Timestamp(date.getTime());

            st1 = connection.prepareStatement(SQL_INSERT_NEW_BAN);
            st1.setInt(1, complaintPostId);
            st1.setInt(2, moderatorId);
            st1.setInt(3, complaintPostId);
            st1.setString(4, decision);
            st1.setTimestamp(5, end);
            st1.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new DAOException("SQL Error, check source", e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
    }

    @Override
    public void updateComplaintStatusToCancelled(int moderatorId, int complaintPostId, int complaintAuthorId, String decision) throws DAOException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(SQL_UPDATE_COMPLAINT_STATUS_TO_CANCELLED);
            st.setInt(1, moderatorId);
            st.setString(2, decision);
            st.setInt(3, complaintPostId);
            st.setInt(4, complaintAuthorId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public void updateCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String categoryStatus) throws DAOException {
        PreparedStatement st1 = null;
        try {
            st1 = connection.prepareStatement(SQL_UPDATE_CATEGORY_INFO);
            st1.setString(1, titleEn);
            st1.setString(2, titleRu);
            st1.setString(3, descriptionEn);
            st1.setString(4, descriptionRu);
            st1.setString(5, categoryStatus.toLowerCase());
            st1.setInt(6, Integer.parseInt(categoryId));
            st1.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st1);
        }
    }

    private List<Ban> createBansFromResultSet(ResultSet rs) throws SQLException {
        List<Ban> bans = null;
        if (rs.next()) {
            bans = new ArrayList<>();
            rs.beforeFirst();
            Ban ban;
            while (rs.next()) {
                ban = new Ban();
                ban.setId(rs.getInt(BAN_ID));
                ban.setPostId(rs.getInt(POST_ID));
                ban.setCause(rs.getString(CAUSE));
                ban.setStart(rs.getTimestamp(START));
                ban.setEnd(rs.getTimestamp(END));

                ShortUser user = new User();
                user.setId(rs.getInt(USER_ID));
                user.setLogin(rs.getString(LOGIN));
                user.setRole(Role.fromValue(rs.getString(ROLE)));
                user.setAvatar(rs.getString(AVATAR));
                ban.setUser(user);

                ShortUser moderator = new ShortUser();
                moderator.setId(rs.getInt(MODERATOR_ID));
                moderator.setLogin(rs.getString(MODERATOR_LOGIN));
                moderator.setRole(Role.fromValue(rs.getString(MODERATOR_ROLE)));
                moderator.setAvatar(rs.getString(MODERATOR_AVATAR));
                ban.setModerator(moderator);

                bans.add(ban);
            }
        }
        return bans;
    }
}
