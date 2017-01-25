package by.alesnax.qanda.dao;

import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;

/**
 * Created by alesnax on 05.12.2016.
 */
public interface AdminDAO {
    PaginatedList<Friend> takeManagingUsers(int startUser, int usersPerPage) throws DAOException;

    PaginatedList<Ban> takeAllCurrentBans(int startBan, int bansPerPage) throws DAOException;

    PaginatedList<Complaint> takeAllComplaints(int startComplaint, int complaintsPerPage)  throws DAOException;

    boolean updateUserStatus(String login, String role) throws DAOException;

    void addNewCategory(int userId, String titleEn, String titleRu, String descriptionEn, String descriptionRu) throws DAOException;

    void updateCategoryStatusToClosed(int catId) throws DAOException;

    boolean updateCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String login, String categoryStatus) throws DAOException;
}
