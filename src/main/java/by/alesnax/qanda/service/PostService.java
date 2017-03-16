package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.pagination.PaginatedList;

import java.util.List;

/**
 * PostService contains list of methods that should be implemented to provide linking between command
 * and DAO layers. Methods processes data before calling DAO layer and process result of returned parameters
 * from DAO layer before sending back to command layer.
 * Methods processes data related with Post or Category entities.
 *
 * @author Aliaksandr Nakhankou
 */
public interface PostService {
    PaginatedList<Post> findBestAnswers(int userId, int startPost, int postsPerPage) throws ServiceException;

    PaginatedList<Post> findBestQuestions(int userId, int startPost, int postsPerPage) throws ServiceException;

    PaginatedList<Category> takeCategoriesList(int startCategory, int categoriesPerPage) throws ServiceException;

    PaginatedList<Category> takeModeratedCategoriesList(int userId, int startCategory, int categoriesPerPage) throws ServiceException;

    List<CategoryInfo> takeShortCategoriesList() throws ServiceException;

    String addNewQuestion(int id, String category, String title, String description) throws ServiceException;

    PaginatedList<Post> findQuestionsByCategoryList(String categoryId, int userId, int startPost, int postsPerPage) throws ServiceException;

    PaginatedList<Post> findPostsByUserId(int profileUserId, int userId, int startPost, int postsPerPage)  throws ServiceException;

    PaginatedList<Post> findLikedPosts(int userId, int startPost, int postsPerPage) throws ServiceException;

    void deletePost(int postId) throws ServiceException;

    PaginatedList<Post> findFriendsPosts(int id, int startPost, int postsPerPage) throws ServiceException;

    List<Post> findQuestionWithAnswersById(int questionId, int userId) throws ServiceException;

    String addNewAnswer(int id, String questionId, String categoryId, String description) throws ServiceException;

    void ratePost(int postId, int mark, int userId) throws ServiceException;

    String addCorrectedAnswer(int userId, int answerId, String description) throws ServiceException;

    String addCorrectedQuestion(int userId, int questionId, int catId, String correctedTitle, String description) throws ServiceException;

    Post findPostById(int postId) throws ServiceException;

    void addNewComplaint(int id, int complaintPostId, String description) throws ServiceException;

    PaginatedList<Post> searchPosts(int userId, String content, int startPost, int postsPerPage) throws ServiceException;
}
