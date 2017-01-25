package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.AdminDAO;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.entity.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 */
public class AdminDAOImpl extends AbstractDAO<Integer, User> implements AdminDAO {

    private static final String SQL_SELECT_MANAGING_USERS = "SELECT sql_calc_found_rows users.id AS user_id, users.surname, users.name,  " +
            "users.avatar, users.role, users.login, users.status AS u_status, AVG(rates.value) AS rate\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=users.id)\n" +
            "WHERE users.state = 'active' AND users.role!='user' GROUP BY users.id ORDER BY role, surname, name LIMIT ?,?;";

    private static final String SQL_SELECT_ALL_CURRENTLY_BANNED_USERS = "SELECT sql_calc_found_rows bans.id AS ban_id, bans.cause, bans.posts_id, bans.start, bans.end, " +
            "users.id AS user_id, users.avatar, users.role, users.login,  \n" +
            "bans.users_admin_id AS moderator_id, admins.login AS moderator_login, admins.avatar AS moderator_avatar, admins.role AS moderator_role\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id JOIN bans ON (users.id = bans.users_id AND current_timestamp() < bans.end) \n" +
            "JOIN users AS admins ON admins.id=bans.users_admin_id\n" +
            "WHERE users.state = 'active' GROUP BY users.id ORDER BY bans.end DESC LIMIT ?,?;";

    private static final String SQL_SELECT_ALL_COMPLAINTS = "SELECT sql_calc_found_rows posts_id, users_id, authors.login, authors.avatar, authors.role,  description, published_time, \n" +
            "complaints.status, processed_time, decision, moderator_id, coalesce(moder.login, 0) AS moderator_login, moder.role AS moderator_role, moder.avatar AS moderator_avatar \n" +
            "FROM likeit_db.complaints LEFT JOIN users AS authors ON users_id=authors.id LEFT JOIN users AS moder ON moderator_id=moder.id\n" +
            "ORDER BY published_time DESC LIMIT ?,?;";

    private static final String SQL_UPDATE_USER_ROLE = "UPDATE users SET role=? WHERE login=?  AND role!='admin';";

    private static final String SQL_UPDATE_CATEGORY_TO_CLOSED = "UPDATE categories SET status='closed' WHERE id=?;";

    private static final String SQL_NEW_CATEGORY = "INSERT INTO categories (`users_id`, `title_en`, `title_ru`, `description_en`, `description_ru`) VALUES (?, ?, ?, ?, ?);";

    private static final String SQL_SELECT_FOUND_ROWS = "SELECT FOUND_ROWS();";

    private static final String SQL_SELECT_USER_ID_BY_LOGIN = "SELECT id FROM users WHERE login=? AND role!='user';";

    private static final String SQL_UPDATE_CATEGORY_INFO = "UPDATE categories SET users_id=(SELECT id FROM users WHERE login=?), title_en=?, title_ru=?, description_en=?, description_ru=?, status=? WHERE `id`=?;";



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


    public AdminDAOImpl(WrappedConnection connection) {
        super(connection);
    }

    @Override
    public User findEntityById(Integer id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaginatedList<Friend> takeManagingUsers(int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> management = new PaginatedList<>();
        List<Friend> items = null;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_MANAGING_USERS);
            st.setInt(1, startUser);
            st.setInt(2, usersPerPage);
            rs = st.executeQuery();

            if (rs.next()) {
                items = new ArrayList<>();
                rs.beforeFirst();
                Friend user;
                while (rs.next()) {
                    user = new Friend();
                    user.setLogin(rs.getString(LOGIN));
                    user.setId(rs.getInt(USER_ID));
                    user.setSurname(rs.getString(SURNAME));
                    user.setName(rs.getString(NAME));
                    user.setRole(Role.fromValue(rs.getString(ROLE)));
                    user.setAvatar(rs.getString(AVATAR));
                    user.setUserStatus(rs.getString(USER_STATUS));
                    user.setUserRate(rs.getDouble(USER_RATE));
                    items.add(user);
                }
            }

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                management.setItemsPerPage(usersPerPage);
                management.setTotalCount(rs1.getInt(1));
                management.setItemStart(startUser);
                management.setItems(items);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return management;
    }

    @Override
    public PaginatedList<Ban> takeAllCurrentBans(int startBan, int bansPerPage) throws DAOException {
        PaginatedList<Ban> allBannedUsers = new PaginatedList<>();
        List<Ban> items = null;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_ALL_CURRENTLY_BANNED_USERS);
            st.setInt(1, startBan);
            st.setInt(2, bansPerPage);
            rs = st.executeQuery();

            if (rs.next()) {
                items = new ArrayList<>();
                rs.beforeFirst();
                Ban ban;
                while (rs.next()) {
                    ban = new Ban();
                    ban.setId(rs.getInt(BAN_ID));
                    ban.setCause(rs.getString(CAUSE));
                    ban.setPostId(rs.getInt(POST_ID));
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

                    items.add(ban);
                }
            }

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                allBannedUsers.setTotalCount(rs1.getInt(1));
                allBannedUsers.setItemStart(startBan);
                allBannedUsers.setItems(items);
                allBannedUsers.setItemsPerPage(bansPerPage);
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
    public PaginatedList<Complaint> takeAllComplaints(int startComplaint, int complaintsPerPage) throws DAOException {
        PaginatedList<Complaint> complaints = new PaginatedList<>();
        List<Complaint> items = null;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_ALL_COMPLAINTS);
            st.setInt(1, startComplaint);
            st.setInt(2, complaintsPerPage);
            rs = st.executeQuery();

            if (rs.next()) {
                items = new ArrayList<>();
                rs.beforeFirst();
                Complaint complaint;
                while (rs.next()) {
                    complaint = new Complaint();
                    complaint.setPostId(rs.getInt(POST_ID));
                    complaint.setDescription(rs.getString(DESCRIPTION));
                    complaint.setPublishedTime(rs.getTimestamp(PUBLISHED_TIME));
                    complaint.setProcessedTime(rs.getTimestamp(PROCESSED_TIME));
                    complaint.setDecision(rs.getString(DECISION));
                    complaint.setStatus(Complaint.ComplaintStatus.fromValue(rs.getString(COMPLAINT_STATUS)));

                    ShortUser author = new User();
                    author.setId(rs.getInt(AUTHOR_ID));
                    author.setLogin(rs.getString(LOGIN));
                    author.setRole(Role.fromValue(rs.getString(ROLE)));
                    author.setAvatar(rs.getString(AVATAR));
                    complaint.setUser(author);

                    if (rs.getInt(MODERATOR_ID) != 0) {
                        ShortUser moderator = new ShortUser();
                        moderator.setId(rs.getInt(MODERATOR_ID));
                        moderator.setLogin(rs.getString(MODERATOR_LOGIN));
                        moderator.setRole(Role.fromValue(rs.getString(MODERATOR_ROLE)));
                        moderator.setAvatar(rs.getString(MODERATOR_AVATAR));
                        complaint.setModerator(moderator);
                    }
                    items.add(complaint);
                }
            }

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                complaints.setItemsPerPage(complaintsPerPage);
                complaints.setTotalCount(rs1.getInt(1));
                complaints.setItems(items);
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
    public boolean updateUserStatus(String login, String role) throws DAOException {
        boolean updated = false;
        PreparedStatement st = null;

        try {
            st = connection.prepareStatement(SQL_UPDATE_USER_ROLE);
            st.setString(1, role);
            st.setString(2, login);
            int count = st.executeUpdate();
            if (count == 1) {
                updated = true;
            }

        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return updated;
    }

    @Override
    public void addNewCategory(int userId, String titleEn, String titleRu, String descriptionEn, String descriptionRu) throws DAOException {
        PreparedStatement st = null;

        try {
            st = connection.prepareStatement(SQL_NEW_CATEGORY);
            st.setInt(1, userId);
            st.setString(2, titleEn);
            st.setString(3, titleRu);
            st.setString(4, descriptionEn);
            st.setString(5, descriptionRu);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void updateCategoryStatusToClosed(int catId) throws DAOException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(SQL_UPDATE_CATEGORY_TO_CLOSED);
            st.setInt(1, catId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }

    }

    @Override
    public boolean updateCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String login, String categoryStatus) throws DAOException {
        boolean updated = false;
        PreparedStatement st1 = null;
        PreparedStatement st2 = null;
        ResultSet rs1;

        try {
            connection.setAutoCommit(false);
            st1 = connection.prepareStatement(SQL_SELECT_USER_ID_BY_LOGIN);
            st1.setString(1, login);
            rs1 = st1.executeQuery();
            if(rs1.next()){
               st2 = connection.prepareStatement(SQL_UPDATE_CATEGORY_INFO);
                st2.setString(1, login);
                st2.setString(2, titleEn);
                st2.setString(3, titleRu);
                st2.setString(4, descriptionEn);
                st2.setString(5, descriptionRu);
                st2.setString(6, categoryStatus.toLowerCase());
                st2.setInt(7, Integer.parseInt(categoryId));
                st2.executeUpdate();
                updated = true;
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new DAOException("SQL Error, check source", e);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st1);
            connection.closeStatement(st2);
        }
        return updated;
    }
}
