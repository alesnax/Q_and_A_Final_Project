package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.PostDAO;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.pool.WrappedConnection;
import by.alesnax.qanda.entity.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 *
 */

@SuppressWarnings("Duplicates")
public class PostDAOImpl extends AbstractDAO<Integer, Post> implements PostDAO {
    private static Logger logger = LogManager.getLogger(PostDAOImpl.class);

    private static final String SQL_SELECT_ALL_CATEGORIES = "SELECT sql_calc_found_rows categories.id AS category_id, categories.users_id as users_id, title_en, title_ru, creation_date, description_ru, description_en, categories.status AS status, image, " +
            "login, avatar, role, count(posts.id) AS quantity " +
            "FROM categories LEFT  JOIN posts ON (posts.category_id = categories.id AND posts.type='question' AND posts.status!='deleted') JOIN users ON users.id = categories.users_id " +
            "group by categories.id ORDER BY quantity DESC, category_id LIMIT ?,?;";

    private static final String SQL_SELECT_MODERATED_CATEGORIES = "SELECT sql_calc_found_rows categories.id AS category_id, categories.users_id as users_id, title_en, title_ru, creation_date, description_ru, description_en, categories.status AS status, image,\n" +
            "login, avatar, role, count(posts.id) AS quantity\n" +
            "FROM categories LEFT JOIN posts ON (posts.category_id = categories.id AND posts.type='question') JOIN users ON (users.id = categories.users_id AND users.id = ?)\n" +
            " group by categories.id LIMIT ?,?;";

    private static final String SQL_SELECT_CATEGORIES_INFO = "SELECT categories.id AS category_id, title_en, title_ru, categories.users_id AS moderator_id FROM categories WHERE status!='closed';";

    private static final String SQL_SELECT_SINGLE_CATEGORY_INFO = "SELECT categories.id AS category_id, title_en, title_ru, categories.users_id AS moderator_id  FROM categories WHERE categories.id=?";

    private static final String SQL_ADD_NEW_QUESTION = "INSERT INTO posts " +
            "(`users_id`, `category_id`, `type`, `title`, `content`) VALUES (?,?,?,?,?);";  //+++

    private static final String SQL_ADD_NEW_ANSWER = "INSERT INTO posts (users_id, category_id, type, content, parent_id) VALUES (?,?,?,?,?);";

    //MY PROFILE
    private static final String SQL_SELECT_USERS_POSTS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, posts.type, posts.title, posts.content, posts.status,\n" +
            "posts.published_time, posts.modified_time, posts.parent_id, parent.title AS parent_title,  AVG(coalesce(rates.value, 0)) AS mark, categories.title_en, categories.title_ru, " +
            "categories.users_id AS moderator_id, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id JOIN categories ON categories.id = posts.category_id LEFT JOIN posts AS parent ON posts.parent_id = parent.id " +
            "LEFT JOIN rates ON posts.id = rates.posts_id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type != 'service' AND posts.status != 'deleted'  AND users.id = ?\n" +
            "GROUP BY posts.id ORDER BY published_time DESC LIMIT ?,?";

    // I LIKED IT
    private static final String SQL_SELECT_LIKED_POSTS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru, posts.type, posts.title, \n" +
            "posts.status, posts.published_time, posts.modified_time, posts.content, AVG(coalesce(rates.value, 0)) AS mark,\n" +
            "posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role, r.value, categories.users_id AS moderator_id\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type != 'service' AND posts.status != 'deleted' GROUP BY posts.id ORDER BY rates.adding_time DESC LIMIT ?,?";

    // MY NEWS
    private static final String SQL_SELECT_ALL_FRIENDS_POSTS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru, posts.type,  \n" +
            "posts.title, posts.status, posts.published_time, posts.modified_time, posts.content, AVG(coalesce(rates.value, 0)) AS mark, categories.users_id AS moderator_id,\n" +
            " posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type != 'service' AND posts.status != 'deleted' AND posts.users_id IN (SELECT users_friend_id FROM friends WHERE friends.users_id=? AND friends.state = 'follower')\n" +
            "GROUP BY posts.id ORDER BY posts.published_time DESC LIMIT ?,?";

    // BEST QUESTIONS
    private static final String SQL_SELECT_BEST_QUESTIONS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, posts.type,   posts.title,  posts.content, posts.status,\n" +
            "cast(posts.published_time AS datetime) AS published_time, cast(posts.modified_time AS datetime) AS modified_time,\n" +
            " AVG(coalesce(rates.value, 0)) AS mark, categories.title_en, categories.title_ru, categories.users_id AS moderator_id, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id JOIN categories ON categories.id = posts.category_id \n" +
            "JOIN rates ON posts.id = rates.posts_id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type = 'question' AND posts.status != 'deleted' GROUP BY posts.id ORDER BY mark DESC LIMIT ?, ?";         // +++++
    //+++
    // BEST ANSWERS
    private static final String SQL_SELECT_BEST_ANSWERS_A_Q = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru,  categories.users_id AS moderator_id, posts.type,  \n" +
            "posts.title, posts.status, posts.published_time, posts.modified_time, posts.content, AVG(coalesce(rates.value, 0)) AS mark,\n" +
            "posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type = 'answer' AND posts.status != 'deleted'\n" +
            "GROUP BY posts.id ORDER BY mark DESC LIMIT ?, ?;";


    private static final String SQL_SELECT_QUESTIONS_BY_CATEGORY = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, posts.type,   posts.title, posts.content, posts.status, cast(posts.published_time AS datetime) AS published_time, \n" +
            "cast(posts.modified_time AS datetime) AS modified_time, AVG(coalesce(rates.value, 0)) AS mark, categories.title_en, categories.title_ru, categories.users_id AS moderator_id, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id JOIN categories ON categories.id = posts.category_id LEFT JOIN rates ON posts.id = rates.posts_id LEFT JOIN rates AS r " +
            "ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE posts.type != 'answer' AND posts.status != 'deleted' AND posts.category_id=? GROUP BY posts.id " +
            "ORDER BY published_time DESC  LIMIT ?,?";


    private static final String SQL_UPDATE_QUESTION_STATUS_TO_DELETE = "UPDATE posts SET status='deleted', modified_time=CURRENT_TIMESTAMP WHERE id=?;";

    private static final String SQL_UPDATE_ANSWER_STATUS_TO_DELETE = "UPDATE posts SET status='deleted', modified_time=CURRENT_TIMESTAMP WHERE parent_id=?;";

    private static final String SQL_UPDATE_ANSWER_DESCRIPTION = "UPDATE posts SET status='modified', content=?, modified_time=CURRENT_TIMESTAMP WHERE id=?;\n";

    private static final String SQL_UPDATE_QUESTION = "UPDATE posts SET category_id=?, title=?, content=?, status='modified', modified_time=CURRENT_TIMESTAMP WHERE id=?;\n";

    private static final String SQL_DELETE_RATE_BY_QUESTION_ID = "DELETE FROM rates WHERE posts_id=?;";

    private static final String SQL_DELETE_RATE = "DELETE FROM rates WHERE users_id=? and posts_id=?;";

    private static final String SQL_INSERT_NEW_RATE = "INSERT INTO rates (users_id, posts_id, value) VALUES (?, ?, ?);";

    private static final String SQL_SELECT_QUESTION_AND_ANSWERS = "SELECT posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru, posts.type, \n" +
            " categories.users_id AS moderator_id, posts.title, posts.status, posts.published_time, posts.modified_time, posts.content, \n" +
            "AVG(coalesce(rates.value, 0)) AS mark, users.login, users.avatar, users.role, r.value\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id = r.posts_id )\n" +
            "WHERE posts.type != 'service' AND posts.status != 'deleted' AND posts.id IN (SELECT id FROM posts WHERE id=? UNION DISTINCT\n" +
            "SELECT id FROM posts WHERE parent_id=?) GROUP BY posts.id ORDER BY posts.type ASC";

    private static final String SQL_SELECT_POST = "SELECT posts.id, posts.users_id, posts.category_id, " +
            "categories.title_en, categories.title_ru, categories.users_id AS moderator_id, posts.type,\n" +
            "posts.title, posts.status, posts.published_time, posts.modified_time, posts.content,\n" +
            "posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN posts AS parent ON posts.parent_id = parent.id\n" +
            "WHERE posts.type != 'service' AND posts.id=? GROUP BY posts.id";

    private static final String SQL_ADD_NEW_COMPLAINT = "INSERT INTO complaints (`posts_id`, `users_id`, `description`) VALUES (?, ?, ?);";

    private static final String SQL_SELECT_POSTS_BY_KEY_WORDS = "SELECT sql_calc_found_rows posts.id, posts.users_id, posts.category_id, categories.title_en, categories.title_ru, posts.type, posts.title,\n" +
            "posts.status, posts.published_time, posts.modified_time, posts.content, AVG(coalesce(rates.value, 0)) AS mark,\n" +
            "posts.parent_id, parent.title AS parent_title, users.login, users.avatar, users.role, r.value, categories.users_id AS moderator_id\n" +
            "FROM posts JOIN users ON users.id = posts.users_id JOIN categories ON posts.category_id=categories.id LEFT JOIN rates ON posts.id = rates.posts_id \n" +
            "LEFT JOIN posts AS parent ON posts.parent_id = parent.id LEFT JOIN rates AS r ON ( r.users_id=? AND posts.id=r.posts_id )\n" +
            "WHERE  posts.status != 'deleted' AND MATCH (posts.title, posts.content) AGAINST (?)\n" +
            "GROUP BY posts.id ORDER BY posts.published_time DESC LIMIT ?,?";

    private static final String SQL_SELECT_FOUND_ROWS = "SELECT FOUND_ROWS()";

    private static final String SQL_UPDATE_ANSWERS_CATEGORY = "UPDATE posts SET category_id=? WHERE parent_id=?;";

    private static final String CATEGORY_ID = "category_id";
    private static final String CAT_CREATION_DATE = "creation_date";
    private static final String TITLE_EN = "title_en";
    private static final String TITLE_RU = "title_ru";
    private static final String QUESTION_TYPE = "question";
    private static final String ANSWER_TYPE = "answer";
    private static final String DESCRIPTION_EN = "description_en";
    private static final String DESCRIPTION_RU = "description_ru";
    private static final String CAT_IMAGE = "image";
    private static final String CAT_STATUS = "status";
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


    public PostDAOImpl(WrappedConnection connection) {
        super(connection);
    }

    @Override
    public Post findEntityById(Integer id) {
        throw new UnsupportedOperationException();
    }


    @Override
    public PaginatedList<Post> takeQuestionsByCategory(String categoryId, int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> questions = new PaginatedList<>();
        List<Post> items;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_QUESTIONS_BY_CATEGORY);
            st.setInt(1, userId);
            st.setInt(2, Integer.parseInt(categoryId));
            st.setInt(3, startPost);
            st.setInt(4, postsPerPage);

            rs = st.executeQuery();
            items = fillQuestionList(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                questions.setTotalCount(rs1.getInt(1));
                questions.setItems(items);
                questions.setItemsPerPage(postsPerPage);
                questions.setItemStart(startPost);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return questions;
    }

    @Override
    public PaginatedList<Post> takePostsByUserId(int profileUserId, int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> posts = new PaginatedList<>();
        List<Post> items;

        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_USERS_POSTS);
            st.setInt(1, userId);
            st.setInt(2, profileUserId);
            st.setInt(3, startPost);
            st.setInt(4, postsPerPage);
            rs = st.executeQuery();
            items = fillPostsList(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                posts.setTotalCount(rs1.getInt(1));
                posts.setItems(items);
                posts.setItemsPerPage(postsPerPage);
                posts.setItemStart(startPost);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return posts;
    }

    @Override
    public PaginatedList<Category> takeAllCategories(int startCategory, int categoriesPerPage) throws DAOException {
        PaginatedList<Category> categories = new PaginatedList<>();
        List<Category> items;
        PreparedStatement st = null;
        Statement st1 = null;
        ResultSet rs;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_ALL_CATEGORIES);
            st.setInt(1, startCategory);
            st.setInt(2, categoriesPerPage);
            rs = st.executeQuery();
            items = createCategoriesFromResultSet(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                categories.setTotalCount(rs1.getInt(1));
                categories.setItems(items);
                categories.setItemsPerPage(categoriesPerPage);
                categories.setItemStart(startCategory);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return categories;
    }


    @Override
    public PaginatedList<Category> takeModeratedCategories(int userId, int startCategory, int categoriesPerPage) throws DAOException {
        PaginatedList<Category> categories = new PaginatedList<>();
        List<Category> items;
        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_MODERATED_CATEGORIES);
            st.setInt(1, userId);
            st.setInt(2, startCategory);
            st.setInt(3, categoriesPerPage);
            rs = st.executeQuery();
            items = createCategoriesFromResultSet(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                categories.setTotalCount(rs1.getInt(1));
                categories.setItems(items);
                categories.setItemsPerPage(categoriesPerPage);
                categories.setItemStart(startCategory);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return categories;
    }

    @Override
    public List<CategoryInfo> takeCategoriesInfo() throws DAOException {
        List<CategoryInfo> categoriesInfo = null;

        Statement st = null;
        ResultSet rs;
        try {
            st = connection.getStatement();
            rs = st.executeQuery(SQL_SELECT_CATEGORIES_INFO);

            if (!rs.next()) {
                categoriesInfo = null;
            } else {
                rs.beforeFirst();
                categoriesInfo = new ArrayList<>();
                CategoryInfo category;
                while (rs.next()) {
                    category = new CategoryInfo();
                    category.setId(rs.getInt(CATEGORY_ID));
                    category.setTitleEn(rs.getString(TITLE_EN));
                    category.setTitleRu(rs.getString(TITLE_RU));
                    category.setUserId(rs.getInt(MODERATOR_ID));
                    categoriesInfo.add(category);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return categoriesInfo;
    }

    @Override
    public CategoryInfo takeCategoryInfoById(String categoryId) throws DAOException {
        CategoryInfo info = null;

        PreparedStatement st = null;
        ResultSet rs;
        try {
            st = connection.prepareStatement(SQL_SELECT_SINGLE_CATEGORY_INFO);
            st.setInt(1, Integer.parseInt(categoryId));

            rs = st.executeQuery();
            if (!rs.next()) {
                info = null;
            } else {
                rs.beforeFirst();
                rs.next();

                info = new CategoryInfo();
                info.setId(rs.getInt(CATEGORY_ID));
                info.setTitleEn(rs.getString(TITLE_EN));
                info.setTitleRu(rs.getString(TITLE_RU));
                info.setUserId(rs.getInt(MODERATOR_ID));
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return info;
    }

    @Override
    public PaginatedList<Post> takeLikedPosts(int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> posts = new PaginatedList<>();
        List<Post> items;
        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_LIKED_POSTS);
            st.setInt(1, userId);
            st.setInt(2, startPost);
            st.setInt(3, postsPerPage);
            rs = st.executeQuery();
            items = fillPostsList(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                posts.setTotalCount(rs1.getInt(1));
                posts.setItems(items);
                posts.setItemStart(startPost);
                posts.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error! Check source ", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return posts;
    }

    @Override
    public void deletePostById(int postId) throws DAOException {
        PreparedStatement st1 = null;
        PreparedStatement st2 = null;
        PreparedStatement st3 = null;
        try {
            connection.setAutoCommit(false);
            st1 = connection.prepareStatement(SQL_UPDATE_ANSWER_STATUS_TO_DELETE);
            st1.setInt(1, postId);
            st1.executeUpdate();

            st2 = connection.prepareStatement(SQL_UPDATE_QUESTION_STATUS_TO_DELETE);
            st2.setInt(1, postId);
            st2.executeUpdate();

            st3 = connection.prepareStatement(SQL_DELETE_RATE_BY_QUESTION_ID);
            st3.setInt(1, postId);
            st3.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st1);
            connection.closeStatement(st2);
            connection.closeStatement(st3);
        }
    }

    @Override
    public PaginatedList<Post> takeFriendsPosts(int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> friendsPosts = new PaginatedList<>();
        List<Post> items;
        PreparedStatement st = null;
        ResultSet rs;
        Statement st1 = null;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_ALL_FRIENDS_POSTS);
            st.setInt(1, userId);
            st.setInt(2, userId);
            st.setInt(3, startPost);
            st.setInt(4, postsPerPage);
            rs = st.executeQuery();
            items = fillPostsList(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                friendsPosts.setTotalCount(rs1.getInt(1));
                friendsPosts.setItems(items);
                friendsPosts.setItemStart(startPost);
                friendsPosts.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return friendsPosts;
    }

    @Override
    public List<Post> takeQuestionWithAnswersById(int questionId, int userId) throws DAOException {
        List<Post> question = null;
        PreparedStatement st = null;
        ResultSet rs;
        try {
            st = connection.prepareStatement(SQL_SELECT_QUESTION_AND_ANSWERS);
            st.setInt(1, userId);
            st.setInt(2, questionId);
            st.setInt(3, questionId);
            rs = st.executeQuery();
            question = fillQuestionList(rs);
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return question;
    }

    @Override
    public void addNewAnswer(int userId, String questionId, String categoryId, String description) throws DAOException {
        PreparedStatement st = null;

        try {
            st = connection.prepareStatement(SQL_ADD_NEW_ANSWER);
            st.setInt(1, userId);
            st.setInt(2, Integer.parseInt(categoryId));
            st.setString(3, ANSWER_TYPE);
            st.setString(4, description);
            st.setInt(5, Integer.parseInt(questionId));

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Exception while adding new answer ", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    @Override
    public PaginatedList<Post> takeBestQuestions(int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> questions = new PaginatedList<>();
        List<Post> items;
        PreparedStatement st = null;
        Statement st1 = null;
        ResultSet rs;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_BEST_QUESTIONS);
            st.setInt(1, userId);
            st.setInt(2, startPost);
            st.setInt(3, postsPerPage);
            rs = st.executeQuery();
            items = fillQuestionList(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                questions.setTotalCount(rs1.getInt(1));
                questions.setItems(items);
                questions.setItemStart(startPost);
                questions.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return questions;
    }

    @Override
    public PaginatedList<Post> takeBestAnswers(int userId, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> answers = new PaginatedList<>();
        List<Post> items;
        PreparedStatement st = null;
        Statement st1 = null;
        ResultSet rs;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_BEST_ANSWERS_A_Q);
            st.setInt(1, userId);
            st.setInt(2, startPost);
            st.setInt(3, postsPerPage);
            rs = st.executeQuery();
            items = fillPostsList(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                answers.setTotalCount(rs1.getInt(1));
                answers.setItems(items);
                answers.setItemStart(startPost);
                answers.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source ", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return answers;
    }

    @Override
    public void addNewRate(int postId, int mark, int userId) throws DAOException {
        PreparedStatement st1 = null;
        PreparedStatement st2 = null;
        try {
            connection.setAutoCommit(false);
            st1 = connection.prepareStatement(SQL_DELETE_RATE);
            st1.setInt(1, userId);
            st1.setInt(2, postId);
            st1.executeUpdate();

            st2 = connection.prepareStatement(SQL_INSERT_NEW_RATE);
            st2.setInt(1, userId);
            st2.setInt(2, postId);
            st2.setInt(3, mark);
            st2.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st1);
            connection.closeStatement(st2);
        }
    }

    @Override
    public void addCorrectedAnswer(int answerId, String description) throws DAOException {
        PreparedStatement st1 = null;
        try {
            st1 = connection.prepareStatement(SQL_UPDATE_ANSWER_DESCRIPTION);
            st1.setInt(2, answerId);
            st1.setString(1, description);
            st1.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Error,check source", e);
        } finally {
            connection.closeStatement(st1);
        }
    }

    @Override
    public void addCorrectedQuestion(int questionId, int catId, String correctedTitle, String description) throws DAOException {
        PreparedStatement st1 = null;
        PreparedStatement st2 = null;
        try {
            connection.setAutoCommit(false);
            st1 = connection.prepareStatement(SQL_UPDATE_QUESTION);
            st1.setInt(1, catId);
            st1.setString(2, correctedTitle);
            st1.setString(3, description);
            st1.setInt(4, questionId);
            st1.executeUpdate();

            st2 = connection.prepareStatement(SQL_UPDATE_ANSWERS_CATEGORY);
            st2.setInt(1, catId);
            st2.setInt(2, questionId);
            st2.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.log(Level.ERROR, "Exception while connection rollback, " + e1);
            }
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st1);
            connection.closeStatement(st2);
        }
    }

    @Override
    public Post takePostById(int postId) throws DAOException {
        Post post = null;
        PreparedStatement st = null;
        ResultSet rs;
        try {
            st = connection.prepareStatement(SQL_SELECT_POST);
            st.setInt(1, postId);
            rs = st.executeQuery();
            if (rs.next()) {
                post = new Post();
                post.setId(rs.getInt(POST_ID));
                post.setType(Post.PostType.fromValue(rs.getString(POST_TYPE)));
                post.setTitle(rs.getString(POST_TITLE));
                post.setContent(rs.getString(POST_CONTENT));
                post.setStatus(Post.Status.fromValue(rs.getString(POST_STATUS)));
                post.setPublishedTime(rs.getTimestamp(POST_PUBLISHED_TIME));
                post.setModifiedTime(rs.getTimestamp(POST_MODIFIED_TIME));
                post.setParentId(rs.getInt(PARENT_ID));
                post.setParentTitle(rs.getString(PARENT_TITLE));
                CategoryInfo catInfo = new CategoryInfo();
                catInfo.setId(rs.getInt(CATEGORY_ID));
                catInfo.setTitleEn(rs.getString(TITLE_EN));
                catInfo.setTitleRu(rs.getString(TITLE_RU));
                catInfo.setUserId(rs.getInt(MODERATOR_ID));
                post.setCategoryInfo(catInfo);
                ShortUser author = new ShortUser();
                author.setId(rs.getInt(USER_ID));
                author.setRole(Role.fromValue(rs.getString(ROLE)));
                author.setAvatar(rs.getString(AVATAR));
                author.setLogin(rs.getString(LOGIN));
                post.setUser(author);
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
        }
        return post;
    }

    @Override
    public void addNewComplaint(int userId, int complaintPostId, String description) throws DAOException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(SQL_ADD_NEW_COMPLAINT);
            st.setInt(1, complaintPostId);
            st.setInt(2, userId);
            st.setString(3, description);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAODuplicatedInfoException("Complaint (userId=" + userId + ", postId=" + complaintPostId + "  has already exist.", e);
        } finally {
            connection.closeStatement(st);
        }

    }

    @Override
    public PaginatedList<Post> searchPostsByKeyWords(int userId, String content, int startPost, int postsPerPage) throws DAOException {
        PaginatedList<Post> posts = new PaginatedList<>();
        List<Post> items;
        PreparedStatement st = null;
        Statement st1 = null;
        ResultSet rs;
        ResultSet rs1;
        try {
            st = connection.prepareStatement(SQL_SELECT_POSTS_BY_KEY_WORDS);
            st.setInt(1, userId);
            st.setString(2, content);
            st.setInt(3, startPost);
            st.setInt(4, postsPerPage);
            rs = st.executeQuery();
            items = fillPostsList(rs);

            st1 = connection.getStatement();
            rs1 = st1.executeQuery(SQL_SELECT_FOUND_ROWS);
            if(rs1.next()){
                posts.setTotalCount(rs1.getInt(1));
                posts.setItems(items);
                posts.setItemStart(startPost);
                posts.setItemsPerPage(postsPerPage);
            } else {
                throw new DAOException("SQL Error, check source, found_rows() function doesn't work");
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeStatement(st);
            connection.closeStatement(st1);
        }
        return posts;





    }

    @Override
    public void addNewQuestion(int userId, String category, String title, String description) throws DAOException {
        PreparedStatement st = null;

        try {
            st = connection.prepareStatement(SQL_ADD_NEW_QUESTION);
            st.setInt(1, userId);
            st.setInt(2, Integer.parseInt(category));
            st.setString(3, QUESTION_TYPE);
            st.setString(4, title);
            st.setString(5, description);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQL Exception while adding new question ", e);
        } finally {
            connection.closeStatement(st);
        }
    }

    private List<Post> fillPostsList(ResultSet rs) throws SQLException {
        List<Post> posts;
        if (!rs.next()) {
            posts = null;
        } else {
            posts = new ArrayList<>();
            rs.beforeFirst();
            Post post;
            while (rs.next()) {
                post = new Post();
                post.setId(rs.getInt(POST_ID));
                post.setType(Post.PostType.fromValue(rs.getString(POST_TYPE)));
                post.setTitle(rs.getString(POST_TITLE));
                post.setContent(rs.getString(POST_CONTENT));
                post.setStatus(Post.Status.fromValue(rs.getString(POST_STATUS)));
                post.setPublishedTime(rs.getTimestamp(POST_PUBLISHED_TIME));
                post.setModifiedTime(rs.getTimestamp(POST_MODIFIED_TIME));
                post.setAverageMark(rs.getDouble(MARK));
                post.setCurrentUserMark(rs.getInt(CURRENT_USER_MARK));
                post.setParentId(rs.getInt(PARENT_ID));
                post.setParentTitle(rs.getString(PARENT_TITLE));
                CategoryInfo catInfo = new CategoryInfo();
                catInfo.setId(rs.getInt(CATEGORY_ID));
                catInfo.setTitleEn(rs.getString(TITLE_EN));
                catInfo.setTitleRu(rs.getString(TITLE_RU));
                catInfo.setUserId(rs.getInt(MODERATOR_ID));
                post.setCategoryInfo(catInfo);
                ShortUser author = new ShortUser();
                author.setId(rs.getInt(USER_ID));
                author.setRole(Role.fromValue(rs.getString(ROLE)));
                author.setAvatar(rs.getString(AVATAR));
                author.setLogin(rs.getString(LOGIN));
                post.setUser(author);
                posts.add(post);
            }
        }
        return posts;
    }

    private List<Post> fillQuestionList(ResultSet rs) throws SQLException {
        List<Post> questions;
        if (!rs.next()) {
            questions = null;
        } else {
            questions = new ArrayList<>();
            rs.beforeFirst();
            Post question;
            while (rs.next()) {
                question = new Post();
                question.setId(rs.getInt(POST_ID));
                question.setType(Post.PostType.fromValue(rs.getString(POST_TYPE)));
                question.setTitle(rs.getString(POST_TITLE));
                question.setContent(rs.getString(POST_CONTENT));
                question.setStatus(Post.Status.fromValue(rs.getString(POST_STATUS)));
                question.setPublishedTime(rs.getTimestamp(POST_PUBLISHED_TIME));
                question.setModifiedTime(rs.getTimestamp(POST_MODIFIED_TIME));
                question.setAverageMark(rs.getDouble(MARK));
                question.setCurrentUserMark(rs.getInt(CURRENT_USER_MARK));
                CategoryInfo catInfo = new CategoryInfo();
                catInfo.setId(rs.getInt(CATEGORY_ID));
                catInfo.setTitleEn(rs.getString(TITLE_EN));
                catInfo.setTitleRu(rs.getString(TITLE_RU));
                catInfo.setUserId(rs.getInt(MODERATOR_ID));
                question.setCategoryInfo(catInfo);
                ShortUser author = new ShortUser();
                author.setId(rs.getInt(USER_ID));
                author.setRole(Role.fromValue(rs.getString(ROLE)));
                author.setAvatar(rs.getString(AVATAR));
                author.setLogin(rs.getString(LOGIN));
                question.setUser(author);
                questions.add(question);
            }
        }
        return questions;
    }


    private List<Category> createCategoriesFromResultSet(ResultSet rs) throws SQLException {
        List<Category> categories;
        if (!rs.next()) {
            categories = null;
        } else {
            rs.beforeFirst();
            categories = new ArrayList<>();
            Category category;
            while (rs.next()) {
                category = new Category();
                category.setId(rs.getInt(CATEGORY_ID));
                category.setCreationDate(rs.getDate(CAT_CREATION_DATE));
                category.setTitleEn(rs.getString(TITLE_EN));
                category.setTitleRu(rs.getString(TITLE_RU));
                category.setDescriptionEn(rs.getString(DESCRIPTION_EN));
                category.setDescriptionRu(rs.getString(DESCRIPTION_RU));
                category.setStatus(Category.CategoryStatus.fromValue(rs.getString(CAT_STATUS)));
                category.setUserId(rs.getInt(USER_ID));
                category.setQuestionQuantity(rs.getInt(QUANTITY));
                category.setImageLink(rs.getString(CAT_IMAGE));
                ShortUser moderator = new ShortUser();
                moderator.setId(rs.getInt(USER_ID));
                moderator.setRole(Role.fromValue(rs.getString(ROLE)));
                moderator.setAvatar(rs.getString(AVATAR));
                moderator.setLogin(rs.getString(LOGIN));
                category.setModerator(moderator);
                categories.add(category);
            }
        }
        return categories;
    }
}