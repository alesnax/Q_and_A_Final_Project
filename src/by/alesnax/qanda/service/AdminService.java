package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.service.impl.ServiceException;

/**
 * Created by alesnax on 05.12.2016.
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
