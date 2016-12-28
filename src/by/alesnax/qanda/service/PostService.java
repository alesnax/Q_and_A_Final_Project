package by.alesnax.qanda.service;

import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.service.impl.ServiceException;

import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 */
public interface PostService {
    List<Post> findBestAnswers(String lowLimit, String highLimit);

    List<Post> findBestQuestions(String lowLimit, String highLimit);

    List<Post> findBestUsers(String lowLimit, String highLimit);

    List<Category> takeCategoriesList() throws ServiceException;

    List<CategoryInfo> takeShortCategoriesList() throws ServiceException;

    void addNewQuestion(int id, String category, String title, String description) throws ServiceException;

    List<Post> findMyPosts(int userId, String lowLimit, String highLimit) throws ServiceException;

    List<Post> takeQuestionsByCategoryList(String categoryId) throws ServiceException;

    List<Post> findQuestionsByUserId(int userId)  throws ServiceException;
}
