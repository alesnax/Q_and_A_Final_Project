package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.UserDAO;
import by.alesnax.qanda.dao.pool.WrappedConnection;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.FriendState;
import by.alesnax.qanda.entity.Role;
import by.alesnax.qanda.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alesnax on 04.12.2016.
 */
public class UserDAOImpl extends AbstractDAO<Integer, User> implements UserDAO {
    private static final String SQL_ADD_NEW_USER = "INSERT INTO `likeit_db`.`users` " +
            "(`login`, `password`, `surname`, `name`, `email`, `birthday`, `sex`, `role`, `state`, `country`, `city`, `status`) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";

    private static final String SQL_USER_AUTHORIZATION = "SELECT id, login, password, surname, name, email, birthday, sex, registration_date, " +
            "role, users.state, avatar, country, city, status, language, friends.state AS f_state\n" +
            "FROM users LEFT JOIN friends ON (users.id=friends.users_friend_id AND friends.users_id=users.id)\n" +
            "WHERE email=? AND password=? ";

    private static final String SQL_USER_SELECT_ALL = "SELECT id, login, password, surname, name, email, birthday, sex, registration_date, " +
            "role, users.state, avatar, country, city, status, language, friends.state AS f_state\n" +
            "FROM users LEFT JOIN friends ON (users.id=friends.users_friend_id AND friends.users_id=?)\n" +
            "WHERE id=? ";

    private static final String SQL_SELECT_FRIENDS = "SELECT id, login, name, surname, role, avatar, users.status AS u_status, friends.state AS f_state\n" +
            "FROM users JOIN friends ON users.id = friends.users_friend_id\n" +
            "WHERE friends.users_id=? AND friends.state='follower'\n" +
            "GROUP BY friends.state DESC, name, surname;";

    private static final String SQL_DELETE_USER_FROM_FRIENDS = "DELETE FROM friends WHERE users_id=? and users_friend_id=?;";

    private static final String SQL_ADD_FOLLOWER = "INSERT INTO `likeit_db`.`friends` (`users_id`, `users_friend_id`) VALUES (?, ?);";

    private static final String SQL_UPDATE_USER_INFO = "UPDATE users SET login=?, surname=?, name=?, email=?, birthday=?, sex=?, country=?, city=?, status=? WHERE id=?;";

    private static final String SQL_SELECT_USER_PASSWORD = "SELECT password from users WHERE id=?;";

    private static final String SQL_UPDATE_USER_PASSWORD = "UPDATE users SET password=? WHERE id=?;";

    private static final String SQL_UPDATE_USER_AVATAR = "UPDATE users SET avatar=? WHERE id=?;";

    private static final String SQL_UPDATE_USER_LANGUAGE = "UPDATE users SET language=? WHERE id=?;";


    private static final String ID = "id";
    private static final String LOGIN = "login";
    private static final String SURNAME = "surname";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String BIRTHDAY = "birthday";
    private static final String SEX = "sex";
    private static final String REG_DATE = "registration_date";
    private static final String ROLE = "role";
    private static final String STATE = "state";
    private static final String AVATAR = "avatar";
    private static final String COUNTRY = "country";
    private static final String CITY = "city";
    private static final String STATUS = "status";
    private static final String LANGUAGE = "language";
    private static final String PASSWORD = "password";

    private static final String USER_STATUS = "u_status";
    private static final String FRIEND_STATE = "f_state";


    private static final String USER_ROLE = "user";
    private static final String USER_STATE_ACTIVE = "active";
    private static final int MONTH_DIFFERENCE = 1;
    private static final int YEAR_DIFFERENCE = 1900;

    public UserDAOImpl(WrappedConnection connection) {
        super(connection);
    }

    @Override
    public User findEntityById(Integer id) throws DAOException {
        return null;
    }

    @Override
    public User takeUserById(int userId, int sessionUserId) throws DAOException {
        User user = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(SQL_USER_SELECT_ALL);
            st.setInt(1, sessionUserId);
            st.setInt(2, userId);
            rs = st.executeQuery();

            user = createUserFromResultSet(rs);
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return user;
    }

    @Override
    public void removeUserFromFriends(int removedUserId, int userId) throws DAOException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(SQL_DELETE_USER_FROM_FRIENDS);
            st.setInt(1, userId);
            st.setInt(2, removedUserId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public void addFollower(int followingUserId, int userId) throws DAOException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(SQL_ADD_FOLLOWER);
            st.setInt(1, userId);
            st.setInt(2, followingUserId);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAODuplicatedInfoException("User with id=" + followingUserId + "have already been follower of user id=" + userId, e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public User updateUserInfo(int userId, String login, String name, String surname, String email, String bDay, String bMonth,
                               String bYear, String sex, String country, String city, String status) throws DAOException {
        User user = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        PreparedStatement st1 = null;

        int day = Integer.parseInt(bDay);
        int month = Integer.parseInt(bMonth) - MONTH_DIFFERENCE;
        int year = Integer.parseInt(bYear) - YEAR_DIFFERENCE;
        int gender = Integer.parseInt(sex);

        try {
            connection.setAutoCommit(false);
            st = connection.prepareStatement(SQL_UPDATE_USER_INFO);
            st.setString(1, login);
            st.setString(2, surname);
            st.setString(3, name);
            st.setString(4, email);
            st.setDate(5, new Date(year, month, day));
            st.setInt(6, gender);
            if (country == null || country.isEmpty()) {
                st.setString(7, null);
            } else {
                st.setString(7, country);
            }
            if (city == null || city.isEmpty()) {
                st.setString(8, null);
            } else {
                st.setString(8, city);
            }
            if (status == null || status.isEmpty()) {
                st.setString(9, null);
            } else {
                st.setString(9, status);
            }
            st.setInt(10, userId);

            st1 = connection.prepareStatement(SQL_USER_SELECT_ALL);
            st1.setInt(1, userId);
            st1.setInt(2, userId);

            st.executeUpdate();
            rs = st1.executeQuery();
            user = createUserFromResultSet(rs);
            if (user == null) {
                throw new SQLException();
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new DAOException("SQL Error, check source", e);
            }
            throw new DAODuplicatedInfoException("User " + login + " with this login or email has already been registered ", e);
        } finally {
            connection.closeStatement(st);
        }
        return user;
    }

    @Override
    public boolean updatePassword(int userId, String password1, String password2) throws DAOException {
        boolean updated = false;

        String sha1Password = DigestUtils.sha1Hex(password1);

        ResultSet rs = null;
        PreparedStatement st = null;
        PreparedStatement st1 = null;

        try {
            st = connection.prepareStatement(SQL_SELECT_USER_PASSWORD);
            st.setInt(1, userId);
            rs = st.executeQuery();
            rs.beforeFirst();

            if (rs.next()) {
                String oldPassword = rs.getString(PASSWORD);
                if (oldPassword.equals(sha1Password)) {
                    String shaNewPassword = DigestUtils.sha1Hex(password2);
                    st1 = connection.prepareStatement(SQL_UPDATE_USER_PASSWORD);
                    st1.setString(1, shaNewPassword);
                    st1.setInt(2, userId);
                    st1.executeUpdate();
                    updated = true;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return updated;
    }

    @Override
    public void updateAvatar(int userId, String avatarPath) throws DAOException {
        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            st = connection.prepareStatement(SQL_UPDATE_USER_AVATAR);
            st.setString(1, avatarPath);
            st.setInt(2, userId);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public void updateUserLanguage(int userId, String language) throws DAOException {
        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            st = connection.prepareStatement(SQL_UPDATE_USER_LANGUAGE);
            st.setInt(2, userId);
            st.setString(1, language);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
    }


    @Override
    public void registerNewAccount(String login, String password, String name, String surname, String email, String bDay,
                                   String bMonth, String bYear, String sex, String country, String city, String status) throws DAOException {
        PreparedStatement st = null;
        String sha1Password = DigestUtils.sha1Hex(password);
        int day = Integer.parseInt(bDay);
        int month = Integer.parseInt(bMonth) - MONTH_DIFFERENCE;
        int year = Integer.parseInt(bYear) - YEAR_DIFFERENCE;
        int gender = Integer.parseInt(sex);

        try {
            st = connection.prepareStatement(SQL_ADD_NEW_USER);
            st.setString(1, login);
            st.setString(2, sha1Password);
            st.setString(3, surname);
            st.setString(4, name);
            st.setString(5, email);
            st.setDate(6, new Date(year, month, day));
            st.setInt(7, gender);
            st.setString(8, USER_ROLE);
            st.setString(9, USER_STATE_ACTIVE);
            if (country == null || country.isEmpty()) {
                st.setString(10, null);
            } else {
                st.setString(10, country);
            }
            if (city == null || city.isEmpty()) {
                st.setString(11, null);
            } else {
                st.setString(11, city);
            }
            if (status == null || status.isEmpty()) {
                st.setString(12, null);
            } else {
                st.setString(12, status);
            }

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAODuplicatedInfoException("User " + login + " has already registered ", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public User userAuthorization(String email, String password) throws DAOException {
        User user = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        String sha1Password = DigestUtils.sha1Hex(password);
        try {
            st = connection.prepareStatement(SQL_USER_AUTHORIZATION);
            st.setString(1, email);
            st.setString(2, sha1Password);
            rs = st.executeQuery();

            user = createUserFromResultSet(rs);
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return user;
    }

    @Override
    public List<Friend> takeFriends(int userId) throws DAOException {
        List<Friend> friends = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(SQL_SELECT_FRIENDS);
            st.setInt(1, userId);
            rs = st.executeQuery();

            friends = createFriendsFromResultSet(rs);
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return friends;

    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = null;
        if (!rs.next()) {
            user = null;
        } else {
            user = new User();
            user.setId(rs.getInt(ID));
            user.setLogin(rs.getString(LOGIN));
            user.setSurname(rs.getString(SURNAME));
            user.setName(rs.getString(NAME));
            user.setEmail(rs.getString(EMAIL));
            user.setBirthday(rs.getDate(BIRTHDAY));
            int sex = rs.getInt(SEX);
            if (sex == 1) {
                user.setSex(true);
            } else {
                user.setSex(false);
            }
            user.setRegistrationDate(rs.getDate(REG_DATE));
            user.setRole(Role.fromValue(rs.getString(ROLE)));
            user.setState(User.UserState.valueOf(rs.getString(STATE).toUpperCase()));
            user.setAvatar(rs.getString(AVATAR));
            user.setCountry(rs.getString(COUNTRY));
            user.setCity(rs.getString(CITY));
            user.setStatus(rs.getString(STATUS));
            user.setLanguage(User.Language.valueOf(rs.getString(LANGUAGE).toUpperCase()));
            String friendState = rs.getString(FRIEND_STATE);
            if (friendState != null) {
                user.setFriendState(FriendState.valueOf(friendState.toUpperCase()));
            }
        }
        return user;
    }

    private List<Friend> createFriendsFromResultSet(ResultSet rs) throws SQLException {
        List<Friend> friends = null;
        Friend friend = null;
        if (!rs.next()) {
            friends = null;
        } else {
            friends = new ArrayList<>();
            rs.beforeFirst();
            while (rs.next()) {
                friend = new Friend();
                friend.setId(rs.getInt(ID));
                friend.setLogin(rs.getString(LOGIN));
                friend.setSurname(rs.getString(SURNAME));
                friend.setName(rs.getString(NAME));
                friend.setRole(Role.fromValue(rs.getString(ROLE)));
                friend.setAvatar(rs.getString(AVATAR));
                friend.setUserStatus(rs.getString(USER_STATUS));
                String friendState = rs.getString(FRIEND_STATE);
                if (friendState != null) {
                    friend.setState(FriendState.valueOf(friendState.toUpperCase()));
                }
                friends.add(friend);
            }
        }
        return friends;
    }
}