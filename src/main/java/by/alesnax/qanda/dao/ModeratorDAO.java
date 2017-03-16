package by.alesnax.qanda.dao;

import by.alesnax.qanda.entity.Ban;
import by.alesnax.qanda.entity.Complaint;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.pagination.PaginatedList;

/**
 * Interface has declaration of methods that process operations of
 * manipulating with information that stores in databases and related with session user role 'MODERATOR'.
 * Methods of classes sends SQL statements to the database and
 * get result as ResultSet of objects or number of processed rows in database.
 *
 * @author Aliaksandr Nakhankou
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
