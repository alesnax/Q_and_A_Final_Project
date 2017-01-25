package by.alesnax.qanda.dao;

import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.entity.UserStatistics;
import by.alesnax.qanda.pagination.PaginatedList;

/**
 * Created by alesnax on 04.12.2016.
 */
public interface UserDAO {
    PaginatedList<Friend> takeFriends(int userId, int startUser, int usersPerPage) throws DAOException;

    PaginatedList<Friend> takeFollowers(int userId, int startUser, int usersPerPage) throws DAOException;

    PaginatedList<Friend> takeBestUsers(int startUser, int usersPerPage) throws DAOException;

    void registerNewAccount(String login, String password, String name, String surname,
                            String email, String bDay, String bMonth, String bYear,
                            String sex, String country, String city, String status, String keyWordType, String keyWordValue) throws DAOException;

    User userAuthorization(String email, String password) throws DAOException;

    UserStatistics findUserStatistics(int id) throws DAOException;

    User takeUserById(int userId, int sessionUserId) throws DAOException;

    void removeUserFromFriends(int removedUserId, int userId) throws DAOException;

    void addFollower(int followingUserId, int userId) throws DAOException;

    User updateUserInfo(int userId, String login, String name, String surname, String email, String bDay,
                        String bMonth, String bYear, String sex, String country,
                        String city, String status, String keyWordType, String keyWordValue) throws DAOException;

    boolean updatePassword(int userId, String password1, String password2) throws DAOException;

    void updateAvatar(int userId, String avatarPath) throws DAOException;

    void updateUserLanguage(int userId, String language) throws DAOException;

    boolean updateUserPassWordByKeyword(String changedPassword, String email, String keyWordType, String keyWordValue) throws DAOException;
}
