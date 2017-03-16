package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;

/**
 * UserService contains list of methods that should be implemented to provide linking between command
 * and DAO layers. Methods processes data before calling DAO layer and process result of returned parameters
 * from DAO layer before sending back to command layer.
 *
 * @author Aliaksandr Nakhankou
 */
public interface UserService {
    void registerNewUser(String login, String password, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws ServiceException;

    User userAuthorization(String email, String password) throws ServiceException;

    User findUserById(int userId, int sessionUserId) throws ServiceException;

    PaginatedList<Friend> findFriends(int userId, int startUser, int usersPerPage) throws ServiceException;

    PaginatedList<Friend> findFollowers(int id, int startUser, int usersPerPage) throws ServiceException;

    void removeUserFromFollowing(int removedUserId, int userId) throws ServiceException;

    void addFollower(int followingUserId, int id) throws ServiceException;

    User changeUserInfo(int id, String login, String name, String surname, String email, String bDay, String bMonth, String bYear, String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws ServiceException;

    boolean changePassword(int id, String password1, String password2) throws ServiceException;

    void uploadUserAvatar(int id, String avatarPath) throws ServiceException;

    void changeUserLanguage(int id, String language) throws ServiceException;

    PaginatedList<Friend> findBestUsers(int startUser, int usersPerPage) throws ServiceException;

    String recoverPassword(String email, String keyWordType, String keyWordValue) throws ServiceException;

    boolean deleteAccount(int userId, String password) throws ServiceException;
}
