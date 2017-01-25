package by.alesnax.qanda.dao;

import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;

/**
 * Created by alesnax on 13.01.2017.
 */
public interface ModeratorDAO {
    PaginatedList<Friend> takeAllUsers(int startUser, int usersPerPage) throws DAOException;

    PaginatedList<Ban> takeCurrentBansByModeratorId(int userId, int startBan, int bansPerPage) throws DAOException;

    PaginatedList<Complaint> takeComplaintsByModeratorId(int userId, int startComplaint, int complaintsPerPage) throws DAOException;

    void updateBansStatusFinished(int banId) throws DAOException;

    void updateComplaintStatusToApproved(int moderatorId, int complaintPostId, int complaintAuthorId, String decision) throws DAOException;

    void updateComplaintStatusToCancelled(int moderatorId, int complaintPostId, int complaintAuthorId, String decision) throws DAOException;

    void updateCategoryInfo(String categoryId, String titleEn, String titleRu, String descriptionEn, String descriptionRu, String categoryStatus) throws DAOException;
}
