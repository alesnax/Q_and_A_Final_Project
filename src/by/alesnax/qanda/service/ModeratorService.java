package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.service.impl.ServiceException;

/**
 * Created by alesnax on 17.12.2016.
 */
public interface ModeratorService {

    PaginatedList<Friend> findAllUsers(int startUser, int usersPerPage) throws ServiceException;

    PaginatedList<Ban> findBannedUsersById(int userId, int startBan, int bansPerPage) throws ServiceException;

    void stopUserBan(int banId) throws ServiceException;

    PaginatedList<Complaint> findComplaintsByModeratorId(int userId, int startComplaint, int complaintsPerPage) throws ServiceException;

    void addComplaintDecision(int id, int complaintPostId, int complaintAuthorId, String decision, int status) throws ServiceException;

    void correctCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String categoryStatus) throws ServiceException;
}
