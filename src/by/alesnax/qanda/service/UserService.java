package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.service.impl.ServiceException;

/**
 * Created by alesnax on 05.12.2016.
 */
public interface UserService {
    void registerNewUser(String login, String password, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status) throws ServiceException;

    User userAuthorization(String email, String password) throws ServiceException;

    User findUserById(int userId) throws ServiceException;
}
