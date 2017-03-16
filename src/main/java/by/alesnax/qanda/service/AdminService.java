package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;

/**
 * AdminService contains list of methods that should be implemented to provide linking between command
 * and DAO layers. Methods processes data before calling DAO layer and process result of returned parameters
 * from DAO layer before sending back to command layer.
 * Methods processes operations related with user with ADMIN role.
 *
 * @author Aliaksandr Nakhankou
 */
public interface AdminService {
    PaginatedList<Friend> findManagingUsers(int startUser, int usersPerPage) throws ServiceException;

    PaginatedList<Ban> findAllBans(int startBan, int bansPerPage) throws ServiceException;

    PaginatedList<Complaint> findComplaints(int startComplaint, int complaintsPerPage) throws ServiceException;

    boolean changeUserRole(String login, String role) throws ServiceException;

    void createNewCategory(int id, String titleEn, String titleRu, String descriptionEn, String descriptionRu) throws ServiceException;

    void closeCategory(int catId) throws ServiceException;

    boolean correctCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String login, String categoryStatus) throws ServiceException;
}
