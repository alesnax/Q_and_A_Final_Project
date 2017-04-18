package by.alesnax.qanda.service.impl;

import by.alesnax.qanda.dao.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.DAOException;
import by.alesnax.qanda.dao.impl.DAOFactory;
import by.alesnax.qanda.dao.impl.PostDAOImpl;
import by.alesnax.qanda.entity.Category;
import by.alesnax.qanda.entity.CategoryInfo;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.ConnectionPool;
import by.alesnax.qanda.pool.ConnectionPoolException;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceDuplicatedInfoException;
import by.alesnax.qanda.service.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class contains implemented list of methods to provide linking between command
 * and DAO layers. Methods processes data before calling DAO layer and process result of returned parameters
 * from DAO layer before sending back to command layer.
 * Methods processes data related with Post or Category entities.
 *
 * @author Aliaksandr Nakhankou
 */
class PostServiceImpl implements PostService {
    private static Logger logger = LogManager.getLogger(PostServiceImpl.class);

    /**
     * method takes best answers list from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param userId       id of session user
     * @param startPost    number of first item to be taken from database
     * @param postsPerPage number of items to be taken from database
     * @return container with list of best answers and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Post> findBestAnswers(int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> answers = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            answers = postDAO.takeBestAnswers(userId, startPost, postsPerPage);
            if (startPost > answers.getTotalCount()) {
                startPost = 0;
                answers = postDAO.takeBestAnswers(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return answers;
    }

    /**
     * method takes best questions list from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param userId       id of session user
     * @param startPost    number of first item to be taken from database
     * @param postsPerPage number of items to be taken from database
     * @return container with list of best questions and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Post> findBestQuestions(int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> questions = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            questions = postDAO.takeBestQuestions(userId, startPost, postsPerPage);
            if (startPost > questions.getTotalCount()) {
                startPost = 0;
                questions = postDAO.takeBestAnswers(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return questions;
    }

    /**
     * method takes list of posts of user from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param profileUserId id of user which posts should be taken
     * @param userId        id of session user
     * @param startPost     number of first item to be taken from database
     * @param postsPerPage  number of items to be taken from database
     * @return container with list user posts and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Post> findPostsByUserId(int profileUserId, int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> posts = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            posts = postDAO.takePostsByUserId(profileUserId, userId, startPost, postsPerPage);
            if (startPost > posts.getTotalCount()) {
                startPost = 0;
                posts = postDAO.takeBestAnswers(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return posts;
    }

    /**
     * method takes posts where session user left rate from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param userId       id of session user
     * @param startPost    number of first item to be taken from database
     * @param postsPerPage number of items to be taken from database
     * @return container with list of rated posts and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Post> findLikedPosts(int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> posts = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            posts = postDAO.takeRatedPosts(userId, startPost, postsPerPage);
            if (startPost > posts.getTotalCount()) {
                startPost = 0;
                posts = postDAO.takeRatedPosts(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return posts;
    }

    /**
     * calls method from DAO to update post stattus to 'deleted'
     *
     * @param postId id of post
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void deletePost(int postId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            postDAO.deletePostById(postId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    /**
     * method takes posts where of followers of session user from DAO layer method and checks if first item number less than
     * last one from query, if true repeat query with corrected number of first item
     *
     * @param userId       id of session user
     * @param startPost    number of first item to be taken from database
     * @param postsPerPage number of items to be taken from database
     * @return container with list of followers' posts and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Post> findFriendsPosts(int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> posts = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            posts = postDAO.takeFriendsPosts(userId, startPost, postsPerPage);
            if (startPost > posts.getTotalCount()) {
                startPost = 0;
                posts = postDAO.takeFriendsPosts(userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return posts;
    }

    /**
     * takes question and its answers from DAO method by question id
     *
     * @param questionId id of question
     * @param userId     id of session user for showing rates if exist
     * @return container with list of question and answers and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public List<Post> findQuestionWithAnswersById(int questionId, int userId) throws ServiceException {
        WrappedConnection connection = null;
        List<Post> question = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            question = postDAO.takeQuestionWithAnswersById(questionId, userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return question;
    }

    /**
     * send answer parameters for adding new answers into database
     *
     * @return status of operation
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public String addNewAnswer(int userId, String questionId, String categoryId, String description) throws ServiceException {
        WrappedConnection connection = null;
        String status = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            status = postDAO.addNewAnswer(userId, questionId, categoryId, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return status;
    }

    /**
     * send to DAO value of rate for post
     *
     * @param postId id of post
     * @param mark   value of rate
     * @param userId user who rated post
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void ratePost(int postId, int mark, int userId) throws ServiceException {
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            postDAO.addNewRate(postId, mark, userId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    /**
     * send to DAO corrected content of answer for updating
     *
     * @param userId
     * @param answerId    id of answer
     * @param description content of answer
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public String addCorrectedAnswer(int userId, int answerId, String description) throws ServiceException {
        String status = null;
        WrappedConnection connection = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            status = postDAO.addCorrectedAnswer(userId, answerId, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return status;
    }

    /**
     * send to DAO method parameters of corrected question for updating
     *
     * @return status of operation
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public String addCorrectedQuestion(int userId, int questionId, int catId, String correctedTitle, String description) throws ServiceException {
        WrappedConnection connection = null;
        String status = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            status = postDAO.addCorrectedQuestion(userId, questionId, catId, correctedTitle, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }

        return status;
    }

    /**
     * takes post from DAO by id, called by moderators or admins, post can have deleted status
     *
     * @param postId id of post
     * @return post
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public Post findPostById(int postId) throws ServiceException {
        WrappedConnection connection = null;
        Post post = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            post = postDAO.findEntityById(postId);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return post;
    }

    /**
     * method send data of new complaint to DAO method
     *
     * @param userId          id of user
     * @param complaintPostId id of post to which user have complaint
     * @param description     content of complaint
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public void addNewComplaint(int userId, int complaintPostId, String description) throws ServiceException {
        WrappedConnection connection = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            postDAO.addNewComplaint(userId, complaintPostId, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAODuplicatedInfoException e) {
            throw new ServiceDuplicatedInfoException(e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
    }

    /**
     * method send parameters to DAO for searching posts matching with query
     *
     * @param userId       session user id for marks
     * @param content      user search query
     * @param startPost    number of first item to be taken from database
     * @param postsPerPage number of items to be taken from database
     * @return container with list of finding matching with query posts and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Post> searchPosts(int userId, String content, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> posts = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            posts = postDAO.searchPostsByKeyWords(userId, content, startPost, postsPerPage);
            if (startPost > posts.getTotalCount()) {
                startPost = 0;
                posts = postDAO.searchPostsByKeyWords(userId, content, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return posts;
    }

    /**
     * takes posts by category id from dao layer
     *
     * @param categoryId   id of category
     * @param userId       id of user for marks
     * @param startPost    number of first item to be taken from database
     * @param postsPerPage number of items to be taken from database
     * @return container with list of posts of definite category and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Post> findQuestionsByCategoryList(String categoryId, int userId, int startPost, int postsPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Post> questions = null;
        List<Post> items;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
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
            if (startPost > questions.getTotalCount() & questions.getTotalCount() > 0) {
                startPost = 0;
                questions = postDAO.takeQuestionsByCategory(categoryId, userId, startPost, postsPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return questions;
    }

    /**
     * takes list of categories from DAO layer
     *
     * @param startCategory     number of first item to be taken from database
     * @param categoriesPerPage number of items to be taken from database
     * @return container with list of categories and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Category> takeCategoriesList(int startCategory, int categoriesPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Category> categories = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            categories = postDAO.takeAllCategories(startCategory, categoriesPerPage);
            if (startCategory > categories.getTotalCount()) {
                startCategory = 0;
                categories = postDAO.takeAllCategories(startCategory, categoriesPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return categories;
    }

    /**
     * takes list of moderated categories by definite user from DAO layer
     *
     * @param userId            moderator id
     * @param startCategory     number of first item to be taken from database
     * @param categoriesPerPage number of items to be taken from database
     * @return container with list of moderated categories and pagination parameters
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public PaginatedList<Category> takeModeratedCategoriesList(int userId, int startCategory, int categoriesPerPage) throws ServiceException {
        WrappedConnection connection = null;
        PaginatedList<Category> moderatedCategories = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            moderatedCategories = postDAO.takeModeratedCategories(userId, startCategory, categoriesPerPage);
            if (startCategory > moderatedCategories.getTotalCount()) {
                startCategory = 0;
                moderatedCategories = postDAO.takeModeratedCategories(userId, startCategory, categoriesPerPage);
            }
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return moderatedCategories;
    }

    /**
     * takes info about categories from DAO layer to be showed in add question form
     *
     * @return list of cut info about categories
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public List<CategoryInfo> takeShortCategoriesList() throws ServiceException {
        WrappedConnection connection = null;
        List<CategoryInfo> categories = null;

        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            categories = postDAO.takeCategoriesInfo();
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return categories;
    }

    /**
     * sends parameters of question to DAO method to insert it into database
     *
     * @return status of operation
     * @throws ServiceException if exception while processing SQL query and connection will be caught
     */
    @Override
    public String addNewQuestion(int id, String category, String title, String description) throws ServiceException {
        WrappedConnection connection = null;
        String status = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            PostDAOImpl postDAO = DAOFactory.getInstance().getPostDAO(connection);
            status = postDAO.addNewQuestion(id, category, title, description);
        } catch (ConnectionPoolException e) {
            throw new ServiceException("Error while taking connection from ConnectionPool", e);
        } catch (DAOException e) {
            throw new ServiceException(e);
        } finally {
            try {
                ConnectionPool.getInstance().returnConnection(connection);
            } catch (ConnectionPoolException e) {
                logger.log(Level.ERROR, "Error while returning connection to ConnectionPool", e);
            }
        }
        return status;
    }
}
