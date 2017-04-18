package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.DAOException;
import by.alesnax.qanda.dao.UserDAO;
import by.alesnax.qanda.entity.*;
import by.alesnax.qanda.pagination.PaginatedList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Implements UserDAO interface and extends AbstractDAO class.
 * Implements all methods of UserDAO. Processes operations of
 * manipulating with information that stores in databases and related with user information.
 * Methods of classes sends SQL statements to the database and
 * get result as ResultSet of objects or number of processed rows in database.
 *
 * @author Aliaksandr Nakhankou
 * @see by.alesnax.qanda.dao.AbstractDAO
 * @see by.alesnax.qanda.dao.PostDAO
 */
public class UserDAOImpl extends AbstractDAO<Integer, User> implements UserDAO {
    private static Logger logger = LogManager.getLogger(UserDAOImpl.class);

    /**
     * SQL query that inserts information about new user.
     */
    private static final String SQL_INSERT_NEW_USER = "INSERT INTO users " +
            "(`login`, `password`, `surname`, `name`, `email`, `birthday`, `sex`, `role`, `state`, `country`, `city`, `status`, `key_word`, `key_value`) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

    /**
     * SQL query that selects information about user when email and password matches with users' params.
     */
    private static final String SQL_USER_AUTHORIZATION = "SELECT users.id, login, password, surname, name, email, birthday, sex, registration_date, " +
            "role, users.state, avatar, country, city, status, language, friends.state AS f_state, coalesce(bans.id, 0) AS ban_id, key_word, key_value\n" +
            "FROM users LEFT JOIN friends ON (users.id=friends.users_friend_id AND friends.users_id=users.id)" +
            "LEFT JOIN bans ON (users.id = bans.users_id AND current_timestamp() < bans.end)\n" +
            "WHERE email=? AND password=? AND users.state='active';";

    /**
     * SQL query that selects information all info about user except password and registration date
     */
    private static final String SQL_USER_SELECT_ALL = "SELECT users.id, login, password, surname, name, email, birthday, sex, registration_date, key_word, key_value, " +
            "role, users.state, avatar, country, city, status, language, friends.state AS f_state, coalesce(bans.id, 0) AS ban_id\n" +
            "FROM users LEFT JOIN friends ON (users.id=friends.users_friend_id AND friends.users_id=?)" +
            "LEFT JOIN bans ON (users.id = bans.users_id AND current_timestamp() < bans.end)\n" +
            "WHERE users.id=? AND users.state='active';";

    /**
     * SQL query that selects information about users who marked as 'following' by definite user,
     * finds limited list of users.
     */
    private static final String SQL_SELECT_FOLLOWING_USERS = "SELECT sql_calc_found_rows users.id, login, name, surname, role, avatar, " +
            "users.status AS u_status, friends.state AS f_state, AVG(rates.value) AS rate\n" +
            "FROM users JOIN friends ON users.id = friends.users_friend_id AND friends.state='follower' " +
            "LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=?)\n" +
            "WHERE friends.users_id=? AND users.state = 'active'\n" +
            "GROUP BY users.id ORDER BY name, surname LIMIT ?,?;\n";

    /**
     * SQL query that selects information about users who marked as 'following' definite user,
     * finds limited list of users.
     */
    private static final String SQL_SELECT_FOLLOWERS = "SELECT sql_calc_found_rows users.id, login, name, surname, role, avatar, users.status AS u_status, AVG(rates.value) AS rate\n" +
            "FROM users JOIN friends ON users.id = friends.users_id AND friends.state='follower' \n" +
            "LEFT JOIN posts ON users.id = posts.users_id LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=?)\n" +
            "WHERE friends.users_friend_id=? AND users.state = 'active'\n" +
            "GROUP BY users.id ORDER BY name, surname LIMIT ?,?;";

    /**
     * SQL query that deletes row about 'following' status of other user
     */
    private static final String SQL_DELETE_USER_FROM_FRIENDS = "DELETE FROM friends WHERE users_id=? and users_friend_id=?;";

    /**
     * SQL query that inserts row about 'following' status of other user
     */
    private static final String SQL_ADD_FOLLOWER = "INSERT INTO friends (`users_id`, `users_friend_id`) VALUES (?, ?);";

    /**
     * SQL query that updates information about user
     */
    private static final String SQL_UPDATE_USER_INFO = "UPDATE users SET login=?, surname=?, name=?, email=?, birthday=?, sex=?, country=?, city=?, status=?, key_word=?, key_value=? WHERE id=?;";

    /**
     * SQL query that selects password by user's id for later comparison in the same transaction
     */
    private static final String SQL_SELECT_USER_PASSWORD = "SELECT password from users WHERE id=?;";

    /**
     * SQL query that updates user's password
     */
    private static final String SQL_UPDATE_USER_PASSWORD = "UPDATE users SET password=? WHERE id=?;";

    /**
     * SQL query that updates user's avatar link
     */
    private static final String SQL_UPDATE_USER_AVATAR = "UPDATE users SET avatar=? WHERE id=?;";

    /**
     * SQL query that updates user's used language while session
     */
    private static final String SQL_UPDATE_USER_LANGUAGE = "UPDATE users SET language=? WHERE id=?;";

    /**
     * SQL query that updates user's password by email and key word while password recovering
     */
    private static final String SQL_UPDATE_USER_PASSWORD_BY_EMAIL_AND_KEY_WORD = "UPDATE users SET password=?, users.state='active' WHERE email=? AND key_word=? AND key_value=?;";

    /**
     * SQL query that selects information about users with the highest average marks,
     * selects limited list of posts sorted in reversed order by mark from the highest to the lowest.
     */
    private static final String SQL_SELECT_BEST_USERS = "SELECT sql_calc_found_rows users.id, users.name, users.surname, users.avatar, users.role, users.login, users.state, users.status AS u_status, AVG(rates.value) AS rate\n" +
            "FROM users JOIN posts ON users.id = posts.users_id JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=users.id)\n" +
            "WHERE users.state = 'active' GROUP BY users.id ORDER BY rate DESC, name, surname LIMIT ?,?;";

    /**
     * SQL query that selects user's statistics: number of followers, following users, answers, questions
     * and average user's rate
     */
    private static final String SQL_SELECT_USER_STATISTICS = "SELECT (SELECT COUNT(friends.users_friend_id) FROM friends LEFT JOIN users ON friends.users_friend_id=users.id WHERE users_id=? AND users.state!='deleted') AS following_users_count,\n" +
            "(SELECT COUNT(friends.users_id) FROM friends LEFT JOIN users ON friends.users_id=users.id WHERE users_friend_id=? AND users.state!='deleted') AS followers_count,\n" +
            "(SELECT AVG(rates.value) AS rate\n" +
            "FROM users LEFT JOIN posts ON (users.id = posts.users_id AND posts.status!='deleted') LEFT JOIN rates ON (posts.id = rates.posts_id AND rates.users_id!=?)\n" +
            "WHERE users.state = 'active' AND users.id=? GROUP BY users.id) as rate,\n" +
            "(SELECT COUNT(posts.id) FROM posts WHERE users_id=? AND type='question' and status!='deleted') AS question_count,\n" +
            "(SELECT COUNT(posts.id) FROM posts WHERE users_id=? AND type='answer' and status!='deleted') AS answer_count;";

    /**
     * SQL query that selects number of found rows while previous statement.
     * Statement should be located in one transaction with previous one.
     */
    private static final String SQL_SELECT_FOUND_ROWS = "SELECT FOUND_ROWS();";

    /**
     * SQL query that updates user's state to 'deleted'
     */
    private static final String SQL_UPDATE_USER_STATE_TO_DELETED = "UPDATE users SET state='deleted', avatar='/img/no_avatar.jpg' WHERE id=? AND password=?;";

    /**
     * Names of attributes processing in SQL statements related with information about user
     */
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

    /**
     * Names of attributes processing in SQL statements related with information
     * about following users and user's rate
     */
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

    /**
     * value of difference between month in realty and Date class.
     */
    private static final int MONTH_DIFFERENCE = 1;

    /**
     * Constructs UserDAOImpl class, used in package by DAOFactory
     */
    UserDAOImpl(){}

    /**
     * method finds entity by id, not implemented for UserDAOImpl, throws UnsupportedOperationException if called
     */
    @Override
    public User findEntityById(Integer id) throws DAOException {
        throw new UnsupportedOperationException();
    }

    /**
     * method creates PreparedStatement for selecting information about user by id
     *
     * @param userId id of user
     * @param sessionUserId id of session user for checking if user has following status
     * @return user
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public User takeUserById(int userId, int sessionUserId) throws DAOException {
        User user = null;

        PreparedStatement selectUserStatement = null;
        ResultSet userResultSet;
        try {
            selectUserStatement = connection.prepareStatement(SQL_USER_SELECT_ALL);
            selectUserStatement.setInt(1, sessionUserId);
            selectUserStatement.setInt(2, userId);
            userResultSet = selectUserStatement.executeQuery();

            user = createUserFromResultSet(userResultSet);
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectUserStatement);
        }
        return user;
    }

    /**
     * method creates PreparedStatement for deleting row about 'following' status between users
     *
     * @param removedUserId id of 'following' user
     * @param userId id of user who deletes row
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void removeUserFromFriends(int removedUserId, int userId) throws DAOException {
        PreparedStatement deleteFollowingUserStatement = null;
        try {
            deleteFollowingUserStatement = connection.prepareStatement(SQL_DELETE_USER_FROM_FRIENDS);
            deleteFollowingUserStatement.setInt(1, userId);
            deleteFollowingUserStatement.setInt(2, removedUserId);
            deleteFollowingUserStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(deleteFollowingUserStatement);
        }
    }

    /**
     * method creates PreparedStatement for inserting row about 'following' status between users
     *
     * @param followingUserId id of 'following' user
     * @param userId id of user who adds new row
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void addFollower(int followingUserId, int userId) throws DAOException {
        PreparedStatement insertFollowerStatement = null;
        try {
            insertFollowerStatement = connection.prepareStatement(SQL_ADD_FOLLOWER);
            insertFollowerStatement.setInt(1, userId);
            insertFollowerStatement.setInt(2, followingUserId);

            insertFollowerStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAODuplicatedInfoException("User with id=" + followingUserId + "have already been follower of user id=" + userId, e);
        } finally {
            connection.closeStatement(insertFollowerStatement);
        }
    }

    /**
     * method creates PreparedStatement for updating information about user except password, language and
     * state and another statement for selecting updated info if those was successfully updated.
     *
     * @return user
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public User updateUserInfo(int userId, String login, String name, String surname, String email, String bDay, String bMonth,
                               String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws DAOException {
        User user = null;
        PreparedStatement updateUserStatement = null;
        PreparedStatement selectUserStatement = null;
        ResultSet userResultSet;

        int day = Integer.parseInt(bDay);
        int month = Integer.parseInt(bMonth) - MONTH_DIFFERENCE;
        int year = Integer.parseInt(bYear);
        int gender = Integer.parseInt(sex);

        Timestamp birth = new Timestamp(new GregorianCalendar(year, month, day).getTimeInMillis());

        try {
            connection.setAutoCommit(false);
            updateUserStatement = connection.prepareStatement(SQL_UPDATE_USER_INFO);
            updateUserStatement.setString(1, login);
            updateUserStatement.setString(2, surname);
            updateUserStatement.setString(3, name);
            updateUserStatement.setString(4, email);
            updateUserStatement.setTimestamp(5, birth);
            updateUserStatement.setInt(6, gender);
            if (country == null || country.isEmpty()) {
                updateUserStatement.setString(7, null);
            } else {
                updateUserStatement.setString(7, country);
            }
            if (city == null || city.isEmpty()) {
                updateUserStatement.setString(8, null);
            } else {
                updateUserStatement.setString(8, city);
            }
            if (status == null || status.isEmpty()) {
                updateUserStatement.setString(9, null);
            } else {
                updateUserStatement.setString(9, status);
            }
            updateUserStatement.setString(10, User.KeyWord.fromValue(Integer.parseInt(keyWordType)).name().toLowerCase());
            updateUserStatement.setString(11, keyWordValue);
            updateUserStatement.setInt(12, userId);


            selectUserStatement = connection.prepareStatement(SQL_USER_SELECT_ALL);
            selectUserStatement.setInt(1, userId);
            selectUserStatement.setInt(2, userId);

            updateUserStatement.executeUpdate();
            userResultSet = selectUserStatement.executeQuery();
            user = createUserFromResultSet(userResultSet);
            int banId;
            if(user != null){
                banId = userResultSet.getInt(BAN_ID);
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
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAODuplicatedInfoException("User " + login + " with this login or email has already been registered ", e);
        } finally {
            connection.closeStatement(updateUserStatement);
            connection.closeStatement(selectUserStatement);
        }
        return user;
    }

    /**
     * method creates PreparedStatement for updating password if old password match with inputted by user
     *
     * @param userId id of user
     * @param password1 old password
     * @param password2 new password
     * @return true if old password was matched and updated, false otherwise
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public boolean updatePassword(int userId, String password1, String password2) throws DAOException {
        boolean updated = false;

        PreparedStatement selectPasswordStatement = null;
        ResultSet oldPasswordResultSet;
        PreparedStatement updatePasswordStatement = null;

        try {
            selectPasswordStatement = connection.prepareStatement(SQL_SELECT_USER_PASSWORD);
            selectPasswordStatement.setInt(1, userId);
            oldPasswordResultSet = selectPasswordStatement.executeQuery();
            oldPasswordResultSet.beforeFirst();

            if (oldPasswordResultSet.next()) {
                String oldPassword = oldPasswordResultSet.getString(PASSWORD);
                if (oldPassword.equals(password1)) {
                    updatePasswordStatement = connection.prepareStatement(SQL_UPDATE_USER_PASSWORD);
                    updatePasswordStatement.setString(1, password2);
                    updatePasswordStatement.setInt(2, userId);
                    updatePasswordStatement.executeUpdate();
                    updated = true;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectPasswordStatement);
            connection.closeStatement(updatePasswordStatement);
        }
        return updated;
    }

    /**
     * method creates PreparedStatement for updating user's avatar path
     *
     * @param userId id of user
     * @param avatarPath avatar relative path in a server
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void updateAvatar(int userId, String avatarPath) throws DAOException {
        PreparedStatement updateAvatarStatement = null;

        try {
            updateAvatarStatement = connection.prepareStatement(SQL_UPDATE_USER_AVATAR);
            updateAvatarStatement.setInt(2, userId);
            updateAvatarStatement.setString(1, avatarPath);

            updateAvatarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(updateAvatarStatement);
        }
    }

    /**
     * method creates PreparedStatement for updating user's session language
     *
     * @param userId id of user
     * @param language updating language
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void updateUserLanguage(int userId, String language) throws DAOException {
        PreparedStatement updateLanguageStatement = null;
        try {
            updateLanguageStatement = connection.prepareStatement(SQL_UPDATE_USER_LANGUAGE);
            updateLanguageStatement.setString(1, language);
            updateLanguageStatement.setInt(2, userId);

            updateLanguageStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(updateLanguageStatement);
        }
    }

    /**
     * method creates PreparedStatement for updating user's password by key word and email
     * while password recovering action
     *
     * @param changedPassword new generated temporary password
     * @param email user's email
     * @param keyWordType type of key word
     * @param keyWordValue value of key word
     * @return true if key word and email matches, flase otherwise
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public boolean updateUserPassWordByKeyword(String changedPassword, String email, String keyWordType, String keyWordValue) throws DAOException {
        PreparedStatement updatePasswordByKeyWordsStatement = null;
        boolean updated = false;
        try {
            updatePasswordByKeyWordsStatement = connection.prepareStatement(SQL_UPDATE_USER_PASSWORD_BY_EMAIL_AND_KEY_WORD);
            updatePasswordByKeyWordsStatement.setString(1, changedPassword);
            updatePasswordByKeyWordsStatement.setString(2, email);
            updatePasswordByKeyWordsStatement.setString(3, User.KeyWord.fromValue(Integer.parseInt(keyWordType)).name().toLowerCase());
            updatePasswordByKeyWordsStatement.setString(4, keyWordValue);

            int count = updatePasswordByKeyWordsStatement.executeUpdate();
            if(count == 1){
                updated = true;
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(updatePasswordByKeyWordsStatement);
        }
        return updated;
    }

    /**
     * method creates PreparedStatement for updating user's state to "deleted',
     * in other words it's analogue of 'delete profile' operation
     * updates user avatar to default stub too.
     *
     * @param userId id of user
     * @param password user's password
     * @return true if user's status was updated, false otherwise
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public boolean updateUserStateToDeleted(int userId, String password) throws DAOException {
        PreparedStatement updateUserToDeletedStatement = null;
        boolean updated = false;

        try {
            updateUserToDeletedStatement = connection.prepareStatement(SQL_UPDATE_USER_STATE_TO_DELETED);
            updateUserToDeletedStatement.setInt(1, userId);
            updateUserToDeletedStatement.setString(2, password);

            int count = updateUserToDeletedStatement.executeUpdate();
            if(count == 1){
                updated = true;
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(updateUserToDeletedStatement);
        }
        return updated;
    }

    /**
     * creates PreparedStatement for selecting limited number of rows with information
     * about best users sorted by average mark in reverse order
     * and statement for selecting total rows count of statement
     *
     * @param startUser number of first selected row
     * @param usersPerPage number of selected rows
     * @return container with list of users(cut) and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Friend> takeBestUsers(int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> bestUsers = new PaginatedList<>();
        List<Friend> items;

        PreparedStatement selectBestUsersStatement = null;
        ResultSet bestUsersResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectBestUsersStatement = connection.prepareStatement(SQL_SELECT_BEST_USERS);
            selectBestUsersStatement.setInt(1, startUser);
            selectBestUsersStatement.setInt(2, usersPerPage);
            bestUsersResultSet = selectBestUsersStatement.executeQuery();

            if (!bestUsersResultSet.next()) {
                items = null;
            } else {
                bestUsersResultSet.beforeFirst();
                items = new ArrayList<>();
                Friend bestUser;
                while (bestUsersResultSet.next()) {
                    bestUser = new Friend();
                    bestUser.setId(bestUsersResultSet.getInt(ID));
                    bestUser.setLogin(bestUsersResultSet.getString(LOGIN));
                    bestUser.setSurname(bestUsersResultSet.getString(SURNAME));
                    bestUser.setName(bestUsersResultSet.getString(NAME));
                    bestUser.setAvatar(bestUsersResultSet.getString(AVATAR));
                    bestUser.setRole(Role.fromValue(bestUsersResultSet.getString(ROLE)));
                    bestUser.setUserRate(bestUsersResultSet.getDouble(USER_RATE));
                    bestUser.setUserStatus(bestUsersResultSet.getString(USER_STATUS));
                    items.add(bestUser);
                }
            }

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(foundRowsResultSet.next()){
                bestUsers.setItems(items);
                bestUsers.setTotalCount(foundRowsResultSet.getInt(1));
                bestUsers.setItemStart(startUser);
                bestUsers.setItemsPerPage(usersPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectBestUsersStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return bestUsers;
    }

    /**
     * creates PreparedStatements that inserts or data about new registered user
     *
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void registerNewAccount(String login, String password, String name, String surname, String email, String bDay,
                                   String bMonth, String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws DAOException {
        PreparedStatement insertUserStatement = null;

        int day = Integer.parseInt(bDay);
        int month = Integer.parseInt(bMonth) - MONTH_DIFFERENCE;
        int year = Integer.parseInt(bYear);
        int gender = Integer.parseInt(sex);

        Timestamp birth = new Timestamp(new GregorianCalendar(year, month, day).getTimeInMillis());
        try {
            insertUserStatement = connection.prepareStatement(SQL_INSERT_NEW_USER);
            insertUserStatement.setString(1, login);
            insertUserStatement.setString(2, password);
            insertUserStatement.setString(3, surname);
            insertUserStatement.setString(4, name);
            insertUserStatement.setString(5, email);
            insertUserStatement.setTimestamp(6, birth);
            insertUserStatement.setInt(7, gender);
            insertUserStatement.setString(8, USER_ROLE);
            insertUserStatement.setString(9, USER_STATE_ACTIVE);
            if (country == null || country.isEmpty()) {
                insertUserStatement.setString(10, null);
            } else {
                insertUserStatement.setString(10, country);
            }
            if (city == null || city.isEmpty()) {
                insertUserStatement.setString(11, null);
            } else {
                insertUserStatement.setString(11, city);
            }
            if (status == null || status.isEmpty()) {
                insertUserStatement.setString(12, null);
            } else {
                insertUserStatement.setString(12, status);
            }
            insertUserStatement.setString(13, User.KeyWord.fromValue(Integer.parseInt(keyWordType)).name().toLowerCase());
            insertUserStatement.setString(14, keyWordValue);

            insertUserStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAODuplicatedInfoException("User " + login + " has already registered ", e);
        } finally {
            connection.closeStatement(insertUserStatement);
        }
    }

    /**
     * creates PreparedStatements that selects information about user if email and password matches with
     * sent data
     *
     * @param email user's email
     * @param password user's password
     * @return user or null if row with sch email and password wasn't found
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public User userAuthorization(String email, String password) throws DAOException {
        User user = null;

        PreparedStatement selectUserInfoStatement = null;
        ResultSet userResultSet;
        try {
            selectUserInfoStatement = connection.prepareStatement(SQL_USER_AUTHORIZATION);
            selectUserInfoStatement.setString(1, email);
            selectUserInfoStatement.setString(2, password);
            userResultSet = selectUserInfoStatement.executeQuery();
            user = createUserFromResultSet(userResultSet);
            int banId = 0;
            if(user != null){
                banId = userResultSet.getInt(BAN_ID);
            }
            if(banId != 0){
                user.setBanned(true);
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectUserInfoStatement);
        }
        return user;
    }

    /**
     * method creates statement for selecting user's statistics: number of questions and answers, followers
     * and 'following' users, average rate
     *
     * @param userId id of user
     * @return user's statistics instance
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public UserStatistics findUserStatistics(int userId) throws DAOException {
        UserStatistics statistics = new UserStatistics();

        PreparedStatement selectUserStatisticsStatement = null;
        ResultSet statisticsResultSet;
        try {
            selectUserStatisticsStatement = connection.prepareStatement(SQL_SELECT_USER_STATISTICS);
            selectUserStatisticsStatement.setInt(1, userId);
            selectUserStatisticsStatement.setInt(2, userId);
            selectUserStatisticsStatement.setInt(3, userId);
            selectUserStatisticsStatement.setInt(4, userId);
            selectUserStatisticsStatement.setInt(5, userId);
            selectUserStatisticsStatement.setInt(6, userId);
            statisticsResultSet = selectUserStatisticsStatement.executeQuery();

            if(statisticsResultSet.next()){
                statistics.setFollowingUsersCount(statisticsResultSet.getInt(FOLLOWING_USERS_COUNT));
                statistics.setFollowersCount(statisticsResultSet.getInt(FOLLOWERS_COUNT));
                statistics.setRate(statisticsResultSet.getDouble(USER_RATE));
                statistics.setQuestionsCount(statisticsResultSet.getInt(USER_QUESTIONS_COUNT));
                statistics.setAnswersCount(statisticsResultSet.getInt(USER_ANSWERS_COUNT));
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectUserStatisticsStatement);
        }
        return statistics;
    }

    /**
     * creates PreparedStatement for selecting limited number of rows with information
     * about users marked as 'following' by definite user
     *
     * @param userId id of user who has list of 'following' users
     * @param startUser number of first selected row
     * @param usersPerPage number of selected rows
     * @return container with list of users(cut) and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Friend> takeFollowingUsers(int userId, int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> friends = new PaginatedList<>();
        List<Friend> items;

        PreparedStatement selectFollowingUsersStatement = null;
        ResultSet followingUsersResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectFollowingUsersStatement = connection.prepareStatement(SQL_SELECT_FOLLOWING_USERS);
            selectFollowingUsersStatement.setInt(1, userId);
            selectFollowingUsersStatement.setInt(2, userId);
            selectFollowingUsersStatement.setInt(3, startUser);
            selectFollowingUsersStatement.setInt(4, usersPerPage);
            followingUsersResultSet = selectFollowingUsersStatement.executeQuery();
            items = createFollowingUsersFromResultSet(followingUsersResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(foundRowsResultSet.next()){
                friends.setItems(items);
                friends.setTotalCount(foundRowsResultSet.getInt(1));
                friends.setItemsPerPage(usersPerPage);
                friends.setItemStart(startUser);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectFollowingUsersStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return friends;
    }

    /**
     * creates PreparedStatement for selecting limited number of rows with information
     * about users who marked definite user as 'following'
     *
     * @param userId id of user who has list of followers
     * @param startUser number of first selected row
     * @param usersPerPage number of selected rows
     * @return container with list of users(cut) and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Friend> takeFollowers(int userId, int startUser, int usersPerPage) throws DAOException {
        PaginatedList<Friend> followers = new PaginatedList<>();
        List<Friend> items;

        PreparedStatement selectFollowersStatement = null;
        ResultSet followersResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectFollowersStatement = connection.prepareStatement(SQL_SELECT_FOLLOWERS);
            selectFollowersStatement.setInt(1, userId);
            selectFollowersStatement.setInt(2, userId);
            selectFollowersStatement.setInt(3, startUser);
            selectFollowersStatement.setInt(4, usersPerPage);
            followersResultSet = selectFollowersStatement.executeQuery();
            items = createFollowersFromResultSet(followersResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(foundRowsResultSet.next()){
                followers.setTotalCount(foundRowsResultSet.getInt(1));
                followers.setItemsPerPage(usersPerPage);
                followers.setItems(items);
                followers.setItemStart(startUser);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectFollowersStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return followers;
    }

    /**
     * creates list of followers from result set
     *
     * @param followersResultSet result set that contatins list of followers
     * @return list of users marked as 'following'
     * @throws SQLException if exception while processing SQL statement will be caught
     */
    private List<Friend> createFollowersFromResultSet(ResultSet followersResultSet) throws SQLException {
        List<Friend> followers;
        Friend follower;
        if (!followersResultSet.next()) {
            followers = null;
        } else {
            followers = new ArrayList<>();
            followersResultSet.beforeFirst();
            while (followersResultSet.next()) {
                follower = new Friend();
                follower.setId(followersResultSet.getInt(ID));
                follower.setLogin(followersResultSet.getString(LOGIN));
                follower.setSurname(followersResultSet.getString(SURNAME));
                follower.setName(followersResultSet.getString(NAME));
                follower.setRole(Role.fromValue(followersResultSet.getString(ROLE)));
                follower.setAvatar(followersResultSet.getString(AVATAR));
                follower.setUserStatus(followersResultSet.getString(USER_STATUS));
                follower.setUserRate(followersResultSet.getDouble(USER_RATE));
                followers.add(follower);
            }
        }
        return followers;
    }

    /**
     * crates user from result set
     *
     * @param userResultSet result set that contains user
     * @return user or null if result set doesn't contain rows
     * @throws SQLException if exception while processing SQL statement will be caught
     */
    private User createUserFromResultSet(ResultSet userResultSet) throws SQLException {
        User user;
        if (!userResultSet.next()) {
            user = null;
        } else {
            user = new User();
            user.setId(userResultSet.getInt(ID));
            user.setLogin(userResultSet.getString(LOGIN));
            user.setSurname(userResultSet.getString(SURNAME));
            user.setName(userResultSet.getString(NAME));
            user.setEmail(userResultSet.getString(EMAIL));
            user.setBirthday(userResultSet.getDate(BIRTHDAY));
            int sex = userResultSet.getInt(SEX);
            if (sex == 1) {
                user.setSex(true);
            } else {
                user.setSex(false);
            }
            user.setRegistrationDate(userResultSet.getDate(REG_DATE));
            user.setRole(Role.fromValue(userResultSet.getString(ROLE)));
            user.setState(User.UserState.valueOf(userResultSet.getString(STATE).toUpperCase()));
            user.setAvatar(userResultSet.getString(AVATAR));
            user.setCountry(userResultSet.getString(COUNTRY));
            user.setCity(userResultSet.getString(CITY));
            user.setStatus(userResultSet.getString(STATUS));
            user.setLanguage(User.Language.valueOf(userResultSet.getString(LANGUAGE).toUpperCase()));
            String friendState = userResultSet.getString(FRIEND_STATE);
            if (friendState != null) {
                user.setFriend(true);
            }
            user.setKeyWord(User.KeyWord.valueOf(userResultSet.getString(KEY_WORD_TYPE).toUpperCase()));
            user.setKeyWordValue(userResultSet.getString(KEY_WORD_VALUE));
        }
        return user;
    }

    /**
     * creates list of users marked as 'following' by definite user
     *
     * @param followingUsersResultSet result set that contains users
     * @return list of users marked as 'following'
     * @throws SQLException if exception while processing SQL statement will be caught
     */
    private List<Friend> createFollowingUsersFromResultSet(ResultSet followingUsersResultSet) throws SQLException {
        List<Friend> friends;
        Friend friend;
        if (!followingUsersResultSet.next()) {
            friends = null;
        } else {
            friends = new ArrayList<>();
            followingUsersResultSet.beforeFirst();
            while (followingUsersResultSet.next()) {
                friend = new Friend();
                friend.setId(followingUsersResultSet.getInt(ID));
                friend.setLogin(followingUsersResultSet.getString(LOGIN));
                friend.setSurname(followingUsersResultSet.getString(SURNAME));
                friend.setName(followingUsersResultSet.getString(NAME));
                friend.setRole(Role.fromValue(followingUsersResultSet.getString(ROLE)));
                friend.setAvatar(followingUsersResultSet.getString(AVATAR));
                friend.setUserStatus(followingUsersResultSet.getString(USER_STATUS));
                String friendState = followingUsersResultSet.getString(FRIEND_STATE);
                if (friendState != null) {
                    friend.setFriend(true);
                }
                friend.setUserRate(followingUsersResultSet.getDouble(USER_RATE));
                friends.add(friend);
            }
        }
        return friends;
    }
}