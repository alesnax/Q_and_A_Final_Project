package by.alesnax.qanda.dao;

import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.pagination.PaginatedList;

import java.util.List;

/**
 * Interface has declaration of methods that process operations of
 * manipulating with information that stores in databases and related with answer's or question's data.
 * Methods of classes sends SQL statements to the database and
 * get result as ResultSet of objects or number of processed rows in database.
 *
 * @author Aliaksandr Nakhankou
 */
public interface PostDAO {

    PaginatedList<Category> takeAllCategories(int startCategory, int categoriesPerPage) throws DAOException;

    PaginatedList<Category> takeModeratedCategories(int userId, int startCategory, int categoriesPerPage) throws DAOException;

    List<CategoryInfo> takeCategoriesInfo() throws DAOException;

    PaginatedList<Post> takeQuestionsByCategory(String categoryId, int userId, int startPost, int postsPerPage) throws DAOException;

    PaginatedList<Post> takePostsByUserId(int profileUserId, int userId, int startPost, int postsPerPage) throws DAOException;

    PaginatedList<Post> takeRatedPosts(int userId, int startPost, int postsPerPage) throws DAOException;

    PaginatedList<Post> takeFriendsPosts(int userId, int startPost, int postsPerPage) throws DAOException;

    List<Post> takeQuestionWithAnswersById(int questionId, int userId) throws DAOException;

    PaginatedList<Post> takeBestQuestions(int userId, int startPost, int postsPerPage) throws DAOException;

    PaginatedList<Post> takeBestAnswers(int userId, int startPost, int postsPerPage) throws DAOException;

    String addNewQuestion(int id, String category, String title, String description) throws DAOException;

    CategoryInfo takeCategoryInfoById(String categoryId) throws DAOException;

    void deletePostById(int postId) throws DAOException;

    String addNewAnswer(int userId, String questionId, String categoryId, String description) throws DAOException;

    void addNewRate(int postId, int mark, int userId) throws DAOException;

    String addCorrectedAnswer(int userId, int answerId, String description) throws DAOException;

    String addCorrectedQuestion(int userId, int questionId, int catId, String correctedTitle, String description) throws DAOException;

    void addNewComplaint(int userId, int complaintPostId, String description) throws DAOException;

    PaginatedList<Post> searchPostsByKeyWords(int userId, String content, int startPost, int postsPerPage) throws DAOException;
}
