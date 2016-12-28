package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.UserDAO;
import by.alesnax.qanda.dao.pool.WrappedConnection;
import by.alesnax.qanda.entity.Role;
import by.alesnax.qanda.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

/**
 * Created by alesnax on 04.12.2016.
 */
public class UserDAOImpl extends AbstractDAO<Integer, User> implements UserDAO {
    private static final String SQL_ADD_NEW_USER = "INSERT INTO `likeit_db`.`users` " +
            "(`login`, `password`, `surname`, `name`, `email`, `birthday`, `sex`, `role`, `state`, `country`, `city`, `status`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
    private static final String SQL_USER_AUTHORIZATION = "SELECT * FROM `likeit_db`.`users` WHERE email=? AND password=?";
    private static final String SQL_USER_SELECT_ALL = "SELECT * FROM `likeit_db`.`users` WHERE id=?";

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


    private static final String USER_ROLE = "user";
    private static final String USER_STATE_ACTIVE = "active";
    private static final int MONTH_DIFFERENCE = 1;
    private static final int YEAR_DIFFERENCE = 1900;

    public UserDAOImpl(WrappedConnection connection) {
        super(connection);
    }

    @Override
    public User findEntityById(Integer id) throws DAOException {
        User user = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(SQL_USER_SELECT_ALL);
            st.setInt(1, id);
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
    public void registerNewAccount(String login, String password, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status) throws DAOException {

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
            connection.closeResultSet(rs);
            connection.closeStatement(st);
        }
        return user;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException{
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
        }
        return user;

    }
}