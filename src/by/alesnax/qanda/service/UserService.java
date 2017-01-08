package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.service.impl.ServiceException;

import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 */
public interface UserService {
    void registerNewUser(String login, String password, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status) throws ServiceException;

    User userAuthorization(String email, String password) throws ServiceException;

    User findUserById(int userId, int sessionUserId) throws ServiceException;

    List<Friend> findFriends(int userId) throws ServiceException;

    void removeUserFromFollowing(int removedUserId, int userId) throws ServiceException;

    void addFollower(int followingUserId, int id) throws ServiceException;

    User changeUserInfo(int id, String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status) throws ServiceException;

    boolean changePassword(int id, String password1, String password2) throws ServiceException;

    void uploadUserAvatar(int id, String avatarPath) throws ServiceException;

    void changeUserLanguage(int id, String language) throws ServiceException;
}
