package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.DAODuplicatedInfoException;
import by.alesnax.qanda.dao.DAOException;
import by.alesnax.qanda.dao.PostDAO;
import by.alesnax.qanda.entity.*;
import by.alesnax.qanda.pagination.PaginatedList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

// static import

/**
 * Implements PostDAO interface and extends AbstractDAO class.
 * Implements all methods of PostDAO. Processes operations of
 * manipulating with information that stores in databases and related with answer's and question's and category's information.
 * Methods of classes sends SQL statements to the database and
 * get result as ResultSet of objects or number of processed rows in database.
 *
 * @author Aliaksandr Nakhankou
 * @see by.alesnax.qanda.dao.AbstractDAO
 * @see by.alesnax.qanda.dao.PostDAO
 */
@SuppressWarnings("Duplicates")
public class PostDAOImpl extends AbstractDAO<Integer, Post> implements PostDAO {
    private static Logger logger = LogManager.getLogger(PostDAOImpl.class);

    /**
     * SQL query that selects information about all categories,
     * finds limited list of users.
     */
    private static final String SQL_SELECT_ALL_CATEGORIES = "SELECT sql_calc_found_rows categories.id AS category_id, " +
            "categories.users_id as users_id, title_en, title_ru, creation_date, description_ru, description_en, " +
            "categories.status AS category_status, image, login, avatar, role, count(posts.id) AS quantity " +
            "FROM categories LEFT  JOIN posts ON (posts.category_id = categories.id AND posts.type='question' AND posts.status!='deleted') JOIN users ON users.id = categories.users_id " +
            "group by categories.id ORDER BY quantity DESC, category_id LIMIT ?,?;";

    /**
     * SQL query that selects information about moderated categories,
     * selects limited list of categories.
     */
    private static final String SQL_SELECT_MODERATED_CATEGORIES = "SELECT sql_calc_found_rows categories.id AS category_id," +
            " categories.users_id as users_id, title_en, title_ru, creation_date, description_ru, description_en, " +
            "categories.status AS category_status, image, login, avatar, role, count(posts.id) AS quantity\n" +
            "FROM categories LEFT JOIN posts ON (posts.category_id = categories.id AND posts.type='question') JOIN users ON (users.id = categories.users_id AND users.id = ?)\n" +
            " group by categories.id LIMIT ?,?;";

    /**
     * SQL query that selects cut information about categories.
     */
    private static final String SQL_SELECT_CATEGORIES_INFO = "SELECT categories.id AS category_id, title_en, title_ru, categories.users_id AS moderator_id, status AS category_status FROM categories WHERE status!='closed';";

    /**
     * SQL query that selects full information about category.
     */
    private static final String SQL_SELECT_SINGLE_CATEGORY_INFO = "SELECT categories.id AS category_id, title_en, title_ru, categories.users_id AS moderator_id, status AS category_status FROM categories WHERE categories.id=?";

    /**
     * SQL query that inserts new question's data.
     */
    private static final String SQL_INSERT_NEW_QUESTION = "INSERT INTO posts " +
            "(`users_id`, `category_id`, `type`, `title`, `content`) VALUES (?,?,?,?,?);";

    /**
     * SQL query that inserts new answer's data.
     */
    private static final String SQL_ADD_NEW_ANSWER = "INSERT INTO posts (users_id, category_id, type, content, parent_id) VALUES (?,?,?,?,?);";

    /**
     * SQL query that selects id of category if category not closed or exists
     */
    private static final String SQL_SELECT_NOT_CLOSED_CATEGORY_ID = "SELECT id FROM categories WHERE id=? AND status!='closed';";

    /**
     * SQL query that selects id of answer's category if category not closed or exists
     */
    private static final String SQL_SELECT_NOT_CLOSED_CATEGORY_ID_OF_ANSWER = "SELECT id FROM categories WHERE id=(SELECT posts.category_id FROM posts WHERE id=?) AND status!='closed';";

    /**
     * SQL query that selects information about posts of definite author,
     * selects limited list of posts in reversed order by published time.
     */
    private static final String SQL_SELECT_USERS_POSTS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, posts.type, posts.title, posts.content, posts.status,\n" +
            "posts.published_time, posts.modified_time, posts.parent_id, parent.title AS parent_title,  AVG(coalesce(rates.value, 0)) AS mark, categories.title_en, categories.title_ru, " +
            "categories.status AS category_status, categories.users_id AS moderator_id, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id JOIN categories ON categories.id = posts.category_id LEFT JOIN posts AS parent ON posts.parent_id = parent.id " +
            "LEFT JOIN rates ON posts.id = rates.posts_id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type != 'service' AND posts.status != 'deleted'  AND users.id = ?\n" +
            "GROUP BY posts.id ORDER BY published_time DESC LIMIT ?,?";

    /**
     * SQL query that selects information about posts that were rated by definite user,
     * selects limited list of posts in reversed order by adding rate time.
     */
    private static final String SQL_SELECT_RATED_POSTS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru, categories.status AS category_status, posts.type, posts.title, \n" +
            "posts.status, posts.published_time, posts.modified_time, posts.content, AVG(coalesce(rates.value, 0)) AS mark,\n" +
            "posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role, r.value, categories.users_id AS moderator_id\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type != 'service' AND posts.status != 'deleted' GROUP BY posts.id ORDER BY rates.adding_time DESC LIMIT ?,?";

    /**
     * SQL query that selects information about posts that were added by users
     * that marked as following by user with definite id,
     * selects limited list of posts in reversed order by published time.
     */
    private static final String SQL_SELECT_ALL_FRIENDS_POSTS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru, categories.status AS category_status, posts.type,  \n" +
            "posts.title, posts.status, posts.published_time, posts.modified_time, posts.content, AVG(coalesce(rates.value, 0)) AS mark, categories.users_id AS moderator_id,\n" +
            " posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type != 'service' AND posts.status != 'deleted' AND posts.users_id IN (SELECT users_friend_id FROM friends WHERE friends.users_id=? AND friends.state = 'follower')\n" +
            "GROUP BY posts.id ORDER BY posts.published_time DESC LIMIT ?,?";

    /**
     * SQL query that selects information about questions with the highest average marks,
     * selects limited list of posts sorted in reversed order by mark from the highest to the lowest.
     */
    private static final String SQL_SELECT_BEST_QUESTIONS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, posts.type, posts.title,  posts.content, posts.status,\n" +
            "cast(posts.published_time AS datetime) AS published_time, cast(posts.modified_time AS datetime) AS modified_time,\n" +
            " AVG(coalesce(rates.value, 0)) AS mark, categories.title_en, categories.title_ru, categories.status AS category_status, categories.users_id AS moderator_id, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id JOIN categories ON categories.id = posts.category_id \n" +
            "JOIN rates ON posts.id = rates.posts_id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type = 'question' AND posts.status != 'deleted' GROUP BY posts.id ORDER BY mark DESC LIMIT ?, ?";

    /**
     * SQL query that selects information about answers with their question's title with the highest average marks,
     * selects limited list of posts sorted in reversed order by mark from the highest to the lowest.
     */
    private static final String SQL_SELECT_BEST_ANSWERS_A_Q = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, categories.title_en, categories.status AS category_status, categories.title_ru,  categories.users_id AS moderator_id, posts.type,  \n" +
            "posts.title, posts.status, posts.published_time, posts.modified_time, posts.content, AVG(coalesce(rates.value, 0)) AS mark,\n" +
            "posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type = 'answer' AND posts.status != 'deleted'\n" +
            "GROUP BY posts.id ORDER BY mark DESC LIMIT ?, ?;";

    /**
     * SQL query that selects information about questions of definite category,
     * selects limited list of questions sorted in reversed order by published time.
     */
    private static final String SQL_SELECT_QUESTIONS_BY_CATEGORY = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, posts.type,   posts.title, posts.content, posts.status, cast(posts.published_time AS datetime) AS published_time, \n" +
            "cast(posts.modified_time AS datetime) AS modified_time, AVG(coalesce(rates.value, 0)) AS mark, categories.title_en, categories.title_ru, categories.status AS category_status, categories.users_id AS moderator_id, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id JOIN categories ON categories.id = posts.category_id LEFT JOIN rates ON posts.id = rates.posts_id LEFT JOIN rates AS r " +
            "ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type != 'answer' AND posts.status != 'deleted' AND posts.category_id=? GROUP BY posts.id " +
            "ORDER BY published_time DESC  LIMIT ?,?";

    /**
     * SQL query that updates status of question to 'deleted'
     */
    private static final String SQL_UPDATE_QUESTION_STATUS_TO_DELETE = "UPDATE posts SET status='deleted', modified_time=CURRENT_TIMESTAMP WHERE id=?;";

    /**
     * SQL query that updates status of answer to 'deleted'
     */
    private static final String SQL_UPDATE_ANSWER_STATUS_TO_DELETE = "UPDATE posts SET status='deleted', modified_time=CURRENT_TIMESTAMP WHERE parent_id=?;";

    /**
     * SQL query that updates content of answer, status as 'modified' and modified time to current time.
     */
    private static final String SQL_UPDATE_ANSWER_DESCRIPTION = "UPDATE posts SET status='modified', content=?, modified_time=CURRENT_TIMESTAMP WHERE id=?;\n";

    /**
     * SQL query that updates data of question, status as 'modified' and modified time to current time.
     */
    private static final String SQL_UPDATE_QUESTION = "UPDATE posts SET category_id=?, title=?, content=?, status='modified', modified_time=CURRENT_TIMESTAMP WHERE id=?;\n";

    /**
     * SQL query that deletes rates by question id when question has been deleting in the same transaction
     */
    private static final String SQL_DELETE_RATE_BY_QUESTION_ID = "DELETE FROM rates WHERE posts_id=?;";

    /**
     * SQL query that deletes rate by question's id and user's id
     */
    private static final String SQL_DELETE_RATE = "DELETE FROM rates WHERE users_id=? and posts_id=?;";

    /**
     * SQL query that inserts new rate
     */
    private static final String SQL_INSERT_NEW_RATE = "INSERT INTO rates (users_id, posts_id, value) VALUES (?, ?, ?);";

    /**
     * SQL query that selects information about question with definite id and list of answers to it.
     */
    private static final String SQL_SELECT_QUESTION_AND_ANSWERS = "SELECT posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru, categories.status AS category_status, posts.type, \n" +
            " categories.users_id AS moderator_id, posts.title, posts.status, posts.published_time, posts.modified_time, posts.content, \n" +
            "AVG(coalesce(rates.value, 0)) AS mark, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id = r.posts_id )\n" +
            "WHERE posts.type != 'service' AND posts.status != 'deleted' AND posts.id IN (SELECT id FROM posts WHERE id=? UNION DISTINCT\n" +
            "SELECT id FROM posts WHERE parent_id=?) GROUP BY posts.id ORDER BY posts.type ASC";

    /**
     * SQL query that selects information about post with definite id, status of post doesn't matter.
     */
    private static final String SQL_SELECT_POST = "SELECT posts.id, posts.users_id, posts.category_id, " +
            "categories.title_en, categories.title_ru, categories.status AS category_status, categories.users_id AS moderator_id, posts.type,\n" +
            "posts.title, posts.status, posts.published_time, posts.modified_time, posts.content,\n" +
            "posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN posts AS parent ON posts.parent_id = parent.id\n" +
            "WHERE posts.type != 'service' AND posts.id=? GROUP BY posts.id";

    /**
     * SQL query that inserts new complaint to post
     */
    private static final String SQL_INSERT_NEW_COMPLAINT = "INSERT INTO complaints (`posts_id`, `users_id`, `description`) VALUES (?, ?, ?);";

    /**
     * SQL query that selects information about post where posts' titles or description match with key words.
     */
    private static final String SQL_SELECT_POSTS_BY_KEY_WORDS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru,  categories.status AS category_status, posts.type, posts.title,\n" +
            "posts.status, posts.published_time, posts.modified_time, posts.content, AVG(coalesce(rates.value, 0)) AS mark,\n" +
            "posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role, r.value, categories.users_id AS moderator_id\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE  posts.status != 'deleted' AND MATCH (posts.title, posts.content) AGAINST (?)\n" +
            "GROUP BY posts.id ORDER BY posts.published_time DESC LIMIT ?,?";

    /**
     * SQL query that selects number of found rows while previous statement.
     * Statement should be located in one transaction with previous one.
     */
    private static final String SQL_SELECT_FOUND_ROWS = "SELECT FOUND_ROWS()";

    /**
     * SQL query that updates category of answers while updating category of question in the same transaction.
     */
    private static final String SQL_UPDATE_ANSWERS_CATEGORY = "UPDATE posts SET category_id=? WHERE parent_id=?;";

    /**
     * SQL query that selects user's ban id if exists
     */
    private static final String SQL_SELECT_USER_BAN_STATUS = "SELECT id FROM bans WHERE users_id=? AND current_timestamp() BETWEEN start AND end;";

    /**
     * Names of attributes processing in SQL statements related with posts and categories information
     */
    private static final String CATEGORY_ID = "category_id";
    private static final String CAT_CREATION_DATE = "creation_date";
    private static final String TITLE_EN = "title_en";
    private static final String TITLE_RU = "title_ru";
    private static final String QUESTION_TYPE = "question";
    private static final String ANSWER_TYPE = "answer";
    private static final String DESCRIPTION_EN = "description_en";
    private static final String DESCRIPTION_RU = "description_ru";
    private static final String CAT_IMAGE = "image";
    private static final String CAT_STATUS = "category_status";
    private static final String QUANTITY = "quantity";
    private static final String USER_ID = "users_id";
    private static final String ROLE = "role";
    private static final String AVATAR = "avatar";
    private static final String LOGIN = "login";
    private static final String POST_ID = "id";
    private static final String POST_TYPE = "type";
    private static final String POST_TITLE = "title";
    private static final String POST_CONTENT = "content";
    private static final String POST_STATUS = "status";
    private static final String POST_PUBLISHED_TIME = "published_time";
    private static final String POST_MODIFIED_TIME = "modified_time";
    private static final String PARENT_ID = "parent_id";
    private static final String PARENT_TITLE = "parent_title";
    private static final String MODERATOR_ID = "moderator_id";
    private static final String MARK = "mark";
    private static final String CURRENT_USER_MARK = "value";

    /**
     * Constructs PostDAOImpl class, used in package by DAOFactory
     */
    PostDAOImpl(){}

    /**
     * method creates PreparedStatement for selecting post by id
     *
     * @param postId post's id
     * @return post(answer or question) or null if post with such id doesn't exist
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public Post findEntityById(Integer postId) throws DAOException {
        Post post = null;
        PreparedStatement selectPostStatement = null;
        ResultSet postResultSet;
        try {
            selectPostStatement = connection.prepareStatement(SQL_SELECT_POST);
            selectPostStatement.setInt(1, postId);
            postResultSet = selectPostStatement.executeQuery();
            if (postResultSet.next()) {
                post = new Post();
                post.setId(postResultSet.getInt(POST_ID));
                post.setType(Post.PostType.fromValue(postResultSet.getString(POST_TYPE)));
                post.setTitle(postResultSet.getString(POST_TITLE));
                post.setContent(postResultSet.getString(POST_CONTENT));
                post.setStatus(Post.Status.fromValue(postResultSet.getString(POST_STATUS)));
                post.setPublishedTime(postResultSet.getTimestamp(POST_PUBLISHED_TIME));
                post.setModifiedTime(postResultSet.getTimestamp(POST_MODIFIED_TIME));
                post.setParentId(postResultSet.getInt(PARENT_ID));
                post.setParentTitle(postResultSet.getString(PARENT_TITLE));
                CategoryInfo catInfo = new CategoryInfo();
                catInfo.setId(postResultSet.getInt(CATEGORY_ID));
                catInfo.setTitleEn(postResultSet.getString(TITLE_EN));
                catInfo.setTitleRu(postResultSet.getString(TITLE_RU));
                catInfo.setUserId(postResultSet.getInt(MODERATOR_ID));
                catInfo.setStatus(Category.CategoryStatus.fromValue(postResultSet.getString(CAT_STATUS)));
                post.setCategoryInfo(catInfo);
                ShortUser author = new ShortUser();
                author.setId(postResultSet.getInt(USER_ID));
                author.setRole(Role.fromValue(postResultSet.getString(ROLE)));
                author.setAvatar(postResultSet.getString(AVATAR));
                author.setLogin(postResultSet.getString(LOGIN));
                post.setUser(author);
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectPostStatement);
        }
        return post;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about questions of definite category and statement for selecting total rows count of statement
     *
     * @param categoryId   id of category
     * @param userId       id of user who could rate posts
     * @param startPost    number of first selected row
     * @param postsPerPage number of selected rows
     * @return container with list of questions and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Post> takeQuestionsByCategory(String categoryId, int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> questions = new PaginatedList<>();
        List<Post> items;

        PreparedStatement selectCategoryQuestionsStatement = null;
        ResultSet questionsResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectCategoryQuestionsStatement = connection.prepareStatement(SQL_SELECT_QUESTIONS_BY_CATEGORY);
            selectCategoryQuestionsStatement.setInt(1, userId);
            selectCategoryQuestionsStatement.setInt(2, Integer.parseInt(categoryId));
            selectCategoryQuestionsStatement.setInt(3, startPost);
            selectCategoryQuestionsStatement.setInt(4, postsPerPage);

            questionsResultSet = selectCategoryQuestionsStatement.executeQuery();
            items = fillQuestionList(questionsResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                questions.setTotalCount(foundRowsResultSet.getInt(1));
                questions.setItems(items);
                questions.setItemsPerPage(postsPerPage);
                questions.setItemStart(startPost);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectCategoryQuestionsStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return questions;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with
     * posts data created by definite user and statement for selecting total rows count of statement
     *
     * @param profileUserId id of user who is author of selecting list of posts
     * @param userId        id of session user who could rate some posts
     * @param startPost     number of first selected row
     * @param postsPerPage  number of selected rows
     * @return container with list of posts and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Post> takePostsByUserId(int profileUserId, int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> posts = new PaginatedList<>();
        List<Post> items;

        PreparedStatement selectUserPostsStatement = null;
        ResultSet userPostsResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectUserPostsStatement = connection.prepareStatement(SQL_SELECT_USERS_POSTS);
            selectUserPostsStatement.setInt(1, userId);
            selectUserPostsStatement.setInt(2, profileUserId);
            selectUserPostsStatement.setInt(3, startPost);
            selectUserPostsStatement.setInt(4, postsPerPage);
            userPostsResultSet = selectUserPostsStatement.executeQuery();
            items = fillPostsList(userPostsResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                posts.setTotalCount(foundRowsResultSet.getInt(1));
                posts.setItems(items);
                posts.setItemsPerPage(postsPerPage);
                posts.setItemStart(startPost);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectUserPostsStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return posts;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about questions of definite category and statement for selecting total rows count of statement
     *
     * @param startCategory     number of first selected row
     * @param categoriesPerPage number of selected rows
     * @return container with list of categories and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Category> takeAllCategories(int startCategory, int categoriesPerPage) throws DAOException {
        PaginatedList<Category> categories = new PaginatedList<>();
        List<Category> items;
        PreparedStatement selectCategoriesStatement = null;
        Statement selectFoundRowsStatement = null;
        ResultSet categoriesResultSet;
        ResultSet foundRowsResultSet;
        try {
            selectCategoriesStatement = connection.prepareStatement(SQL_SELECT_ALL_CATEGORIES);
            selectCategoriesStatement.setInt(1, startCategory);
            selectCategoriesStatement.setInt(2, categoriesPerPage);
            categoriesResultSet = selectCategoriesStatement.executeQuery();
            items = createCategoriesFromResultSet(categoriesResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                categories.setTotalCount(foundRowsResultSet.getInt(1));
                categories.setItems(items);
                categories.setItemsPerPage(categoriesPerPage);
                categories.setItemStart(startCategory);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectCategoriesStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return categories;
    }

    /**
     * creates PreparedStatement for selecting limited number of rows with information
     * about categories that have definite moderator and statement for selecting total rows count of statement
     *
     * @param userId            id of moderator of selecting categories
     * @param startCategory     number of first selected row
     * @param categoriesPerPage number of selected rows
     * @return container with list of categories and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Category> takeModeratedCategories(int userId, int startCategory, int categoriesPerPage) throws DAOException {
        PaginatedList<Category> categories = new PaginatedList<>();
        List<Category> items;
        PreparedStatement selectModeratedCategoriesStatement = null;
        ResultSet categoriesResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectModeratedCategoriesStatement = connection.prepareStatement(SQL_SELECT_MODERATED_CATEGORIES);
            selectModeratedCategoriesStatement.setInt(1, userId);
            selectModeratedCategoriesStatement.setInt(2, startCategory);
            selectModeratedCategoriesStatement.setInt(3, categoriesPerPage);
            categoriesResultSet = selectModeratedCategoriesStatement.executeQuery();
            items = createCategoriesFromResultSet(categoriesResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                categories.setTotalCount(foundRowsResultSet.getInt(1));
                categories.setItems(items);
                categories.setItemsPerPage(categoriesPerPage);
                categories.setItemStart(startCategory);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectModeratedCategoriesStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return categories;
    }

    /**
     * creates Statement for selecting list of cut categories, used while selecting category
     * at 'add new question' form
     *
     * @return list of cut categories
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public List<CategoryInfo> takeCategoriesInfo() throws DAOException {
        List<CategoryInfo> categoriesInfo = null;

        Statement selectCutCategoriesStatement = null;
        ResultSet categoriesResultSet;
        try {
            selectCutCategoriesStatement = connection.getStatement();
            categoriesResultSet = selectCutCategoriesStatement.executeQuery(SQL_SELECT_CATEGORIES_INFO);

            if (!categoriesResultSet.next()) {
                categoriesInfo = null;
            } else {
                categoriesResultSet.beforeFirst();
                categoriesInfo = new ArrayList<>();
                CategoryInfo category;
                while (categoriesResultSet.next()) {
                    category = new CategoryInfo();
                    category.setId(categoriesResultSet.getInt(CATEGORY_ID));
                    category.setTitleEn(categoriesResultSet.getString(TITLE_EN));
                    category.setTitleRu(categoriesResultSet.getString(TITLE_RU));
                    category.setUserId(categoriesResultSet.getInt(MODERATOR_ID));
                    category.setStatus(Category.CategoryStatus.fromValue(categoriesResultSet.getString(CAT_STATUS)));
                    categoriesInfo.add(category);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectCutCategoriesStatement);
        }
        return categoriesInfo;
    }

    /**
     * creates PreparedStatement for selecting cut information about category, used when category
     * doesn't contain any questions yet for showing category info
     *
     * @param categoryId id of category
     * @return cut information about category
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public CategoryInfo takeCategoryInfoById(String categoryId) throws DAOException {
        CategoryInfo info = null;

        PreparedStatement selectCategoryStatement = null;
        ResultSet categoryResultSet;
        try {
            selectCategoryStatement = connection.prepareStatement(SQL_SELECT_SINGLE_CATEGORY_INFO);
            selectCategoryStatement.setInt(1, Integer.parseInt(categoryId));

            categoryResultSet = selectCategoryStatement.executeQuery();
            if (!categoryResultSet.next()) {
                info = null;
            } else {
                categoryResultSet.beforeFirst();
                categoryResultSet.next();

                info = new CategoryInfo();
                info.setId(categoryResultSet.getInt(CATEGORY_ID));
                info.setTitleEn(categoryResultSet.getString(TITLE_EN));
                info.setTitleRu(categoryResultSet.getString(TITLE_RU));
                info.setUserId(categoryResultSet.getInt(MODERATOR_ID));
                info.setStatus(Category.CategoryStatus.fromValue(categoryResultSet.getString(CAT_STATUS)));
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectCategoryStatement);
        }
        return info;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about posts rated by definite user and statement for selecting total rows count of statement
     *
     * @param userId       id of user who rated posts
     * @param startPost    number of first selected row
     * @param postsPerPage number of selected rows
     * @return container with list of rated posts and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Post> takeRatedPosts(int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> posts = new PaginatedList<>();
        List<Post> items;
        PreparedStatement selectRatedPostsStatement = null;
        ResultSet ratedPostsResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectRatedPostsStatement = connection.prepareStatement(SQL_SELECT_RATED_POSTS);
            selectRatedPostsStatement.setInt(1, userId);
            selectRatedPostsStatement.setInt(2, startPost);
            selectRatedPostsStatement.setInt(3, postsPerPage);
            ratedPostsResultSet = selectRatedPostsStatement.executeQuery();
            items = fillPostsList(ratedPostsResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                posts.setTotalCount(foundRowsResultSet.getInt(1));
                posts.setItems(items);
                posts.setItemStart(startPost);
                posts.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error! Check source ", e);
        } finally {
            connection.closeStatement(selectRatedPostsStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return posts;
    }

    /**
     * creates statements for updating post status to 'deleted' and deleting post's rates
     *
     * @param postId id of deleting post
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void deletePostById(int postId) throws DAOException {
        PreparedStatement deleteAnswerStatement = null;
        PreparedStatement deleteQuestionStatement = null;
        PreparedStatement deleteQuestionRatesStatement = null;
        try {
            connection.setAutoCommit(false);
            deleteAnswerStatement = connection.prepareStatement(SQL_UPDATE_ANSWER_STATUS_TO_DELETE);
            deleteAnswerStatement.setInt(1, postId);
            deleteAnswerStatement.executeUpdate();

            deleteQuestionStatement = connection.prepareStatement(SQL_UPDATE_QUESTION_STATUS_TO_DELETE);
            deleteQuestionStatement.setInt(1, postId);
            deleteQuestionStatement.executeUpdate();

            deleteQuestionRatesStatement = connection.prepareStatement(SQL_DELETE_RATE_BY_QUESTION_ID);
            deleteQuestionRatesStatement.setInt(1, postId);
            deleteQuestionRatesStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(deleteAnswerStatement);
            connection.closeStatement(deleteQuestionStatement);
            connection.closeStatement(deleteQuestionRatesStatement);
        }
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about posts of users who are marked as 'following' by definite user
     * and statement for selecting total rows count of statement
     *
     * @param userId       id of user
     * @param startPost    number of first selected row
     * @param postsPerPage number of selected rows
     * @return container with list of posts and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Post> takeFriendsPosts(int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> friendsPosts = new PaginatedList<>();
        List<Post> items;
        PreparedStatement selectFriendsPostsStatement = null;
        ResultSet friendsPostsResultSet;
        Statement selectFoundRowsStatement = null;
        ResultSet foundRowsResultSet;
        try {
            selectFriendsPostsStatement = connection.prepareStatement(SQL_SELECT_ALL_FRIENDS_POSTS);
            selectFriendsPostsStatement.setInt(1, userId);
            selectFriendsPostsStatement.setInt(2, userId);
            selectFriendsPostsStatement.setInt(3, startPost);
            selectFriendsPostsStatement.setInt(4, postsPerPage);
            friendsPostsResultSet = selectFriendsPostsStatement.executeQuery();
            items = fillPostsList(friendsPostsResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                friendsPosts.setTotalCount(foundRowsResultSet.getInt(1));
                friendsPosts.setItems(items);
                friendsPosts.setItemStart(startPost);
                friendsPosts.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectFriendsPostsStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return friendsPosts;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about definite question and list of answers to it and statement for selecting total rows count of statement
     *
     * @param questionId id of question
     * @param userId     id of user who could rate some posts from list
     * @return container with list of posts and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public List<Post> takeQuestionWithAnswersById(int questionId, int userId) throws DAOException {
        List<Post> question = null;
        PreparedStatement selectPostStatement = null;
        ResultSet postResultSet;
        try {
            selectPostStatement = connection.prepareStatement(SQL_SELECT_QUESTION_AND_ANSWERS);
            selectPostStatement.setInt(1, userId);
            selectPostStatement.setInt(2, questionId);
            selectPostStatement.setInt(3, questionId);
            postResultSet = selectPostStatement.executeQuery();
            question = fillQuestionList(postResultSet);
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectPostStatement);
        }
        return question;
    }

    /**
     * method creates PreparedStatement for inserting new answer and checks if user banned and category
     * of question exists and wasn't closed
     *
     * @param userId      id of author
     * @param questionId  id of question
     * @param categoryId  id of category
     * @param description answer content
     * @return status of adding
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public String addNewAnswer(int userId, String questionId, String categoryId, String description) throws DAOException {
        PreparedStatement selectBanStatusStatement = null;
        PreparedStatement selectCategoryIdStatement = null;
        ResultSet banStatusResultSet;
        ResultSet categoryIdStatement;
        PreparedStatement st2 = null;
        String status = OPERATION_FAILED;
        try {
            connection.setAutoCommit(false);
            selectBanStatusStatement = connection.prepareStatement(SQL_SELECT_USER_BAN_STATUS);
            selectBanStatusStatement.setInt(1, userId);
            banStatusResultSet = selectBanStatusStatement.executeQuery();
            if (banStatusResultSet.next()) {
                status = USER_BANNED;
            } else {
                selectCategoryIdStatement = connection.prepareStatement(SQL_SELECT_NOT_CLOSED_CATEGORY_ID);
                selectCategoryIdStatement.setInt(1, Integer.parseInt(categoryId));
                categoryIdStatement = selectCategoryIdStatement.executeQuery();
                if (categoryIdStatement.next()) {
                    st2 = connection.prepareStatement(SQL_ADD_NEW_ANSWER);
                    st2.setInt(1, userId);
                    st2.setInt(2, Integer.parseInt(categoryId));
                    st2.setString(3, ANSWER_TYPE);
                    st2.setString(4, description);
                    st2.setInt(5, Integer.parseInt(questionId));

                    st2.executeUpdate();
                    status = OPERATION_PROCESSED;
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Exception while adding new answer ", e);
        } finally {
            connection.closeStatement(selectBanStatusStatement);
            connection.closeStatement(selectCategoryIdStatement);
            connection.closeStatement(st2);
        }
        return status;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about best questions sorted by average mark in reverse order
     * and statement for selecting total rows count of statement
     *
     * @param userId       id of user who could rate some posts from list
     * @param startPost    number of first selected row
     * @param postsPerPage number of selected rows
     * @return container with list of best questions sorted in reverse order by average mark and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Post> takeBestQuestions(int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> questions = new PaginatedList<>();
        List<Post> items;
        PreparedStatement selectBestQuestionsStatement = null;
        Statement selectFoundRowsStatement = null;
        ResultSet bestQuestionsResultSet;
        ResultSet foundRowsResultSet;
        try {
            selectBestQuestionsStatement = connection.prepareStatement(SQL_SELECT_BEST_QUESTIONS);
            selectBestQuestionsStatement.setInt(1, userId);
            selectBestQuestionsStatement.setInt(2, startPost);
            selectBestQuestionsStatement.setInt(3, postsPerPage);
            bestQuestionsResultSet = selectBestQuestionsStatement.executeQuery();
            items = fillQuestionList(bestQuestionsResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                questions.setTotalCount(foundRowsResultSet.getInt(1));
                questions.setItems(items);
                questions.setItemStart(startPost);
                questions.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectBestQuestionsStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return questions;
    }

    /**
     * method creates PreparedStatement for selecting limited number of rows with information
     * about best answers sorted by average mark in reverse order
     * and statement for selecting total rows count of statement
     *
     * @param userId       id of user who could rate some posts from list
     * @param startPost    number of first selected row
     * @param postsPerPage number of selected rows
     * @return container with list of best answers sorted in reverse order by average mark and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Post> takeBestAnswers(int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> answers = new PaginatedList<>();
        List<Post> items;
        PreparedStatement selectBestAnswersStatement = null;
        Statement selectFoundRowsStatement = null;
        ResultSet bestAnswersResultSet;
        ResultSet foundRowsResultSet;
        try {
            selectBestAnswersStatement = connection.prepareStatement(SQL_SELECT_BEST_ANSWERS_A_Q);
            selectBestAnswersStatement.setInt(1, userId);
            selectBestAnswersStatement.setInt(2, startPost);
            selectBestAnswersStatement.setInt(3, postsPerPage);
            bestAnswersResultSet = selectBestAnswersStatement.executeQuery();
            items = fillPostsList(bestAnswersResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                answers.setTotalCount(foundRowsResultSet.getInt(1));
                answers.setItems(items);
                answers.setItemStart(startPost);
                answers.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source ", e);
        } finally {
            connection.closeStatement(selectBestAnswersStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return answers;
    }

    /**
     * creates PreparedStatements that inserts or updates rate
     *
     * @param postId id of rated post
     * @param mark   value
     * @param userId id of user who rates post
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void addNewRate(int postId, int mark, int userId) throws DAOException {
        PreparedStatement deleteRateStatement = null;
        PreparedStatement insertRateStatement = null;
        try {
            connection.setAutoCommit(false);
            deleteRateStatement = connection.prepareStatement(SQL_DELETE_RATE);
            deleteRateStatement.setInt(1, userId);
            deleteRateStatement.setInt(2, postId);
            deleteRateStatement.executeUpdate();

            insertRateStatement = connection.prepareStatement(SQL_INSERT_NEW_RATE);
            insertRateStatement.setInt(1, userId);
            insertRateStatement.setInt(2, postId);
            insertRateStatement.setInt(3, mark);
            insertRateStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(deleteRateStatement);
            connection.closeStatement(insertRateStatement);
        }
    }

    /**
     * method creates statements for updating answer data and checking if user not
     * blocked and category not closed or exists
     *
     * @param userId id of user who corrects answer
     * @param answerId    id of corrected answer
     * @param description answer content
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public String addCorrectedAnswer(int userId, int answerId, String description) throws DAOException {
        PreparedStatement selectBanStatusStatement = null;
        PreparedStatement selectNotClosedCategoryIdStatement = null;
        PreparedStatement updateAnswerStatement = null;

        ResultSet banStatusResultSet;
        ResultSet categoryIdResultSet;
        String status = OPERATION_FAILED;
        try {
            connection.setAutoCommit(false);
            selectBanStatusStatement = connection.prepareStatement(SQL_SELECT_USER_BAN_STATUS);
            selectBanStatusStatement.setInt(1, userId);
            banStatusResultSet = selectBanStatusStatement.executeQuery();
            if (banStatusResultSet.next()) {
                status = USER_BANNED;
            } else {
                selectNotClosedCategoryIdStatement = connection.prepareStatement(SQL_SELECT_NOT_CLOSED_CATEGORY_ID_OF_ANSWER);
                selectNotClosedCategoryIdStatement.setInt(1, answerId);
                categoryIdResultSet = selectNotClosedCategoryIdStatement.executeQuery();
                if (categoryIdResultSet.next()) {
                    updateAnswerStatement = connection.prepareStatement(SQL_UPDATE_ANSWER_DESCRIPTION);
                    updateAnswerStatement.setInt(2, answerId);
                    updateAnswerStatement.setString(1, description);
                    updateAnswerStatement.executeUpdate();

                    status = OPERATION_PROCESSED;
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error,check source", e);
        } finally {
            connection.closeStatement(updateAnswerStatement);
            connection.closeStatement(selectBanStatusStatement);
            connection.closeStatement(selectNotClosedCategoryIdStatement);
        }
        return status;
    }

    /**
     * method creates statements for updating question data and checking if user not
     * blocked and category not closed or exists
     *
     * @param userId id of user who corrects question
     * @param questionId     id of corrected question
     * @param catId          category id
     * @param correctedTitle corrected title
     * @param description    question content
     * @return operation status
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public String addCorrectedQuestion(int userId, int questionId, int catId, String correctedTitle, String description) throws DAOException {
        PreparedStatement selectBanStatusStatement = null;
        PreparedStatement selectNotClosedCategoryIdStatement = null;
        PreparedStatement updateQuestionStatement = null;
        PreparedStatement updateAnswersCategoryStatement = null;
        ResultSet banStatusResultSet;
        ResultSet categoryIdResultSet;
        String status = OPERATION_FAILED;
        try {
            connection.setAutoCommit(false);
            selectBanStatusStatement = connection.prepareStatement(SQL_SELECT_USER_BAN_STATUS);
            selectBanStatusStatement.setInt(1, userId);
            banStatusResultSet = selectBanStatusStatement.executeQuery();
            if (banStatusResultSet.next()) {
                status = USER_BANNED;
            } else {
                selectNotClosedCategoryIdStatement = connection.prepareStatement(SQL_SELECT_NOT_CLOSED_CATEGORY_ID);
                selectNotClosedCategoryIdStatement.setInt(1, catId);
                categoryIdResultSet = selectNotClosedCategoryIdStatement.executeQuery();
                if (categoryIdResultSet.next()) {
                    updateQuestionStatement = connection.prepareStatement(SQL_UPDATE_QUESTION);
                    updateQuestionStatement.setInt(1, catId);
                    updateQuestionStatement.setString(2, correctedTitle);
                    updateQuestionStatement.setString(3, description);
                    updateQuestionStatement.setInt(4, questionId);
                    updateQuestionStatement.executeUpdate();

                    updateAnswersCategoryStatement = connection.prepareStatement(SQL_UPDATE_ANSWERS_CATEGORY);
                    updateAnswersCategoryStatement.setInt(1, catId);
                    updateAnswersCategoryStatement.setInt(2, questionId);
                    updateAnswersCategoryStatement.executeUpdate();

                    status = OPERATION_PROCESSED;
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectBanStatusStatement);
            connection.closeStatement(selectNotClosedCategoryIdStatement);
            connection.closeStatement(updateQuestionStatement);
            connection.closeStatement(updateAnswersCategoryStatement);
        }
        return status;
    }

    /**
     * method creates statements for inserting new complaint
     *
     * @param userId          id of author
     * @param complaintPostId id of post caused complaint
     * @param description     cause of complaint
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public void addNewComplaint(int userId, int complaintPostId, String description) throws DAOException {
        PreparedStatement insertComplaintStatement = null;
        try {
            insertComplaintStatement = connection.prepareStatement(SQL_INSERT_NEW_COMPLAINT);
            insertComplaintStatement.setInt(1, complaintPostId);
            insertComplaintStatement.setInt(2, userId);
            insertComplaintStatement.setString(3, description);

            insertComplaintStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAODuplicatedInfoException("Complaint (userId=" + userId + ", postId=" + complaintPostId + "  has already exist.", e);
        } finally {
            connection.closeStatement(insertComplaintStatement);
        }

    }

    /**
     * @param userId       id of user who could rate any posts from result list
     * @param content      key words
     * @param startPost    number of first selected row
     * @param postsPerPage number of selected rows
     * @return container with list of found posts and pagination parameters
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public PaginatedList<Post> searchPostsByKeyWords(int userId, String content, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> posts = new PaginatedList<>();
        List<Post> items;
        PreparedStatement selectPostsByKeyWordsStatement = null;
        Statement selectFoundRowsStatement = null;
        ResultSet foundPostsResultSet;
        ResultSet foundRowsResultSet;
        try {
            selectPostsByKeyWordsStatement = connection.prepareStatement(SQL_SELECT_POSTS_BY_KEY_WORDS);
            selectPostsByKeyWordsStatement.setInt(1, userId);
            selectPostsByKeyWordsStatement.setString(2, content);
            selectPostsByKeyWordsStatement.setInt(3, startPost);
            selectPostsByKeyWordsStatement.setInt(4, postsPerPage);
            foundPostsResultSet = selectPostsByKeyWordsStatement.executeQuery();
            items = fillPostsList(foundPostsResultSet);

            selectFoundRowsStatement = connection.getStatement();
            foundRowsResultSet = selectFoundRowsStatement.executeQuery(SQL_SELECT_FOUND_ROWS);
            if (foundRowsResultSet.next()) {
                posts.setTotalCount(foundRowsResultSet.getInt(1));
                posts.setItems(items);
                posts.setItemStart(startPost);
                posts.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(selectPostsByKeyWordsStatement);
            connection.closeStatement(selectFoundRowsStatement);
        }
        return posts;
    }

    /**
     * method creates statements for inserting new question
     *
     * @return status of operation
     * @throws DAOException if exception while processing SQL statement and connection will be caught
     */
    @Override
    public String addNewQuestion(int userId, String categoryId, String title, String description) throws DAOException {
        PreparedStatement selectBanStatusStatement = null;
        PreparedStatement selectNotClosedCategoryIdStatement = null;
        ResultSet banStatusResultSet;
        ResultSet categoryIdResultSet;
        PreparedStatement insertQuestionStatement = null;
        String status = OPERATION_FAILED;
        try {
            connection.setAutoCommit(false);
            selectBanStatusStatement = connection.prepareStatement(SQL_SELECT_USER_BAN_STATUS);
            selectBanStatusStatement.setInt(1, userId);
            banStatusResultSet = selectBanStatusStatement.executeQuery();
            if (banStatusResultSet.next()) {
                status = USER_BANNED;
            } else {
                selectNotClosedCategoryIdStatement = connection.prepareStatement(SQL_SELECT_NOT_CLOSED_CATEGORY_ID);
                selectNotClosedCategoryIdStatement.setInt(1, Integer.parseInt(categoryId));
                categoryIdResultSet = selectNotClosedCategoryIdStatement.executeQuery();
                if (categoryIdResultSet.next()) {
                    insertQuestionStatement = connection.prepareStatement(SQL_INSERT_NEW_QUESTION);
                    insertQuestionStatement.setInt(1, userId);
                    insertQuestionStatement.setInt(2, Integer.parseInt(categoryId));
                    insertQuestionStatement.setString(3, QUESTION_TYPE);
                    insertQuestionStatement.setString(4, title);
                    insertQuestionStatement.setString(5, description);

                    insertQuestionStatement.executeUpdate();
                    status = OPERATION_PROCESSED;
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Exception while adding new question ", e);
        } finally {
            connection.closeStatement(selectBanStatusStatement);
            connection.closeStatement(selectNotClosedCategoryIdStatement);
            connection.closeStatement(insertQuestionStatement);
        }
        return status;
    }

    /**
     * inner method that forms list of posts from ResultSet
     *
     * @param postsResultSet result set
     * @return list of posts
     * @throws SQLException if exception while processing SQL statement will be caught
     */
    private List<Post> fillPostsList(ResultSet postsResultSet) throws SQLException {
        List<Post> posts;
        if (!postsResultSet.next()) {
            posts = null;
        } else {
            posts = new ArrayList<>();
            postsResultSet.beforeFirst();
            Post post;
            while (postsResultSet.next()) {
                post = new Post();
                post.setId(postsResultSet.getInt(POST_ID));
                post.setType(Post.PostType.fromValue(postsResultSet.getString(POST_TYPE)));
                post.setTitle(postsResultSet.getString(POST_TITLE));
                post.setContent(postsResultSet.getString(POST_CONTENT));
                post.setStatus(Post.Status.fromValue(postsResultSet.getString(POST_STATUS)));
                post.setPublishedTime(postsResultSet.getTimestamp(POST_PUBLISHED_TIME));
                post.setModifiedTime(postsResultSet.getTimestamp(POST_MODIFIED_TIME));
                post.setAverageMark(postsResultSet.getDouble(MARK));
                post.setCurrentUserMark(postsResultSet.getInt(CURRENT_USER_MARK));
                post.setParentId(postsResultSet.getInt(PARENT_ID));
                post.setParentTitle(postsResultSet.getString(PARENT_TITLE));
                CategoryInfo catInfo = new CategoryInfo();
                catInfo.setId(postsResultSet.getInt(CATEGORY_ID));
                catInfo.setTitleEn(postsResultSet.getString(TITLE_EN));
                catInfo.setTitleRu(postsResultSet.getString(TITLE_RU));
                catInfo.setUserId(postsResultSet.getInt(MODERATOR_ID));
                catInfo.setStatus(Category.CategoryStatus.fromValue(postsResultSet.getString(CAT_STATUS)));
                post.setCategoryInfo(catInfo);
                ShortUser author = new ShortUser();
                author.setId(postsResultSet.getInt(USER_ID));
                author.setRole(Role.fromValue(postsResultSet.getString(ROLE)));
                author.setAvatar(postsResultSet.getString(AVATAR));
                author.setLogin(postsResultSet.getString(LOGIN));
                post.setUser(author);
                posts.add(post);
            }
        }
        return posts;
    }

    /**
     * inner method that forms list of questions from ResultSet
     *
     * @param questionsResultSet result set
     * @return list of questions
     * @throws SQLException if exception while processing SQL statement will be caught
     */
    private List<Post> fillQuestionList(ResultSet questionsResultSet) throws SQLException {
        List<Post> questions;
        if (!questionsResultSet.next()) {
            questions = null;
        } else {
            questions = new ArrayList<>();
            questionsResultSet.beforeFirst();
            Post question;
            while (questionsResultSet.next()) {
                question = new Post();
                question.setId(questionsResultSet.getInt(POST_ID));
                question.setType(Post.PostType.fromValue(questionsResultSet.getString(POST_TYPE)));
                question.setTitle(questionsResultSet.getString(POST_TITLE));
                question.setContent(questionsResultSet.getString(POST_CONTENT));
                question.setStatus(Post.Status.fromValue(questionsResultSet.getString(POST_STATUS)));
                question.setPublishedTime(questionsResultSet.getTimestamp(POST_PUBLISHED_TIME));
                question.setModifiedTime(questionsResultSet.getTimestamp(POST_MODIFIED_TIME));
                question.setAverageMark(questionsResultSet.getDouble(MARK));
                question.setCurrentUserMark(questionsResultSet.getInt(CURRENT_USER_MARK));
                CategoryInfo catInfo = new CategoryInfo();
                catInfo.setId(questionsResultSet.getInt(CATEGORY_ID));
                catInfo.setTitleEn(questionsResultSet.getString(TITLE_EN));
                catInfo.setTitleRu(questionsResultSet.getString(TITLE_RU));
                catInfo.setUserId(questionsResultSet.getInt(MODERATOR_ID));
                catInfo.setStatus(Category.CategoryStatus.fromValue(questionsResultSet.getString(CAT_STATUS)));
                question.setCategoryInfo(catInfo);
                ShortUser author = new ShortUser();
                author.setId(questionsResultSet.getInt(USER_ID));
                author.setRole(Role.fromValue(questionsResultSet.getString(ROLE)));
                author.setAvatar(questionsResultSet.getString(AVATAR));
                author.setLogin(questionsResultSet.getString(LOGIN));
                question.setUser(author);
                questions.add(question);
            }
        }
        return questions;
    }

    /**
     * inner method that forms list of categories from ResultSet
     *
     * @param categoriesResultSet result set
     * @return list of categories
     * @throws SQLException if exception while processing SQL statement will be caught
     */
    private List<Category> createCategoriesFromResultSet(ResultSet categoriesResultSet) throws SQLException {
        List<Category> categories;
        if (!categoriesResultSet.next()) {
            categories = null;
        } else {
            categoriesResultSet.beforeFirst();
            categories = new ArrayList<>();
            Category category;
            while (categoriesResultSet.next()) {
                category = new Category();
                category.setId(categoriesResultSet.getInt(CATEGORY_ID));
                category.setCreationDate(categoriesResultSet.getDate(CAT_CREATION_DATE));
                category.setTitleEn(categoriesResultSet.getString(TITLE_EN));
                category.setTitleRu(categoriesResultSet.getString(TITLE_RU));
                category.setDescriptionEn(categoriesResultSet.getString(DESCRIPTION_EN));
                category.setDescriptionRu(categoriesResultSet.getString(DESCRIPTION_RU));
                category.setStatus(Category.CategoryStatus.fromValue(categoriesResultSet.getString(CAT_STATUS)));
                category.setUserId(categoriesResultSet.getInt(USER_ID));
                category.setQuestionQuantity(categoriesResultSet.getInt(QUANTITY));
                category.setImageLink(categoriesResultSet.getString(CAT_IMAGE));
                ShortUser moderator = new ShortUser();
                moderator.setId(categoriesResultSet.getInt(USER_ID));
                moderator.setRole(Role.fromValue(categoriesResultSet.getString(ROLE)));
                moderator.setAvatar(categoriesResultSet.getString(AVATAR));
                moderator.setLogin(categoriesResultSet.getString(LOGIN));
                category.setModerator(moderator);
                categories.add(category);
            }
        }
        return categories;
    }
}