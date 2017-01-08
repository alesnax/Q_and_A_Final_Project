package by.alesnax.qanda.dao;

import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;

import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 */
public interface PostDAO {

    List<Category> takeAllCategories() throws DAOException;

    List<CategoryInfo> takeCategoriesInfo() throws DAOException;

    void addNewQuestion(int id, String category, String title, String description) throws DAOException;

    List<Post> takeMyPosts(int userId, String lowLimit, String highLimit) throws DAOException;

    List<Post> takeQuestionsByCategory(String categoryId, int userId) throws DAOException;

    List<Post> takeQuestionsByUserId(int profileUserId, int userId) throws DAOException;

    CategoryInfo takeCategoryInfoById(String categoryId) throws DAOException;

    List<Post> takeLikedPosts(int userId) throws DAOException;

    void deletePostById(int postId) throws DAOException;
}
