package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.UserDAO;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.entity.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;
import java.util.*;

/**
 * Created by alesnax on 04.12.2016.
 */

public class UserDAOImpl extends AbstractDAO<Integer, User> implements UserDAO {
    private static final String SQL_ADD_NEW_USER = "INSERT INTO users " +
            "(`login`, `password`, `surname`, `name`, `email`, `birthday`, `sex`, `role`, `state`, `country`, `city`, `status`, `key_word`, `key_value`) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

    private static final String SQL_USER_AUTHORIZATION = "SELECT users.id, login, password, surname, name, email, birthday, sex, registration_date, " +
            "role, users.state, avatar, country, city, status, language, friends.state AS f_state, coalesce(bans.id, 0) AS ban_id, key_word, key_value\n" +
            "FROM users LEFT JOIN friends ON (users.id=friends.users_friend_id AND friends.users_id=users.id)" +
            "LEFT JOIN bans ON (users.id = bans.users_id AND current_timestamp() < bans.end)\n" +
            "WHERE email=? AND password=? AND users.state='active';";

    private static final String SQL_USER_SELECT_ALL = "SELECT users.id, login, password, surname, name, email, birthday, sex, registration_date, key_word, key_value, " +
            "role, users.state, avatar, country, city, status, language, friends.state AS f_state, coalesce(bans.id, 0) AS ban_id\n" +
            "FROM users LEFT JOIN friends ON (users.id=friends.users_friend_id AND friends.users_id=?)" +
            "LEFT JOIN bans ON (users.id = bans.users_id AND current_timestamp() < bans.end)\n" +
            "WHERE users.id=? AND users.state='active';";

    private static final String SQL_SELECT_FOLLOWING_USERS = "SELECT sql_calc_found_rows users.id, login, name, surname, role, avatar, " +
            "users.status AS u_status, friends.state AS f_state, AVG(rates.value) AS rate\n" +
            "FROM users JOIN friends ON users.id = friends.users_friend_id AND friends.state='follower' " +
            "LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=?)\n" +
            "WHERE friends.users_id=? AND users.state = 'active'\n" +
            "GROUP BY users.id ORDER BY name, surname LIMIT ?,?;\n";

    private static final String SQL_SELECT_FOLLOWERS = "SELECT sql_calc_found_rows users.id, login, name, surname, role, avatar, users.status AS u_status, AVG(rates.value) AS rate\n" +
            "FROM users JOIN friends ON users.id = friends.users_id AND friends.state='follower' \n" +
            "LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=?)\n" +
            "WHERE friends.users_friend_id=? AND users.state = 'active'\n" +
            "GROUP BY users.id ORDER BY name, surname LIMIT ?,?;";

    private static final String SQL_DELETE_USER_FROM_FRIENDS = "DELETE FROM friends WHERE users_id=? and users_friend_id=?;";

    private static final String SQL_ADD_FOLLOWER = "INSERT INTO friends (`users_id`, `users_friend_id`) VALUES (?, ?);";

    private static final String SQL_UPDATE_USER_INFO = "UPDATE users SET login=?, surname=?, name=?, email=?, birthday=?, sex=?, country=?, city=?, status=?, key_word=?, key_value=? WHERE id=?;";

    private static final String SQL_SELECT_USER_PASSWORD = "SELECT password from users WHERE id=?;";

    private static final String SQL_UPDATE_USER_PASSWORD = "UPDATE users SET password=? WHERE id=?;";

    private static final String SQL_UPDATE_USER_AVATAR = "UPDATE users SET avatar=? WHERE id=?;";

    private static final String SQL_UPDATE_USER_LANGUAGE = "UPDATE users SET language=? WHERE id=?;";

    private static final String SQL_UPDATE_USER_PASSWORD_BY_EMAIL_AND_KEY_WORD = "UPDATE users SET password=?, users.state='active' WHERE email=? AND key_word=? AND key_value=?;";

    private static final String SQL_SELECT_BEST_USERS = "SELECT sql_calc_found_rows users.id, users.name, users.surname, users.avatar, users.role, users.login, users.state, users.status AS u_status, AVG(rates.value) AS rate\n" +
            "FROM users LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=users.id)\n" +
            "WHERE users.state = 'active' GROUP BY users.id ORDER BY rate DESC, name, surname LIMIT ?,?;";

    private static final String SQL_SELECT_USER_STATISTICS = "SELECT (SELECT COUNT(friends.users_friend_id) FROM friends WHERE users_id=?) AS following_users_count,\n" +
            "(SELECT COUNT(friends.users_id) FROM friends WHERE users_friend_id=?) AS followers_count,\n" +
            "(SELECT AVG(rates.value) AS rate\n" +
            "FROM users LEFT JOIN posts ON (users.id = posts.users_id AND posts.status!='deleted') LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=?)\n" +
            "WHERE users.state = 'active' AND users.id=? GROUP BY users.id) as rate,\n" +
            "(SELECT COUNT(posts.id) FROM posts WHERE users_id=? AND type='question' and status!='deleted') AS question_count,\n" +
            "(SELECT COUNT(posts.id) FROM posts WHERE users_id=? AND type='answer' and status!='deleted') AS answer_count;";

    private static final String SQL_SELECT_FOUND_ROWS = "SELECT FOUND_ROWS();";

    private static final String SQL_UPDATE_USER_STATE_TO_DELETED = "UPDATE users SET state='deleted', avatar='/img/no_avatar.jpg' WHERE id=? AND password=?;";

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
    private static final String KEY_WORD_TYPE = "key_word";
    private static final String KEY_WORD_VALUE = "key_value";

    private static final String USER_STATUS = "u_status";
    private static final String FRIEND_STATE = "f_state";
    private static final String FOLLOWING_USERS_COUNT = "following_users_count";
    private static final String FOLLOWERS_COUNT = "followers_count";
    private static final String USER_RATE = "rate";
    private static final String USER_QUESTIONS_COUNT = "question_count";
    private static final String USER_ANSWERS_COUNT = "answer_count";
    private static final String BAN_ID = "ban_id";

    private static final String USER_ROLE = "user";
    private static final String USER_STATE_ACTIVE = "active";
    private static final int MONTH_DIFFERENCE = 1;

    public UserDAOImpl(WrappedConnection connection) {
        super(connection);
    }

    @Override
    public User findEntityById(Integer id) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public User takeUserById(int userId, int sessionUserId) throws DAOException {
        User user = null;

        PreparedStatement st = null;
        ResultSet rs;
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
                               String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws DAOException {
        User user = null;
        ResultSet rs;
        PreparedStatement st = null;
        PreparedStatement st1 = null;

        int day = Integer.parseInt(bDay);
        int month = Integer.parseInt(bMonth) - MONTH_DIFFERENCE;
        int year = Integer.parseInt(bYear);
        int gender = Integer.parseInt(sex);

        Timestamp birth = new Timestamp(new GregorianCalendar(year, month, day).getTimeInMillis());

        try {
            connection.setAutoCommit(false);
            st = connection.prepareStatement(SQL_UPDATE_USER_INFO);
            st.setString(1, login);
            st.setString(2, surname);
            st.setString(3, name);
            st.setString(4, email);
            st.setTimestamp(5, birth);
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
            st.setString(10, User.KeyWord.fromValue(Integer.parseInt(keyWordType)).name().toLowerCase());
            st.setString(11, keyWordValue);
            st.setInt(12, userId);


            st1 = connection.prepareStatement(SQL_USER_SELECT_ALL);
            st1.setInt(1, userId);
            st1.setInt(2, userId);

            st.executeUpdate();
            rs = st1.executeQuery();
            user = createUserFromResultSet(rs);
            int banId;
            if(user != null){
                banId = rs.getInt(BAN_ID);
                if(banId != 0){
                    user.setBanned(true);
                }
            } else {
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
            connection.closeStatement(st1);
        }
        return user;
    }

    @Override
    public boolean updatePassword(int userId, String password1, String password2) throws DAOException {
        boolean updated = false;

        String sha1Password = DigestUtils.sha1Hex(password1);

        ResultSet rs;
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
            connection.closeStatement(st1);
        }
        return updated;
    }

    @Override
    public void updateAvatar(int userId, String avatarPath) throws DAOException {
        PreparedStatement st = null;

        try {
            st = connection.prepareStatement(SQL_UPDATE_USER_AVATAR);
            st.setInt(2, userId);
            st.setString(1, avatarPath);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public void updateUserLanguage(int userId, String language) throws DAOException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(SQL_UPDATE_USER_LANGUAGE);
            st.setString(1, language);
            st.setInt(2, userId);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public boolean updateUserPassWordByKeyword(String changedPassword, String email, String keyWordType, String keyWordValue) throws DAOException {
        PreparedStatement st = null;
        boolean updated = false;
        String shaPassword = DigestUtils.sha1Hex(changedPassword);
        try {
            st = connection.prepareStatement(SQL_UPDATE_USER_PASSWORD_BY_EMAIL_AND_KEY_WORD);
            st.setString(1, shaPassword);
            st.setString(2, email);
            st.setString(3, User.KeyWord.fromValue(Integer.parseInt(keyWordType)).name().toLowerCase());
            st.setString(4, keyWordValue);

            int count = st.executeUpdate();
            if(count == 1){
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
    public boolean updateUserStateToDeleted(int userId, String password) throws DAOException {

        PreparedStatement st = null;
        boolean updated = false;
        String shaPassword = DigestUtils.sha1Hex(password);
        try {
            st = connection.prepareStatement(SQL_UPDATE_USER_STATE_TO_DELETED);
            st.setInt(1, userId);
            st.setString(2, shaPassword);

            int count = st.executeUpdate();
            if(count == 1){
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
    public PaginatedList<Friend> takeBestUsers(int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> bestUsers = new PaginatedList<>();
        List<Friend> items;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_BEST_USERS);
            st.setInt(1, startUser);
            st.setInt(2, usersPerPage);
            rs = st.executeQuery();

            if (!rs.next()) {
                items = null;
            } else {
                rs.beforeFirst();
                items = new ArrayList<>();
                Friend bestUser;
                while (rs.next()) {
                    bestUser = new Friend();
                    bestUser.setId(rs.getInt(ID));
                    bestUser.setLogin(rs.getString(LOGIN));
                    bestUser.setSurname(rs.getString(SURNAME));
                    bestUser.setName(rs.getString(NAME));
                    bestUser.setAvatar(rs.getString(AVATAR));
                    bestUser.setRole(Role.fromValue(rs.getString(ROLE)));
                    bestUser.setUserRate(rs.getDouble(USER_RATE));
                    bestUser.setUserStatus(rs.getString(USER_STATUS));
                    items.add(bestUser);
                }
            }

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                bestUsers.setItems(items);
                bestUsers.setTotalCount(rs1.getInt(1));
                bestUsers.setItemStart(startUser);
                bestUsers.setItemsPerPage(usersPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return bestUsers;
    }


    @Override
    public void registerNewAccount(String login, String password, String name, String surname, String email, String bDay,
                                   String bMonth, String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws DAOException {
        PreparedStatement st = null;
        String sha1Password = DigestUtils.sha1Hex(password);
        int day = Integer.parseInt(bDay);
        int month = Integer.parseInt(bMonth) - MONTH_DIFFERENCE;
        int year = Integer.parseInt(bYear);
        int gender = Integer.parseInt(sex);


        Timestamp birth = new Timestamp(new GregorianCalendar(year, month, day).getTimeInMillis());

        try {
            st = connection.prepareStatement(SQL_ADD_NEW_USER);
            st.setString(1, login);
            st.setString(2, sha1Password);
            st.setString(3, surname);
            st.setString(4, name);
            st.setString(5, email);
            st.setTimestamp(6, birth);
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
            st.setString(13, User.KeyWord.fromValue(Integer.parseInt(keyWordType)).name().toLowerCase());
            st.setString(14, keyWordValue);

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
        ResultSet rs;
        String sha1Password = DigestUtils.sha1Hex(password);
        try {
            st = connection.prepareStatement(SQL_USER_AUTHORIZATION);
            st.setString(1, email);
            st.setString(2, sha1Password);
            rs = st.executeQuery();
            user = createUserFromResultSet(rs);
            int banId = 0;
            if(user != null){
                banId = rs.getInt(BAN_ID);
            }
            if(banId != 0){
                user.setBanned(true);
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return user;
    }

    @Override
    public UserStatistics findUserStatistics(int userId) throws DAOException {
        UserStatistics statistics = new UserStatistics();

        PreparedStatement st = null;
        ResultSet rs;
        try {
            st = connection.prepareStatement(SQL_SELECT_USER_STATISTICS);
            st.setInt(1, userId);
            st.setInt(2, userId);
            st.setInt(3, userId);
            st.setInt(4, userId);
            st.setInt(5, userId);
            st.setInt(6, userId);
            rs = st.executeQuery();

            if(rs.next()){
                statistics.setFollowingUsersCount(rs.getInt(FOLLOWING_USERS_COUNT));
                statistics.setFollowersCount(rs.getInt(FOLLOWERS_COUNT));
                statistics.setRate(rs.getDouble(USER_RATE));
                statistics.setQuestionsCount(rs.getInt(USER_QUESTIONS_COUNT));
                statistics.setAnswersCount(rs.getInt(USER_ANSWERS_COUNT));
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return statistics;
    }

    @Override
    public PaginatedList<Friend> takeFriends(int userId, int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> friends = new PaginatedList<>();
        List<Friend> items;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_FOLLOWING_USERS);
            st.setInt(1, userId);
            st.setInt(2, userId);
            st.setInt(3, startUser);
            st.setInt(4, usersPerPage);
            rs = st.executeQuery();
            items = createFriendsFromResultSet(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                friends.setItems(items);
                friends.setTotalCount(rs1.getInt(1));
                friends.setItemsPerPage(usersPerPage);
                friends.setItemStart(startUser);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return friends;
    }

    @Override
    public PaginatedList<Friend> takeFollowers(int userId, int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> followers = new PaginatedList<>();
        List<Friend> items;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_FOLLOWERS);
            st.setInt(1, userId);
            st.setInt(2, userId);
            st.setInt(3, startUser);
            st.setInt(4, usersPerPage);
            rs = st.executeQuery();
            items = createFollowersFromResultSet(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                followers.setTotalCount(rs1.getInt(1));
                followers.setItemsPerPage(usersPerPage);
                followers.setItems(items);
                followers.setItemStart(startUser);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return followers;
    }

    private List<Friend> createFollowersFromResultSet(ResultSet rs) throws SQLException {
        List<Friend> followers;
        Friend follower;
        if (!rs.next()) {
            followers = null;
        } else {
            followers = new ArrayList<>();
            rs.beforeFirst();
            while (rs.next()) {
                follower = new Friend();
                follower.setId(rs.getInt(ID));
                follower.setLogin(rs.getString(LOGIN));
                follower.setSurname(rs.getString(SURNAME));
                follower.setName(rs.getString(NAME));
                follower.setRole(Role.fromValue(rs.getString(ROLE)));
                follower.setAvatar(rs.getString(AVATAR));
                follower.setUserStatus(rs.getString(USER_STATUS));
                follower.setUserRate(rs.getDouble(USER_RATE));
                followers.add(follower);
            }
        }
        return followers;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user;
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
                user.setFriend(true);
            }
            user.setKeyWord(User.KeyWord.valueOf(rs.getString(KEY_WORD_TYPE).toUpperCase()));
            user.setKeyWordValue(rs.getString(KEY_WORD_VALUE));
        }
        return user;
    }

    private List<Friend> createFriendsFromResultSet(ResultSet rs) throws SQLException {
        List<Friend> friends;
        Friend friend;
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
                    friend.setFriend(true);
                }
                friend.setUserRate(rs.getDouble(USER_RATE));
                friends.add(friend);

            }
        }
        return friends;
    }
}