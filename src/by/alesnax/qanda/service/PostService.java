package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.service.impl.ServiceException;

import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 */
public interface PostService {
    PaginatedList<Post> findBestAnswers(int userId, int startPost, int postsPerPage) throws ServiceException;

    PaginatedList<Post> findBestQuestions(int userId, int startPost, int postsPerPage) throws ServiceException;

    PaginatedList<Category> takeCategoriesList(int startCategory, int categoriesPerPage) throws ServiceException;

    PaginatedList<Category> takeModeratedCategoriesList(int userId, int startCategory, int categoriesPerPage) throws ServiceException;

    List<CategoryInfo> takeShortCategoriesList() throws ServiceException;

    void addNewQuestion(int id, String category, String title, String description) throws ServiceException;

    PaginatedList<Post> findQuestionsByCategoryList(String categoryId, int userId, int startPost, int postsPerPage) throws ServiceException;

    PaginatedList<Post> findPostsByUserId(int profileUserId, int userId, int startPost, int postsPerPage)  throws ServiceException;

    PaginatedList<Post> findLikedPosts(int userId, int startPost, int postsPerPage) throws ServiceException;

    void deletePost(int postId) throws ServiceException;

    PaginatedList<Post> findFriendsPosts(int id, int startPost, int postsPerPage) throws ServiceException;

    List<Post> findQuestionWithAnswersById(int questionId, int userId) throws ServiceException;

    void addNewAnswer(int id, String questionId, String categoryId, String description) throws ServiceException;

    void ratePost(int postId, int mark, int userId) throws ServiceException;

    void addCorrectedAnswer(int answerId, String description) throws ServiceException;

    void addCorrectedQuestion(int questionId, int catId, String correctedTitle, String description) throws ServiceException;

    Post findPostById(int postId) throws ServiceException;

    void addNewComplaint(int id, int complaintPostId, String description) throws ServiceException;

    PaginatedList<Post> searchPosts(int userId, String content, int startPost, int postsPerPage) throws ServiceException;
}
