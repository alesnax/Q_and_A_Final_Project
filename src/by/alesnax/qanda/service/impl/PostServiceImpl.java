package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.impl.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.dao.impl.PostDAOImpl;
import by.alesnax.qanda.dao.pool.ConnectionPool;
import by.alesnax.qanda.dao.pool.ConnectionPoolException;
import by.alesnax.qanda.dao.pool.WrappedConnection;
import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.service.PostService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 */
public class PostServiceImpl implements PostService {
    @Override
    public ArrayList<Post> findBestAnswers(String lowLimit, String highLimit) {
        return null;
    }

    @Override
    public ArrayList<Post> findBestQuestions(String lowLimit, String highLimit) {
        return null;
    }

    @Override
    public ArrayList<Post> findBestUsers(String lowLimit, String highLimit) {
        return null;
    }


    @Override
    public List<Post> findQuestionsByUserId(int userId) throws ServiceException {
        WrappedConnection connection = null;
        List<Post> questions = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            questions = postDAO.findQuestionsByUserId(userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return questions;
    }

    @Override
    public List<Post> takeQuestionsByCategoryList(String categoryId) throws ServiceException {
        WrappedConnection connection = null;
        List<Post> questions = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            questions = postDAO.findQuestionsByCategory(categoryId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return questions;
    }

    @Override
    public List<Category> takeCategoriesList() throws ServiceException {
        WrappedConnection connection = null;
        List<Category> categories = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            categories = postDAO.takeAllCategories();
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return categories;
    }

    @Override
    public List<CategoryInfo> takeShortCategoriesList() throws ServiceException {
        WrappedConnection connection = null;
        List<CategoryInfo> categories = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            categories = postDAO.takeCategoriesInfo();
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return categories;
    }

    @Override
    public void addNewQuestion(int id, String category, String title, String description) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            postDAO.addNewQuestion(id, category, title, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAODuplicatedInfoException e) {
            throw new ServiceDuplicatedInfoException(e.getMessage(), e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public List<Post> findMyPosts(int userId, String lowLimit, String highLimit) throws ServiceException {
        WrappedConnection connection = null;
        List<Post> myPosts = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            myPosts = postDAO.findMyPosts(userId, lowLimit, highLimit);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                throw new ServiceException("Error while returning connection to ConnectionPool", e);
            }
        }
        return myPosts;



    }


}
