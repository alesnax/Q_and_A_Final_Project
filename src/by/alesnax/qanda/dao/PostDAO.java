package by.alesnax.qanda.dao;

import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.pagination.PaginatedList;

import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 */
public interface PostDAO {

    PaginatedList<Category> takeAllCategories(int startCategory, int categoriesPerPage) throws DAOException;

    PaginatedList<Category> takeModeratedCategories(int userId, int startCategory, int categoriesPerPage) throws DAOException;

    List<CategoryInfo> takeCategoriesInfo() throws DAOException;

    PaginatedList<Post> takeQuestionsByCategory(String categoryId, int userId, int startPost, int postsPerPage) throws DAOException;

    PaginatedList<Post> takePostsByUserId(int profileUserId, int userId, int startPost, int postsPerPage) throws DAOException;

    PaginatedList<Post> takeLikedPosts(int userId, int startPost, int postsPerPage) throws DAOException;

    PaginatedList<Post> takeFriendsPosts(int userId, int startPost, int postsPerPage) throws DAOException;

    List<Post> takeQuestionWithAnswersById(int questionId, int userId) throws DAOException;

    PaginatedList<Post> takeBestQuestions(int userId, int startPost, int postsPerPage) throws DAOException;

    PaginatedList<Post> takeBestAnswers(int userId, int startPost, int postsPerPage) throws DAOException;

    void addNewQuestion(int id, String category, String title, String description) throws DAOException;

    CategoryInfo takeCategoryInfoById(String categoryId) throws DAOException;

    void deletePostById(int postId) throws DAOException;

    void addNewAnswer(int userId, String questionId, String categoryId, String description) throws DAOException;

    void addNewRate(int postId, int mark, int userId) throws DAOException;

    void addCorrectedAnswer(int answerId, String description) throws DAOException;

    void addCorrectedQuestion(int questionId, int catId, String correctedTitle, String description) throws DAOException;

    Post takePostById(int postId) throws DAOException;

    void addNewComplaint(int userId, int complaintPostId, String description) throws DAOException;

    PaginatedList<Post> searchPostsByKeyWords(int userId, String content, int startPost, int postsPerPage) throws DAOException;
}
