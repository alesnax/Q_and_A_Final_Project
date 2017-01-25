package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.impl.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.impl.DAOException;
import by.alesnax.qanda.dao.impl.PostDAOImpl;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.service.PostService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 *
 */
public class PostServiceImpl implements PostService {
    private static Logger logger = LogManager.getLogger(PostServiceImpl.class);

    @Override
    public PaginatedList<Post> findBestAnswers(int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> answers = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            answers = postDAO.takeBestAnswers(userId, startPost, postsPerPage);
            if(startPost > answers.getTotalCount()){
                startPost = 0;
                answers = postDAO.takeBestAnswers(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return answers;
    }

    @Override
    public PaginatedList<Post> findBestQuestions(int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> questions = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            questions = postDAO.takeBestQuestions(userId, startPost, postsPerPage);
            if(startPost > questions.getTotalCount()){
                startPost = 0;
                questions = postDAO.takeBestAnswers(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return questions;
    }

    @Override
    public PaginatedList<Post> findPostsByUserId(int profileUserId, int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> posts = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            posts = postDAO.takePostsByUserId(profileUserId, userId, startPost, postsPerPage);
            if(startPost > posts.getTotalCount()){
                startPost = 0;
                posts = postDAO.takeBestAnswers(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return posts;
    }

    @Override
    public PaginatedList<Post> findLikedPosts(int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> posts = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            posts = postDAO.takeLikedPosts(userId, startPost, postsPerPage);
            if(startPost > posts.getTotalCount()){
                startPost = 0;
                posts = postDAO.takeLikedPosts(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return posts;
    }

    @Override
    public void deletePost(int postId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            postDAO.deletePostById(postId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public PaginatedList<Post> findFriendsPosts(int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> posts = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            posts = postDAO.takeFriendsPosts(userId, startPost, postsPerPage);
            if(startPost > posts.getTotalCount()){
                startPost = 0;
                posts = postDAO.takeFriendsPosts(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return posts;
    }

    @Override
    public List<Post> findQuestionWithAnswersById(int questionId, int userId) throws ServiceException {
        WrappedConnection connection = null;
        List<Post> question = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            question = postDAO.takeQuestionWithAnswersById(questionId, userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return question;
    }

    @Override
    public void addNewAnswer(int userId, String questionId, String categoryId, String description) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            postDAO.addNewAnswer(userId, questionId, categoryId, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public void ratePost(int postId, int mark, int userId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            postDAO.addNewRate(postId, mark, userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public void addCorrectedAnswer(int answerId, String description) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            postDAO.addCorrectedAnswer(answerId, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public void addCorrectedQuestion(int questionId, int catId, String correctedTitle, String description) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            postDAO.addCorrectedQuestion(questionId, catId, correctedTitle, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }

    }

    @Override
    public Post findPostById(int postId) throws ServiceException {
        WrappedConnection connection = null;
        Post post = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            post = postDAO.takePostById(postId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return post;
    }

    @Override
    public void addNewComplaint(int userId, int complaintPostId, String description) throws ServiceException {
        WrappedConnection connection = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            postDAO.addNewComplaint(userId, complaintPostId, description);
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    @Override
    public PaginatedList<Post> searchPosts(int userId, String content, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> posts = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            posts = postDAO.searchPostsByKeyWords(userId, content, startPost, postsPerPage);
            if(startPost > posts.getTotalCount()){
                startPost = 0;
                posts = postDAO.searchPostsByKeyWords(userId, content, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return posts;
    }

    @Override
    public PaginatedList<Post> findQuestionsByCategoryList(String categoryId, int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> questions = null;
        List<Post> items = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            questions = postDAO.takeQuestionsByCategory(categoryId, userId, startPost, postsPerPage);

            if (questions.getItems() == null || questions.getItems().isEmpty()) {
                CategoryInfo info = postDAO.takeCategoryInfoById(categoryId);
                if (info != null) {
                    Post stubPost = new Post();
                    stubPost.setId(0);
                    stubPost.setCategoryInfo(info);
                    items = new ArrayList<>();
                    items.add(stubPost);
                    questions.setItems(items);
                }
            }
            if(startPost > questions.getTotalCount() & questions.getTotalCount() > 0){
                startPost = 0;
                questions = postDAO.takeQuestionsByCategory(categoryId, userId, startPost, postsPerPage);            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return questions;
    }

    @Override
    public PaginatedList<Category> takeCategoriesList(int startCategory, int categoriesPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Category> categories = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            categories = postDAO.takeAllCategories(startCategory, categoriesPerPage);
            if(startCategory > categories.getTotalCount()){
                startCategory = 0;
                categories = postDAO.takeAllCategories(startCategory, categoriesPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return categories;
    }

    @Override
    public PaginatedList<Category> takeModeratedCategoriesList(int userId, int startCategory, int categoriesPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Category> moderatedCategories = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = new PostDAOImpl(connection);
            moderatedCategories = postDAO.takeModeratedCategories(userId, startCategory, categoriesPerPage);
            if(startCategory > moderatedCategories.getTotalCount()){
                startCategory = 0;
                moderatedCategories = postDAO.takeModeratedCategories(userId, startCategory, categoriesPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return moderatedCategories;
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
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
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
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }
}
