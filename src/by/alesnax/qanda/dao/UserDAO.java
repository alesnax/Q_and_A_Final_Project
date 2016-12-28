package by.alesnax.qanda.dao;

import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.entity.User;

/**
 * Created by alesnax on 04.12.2016.
 */
public interface UserDAO {
    void registerNewAccount(String login, String password, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status) throws DAOException;

    User userAuthorization(String email, String password) throws DAOException;
}
