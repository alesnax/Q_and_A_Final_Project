package by.alesnax.qanda.dao.impl;

import by.alesnax.qanda.dao.AbstractDAO;
import by.alesnax.qanda.dao.PostDAO;
import by.alesnax.qanda.dao.pool.WrappedConnection;
import by.alesnax.qanda.entity.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alesnax on 05.12.2016.
 */
public class PostDAOImpl extends AbstractDAO<Integer, Post> implements PostDAO {

    private static final String SQL_SELECT_ALL_CATEGORIES = "SELECT categories.id AS category_id, categories.users_id as users_id, title_en, title_ru, creation_date, description_ru, description_en, categories.status AS status, image, " +
            "login, avatar, role, count(posts.id) AS quantity " +
            "FROM categories   JOIN posts ON posts.category_id = categories.id JOIN users ON users.id = categories.users_id " +
            "WHERE posts.type != 'answer' AND categories.status != 'closed' group by categories.id";    // +++

    private static final String SQL_SELECT_CATEGORIES_INFO = "SELECT categories.id AS category_id, title_en, title_ru FROM categories"; // ++++

    private static final String SQL_ADD_NEW_QUESTION = "INSERT INTO `likeit_db`.`posts` " +
            "(`users_id`, `category_id`, `type`, `title`, `content`) VALUES (?,?,?,?,?);";  //+++
//MY PROFILE
    private static final String SQL_SELECT_USERS_QUESTIONS = "SELECT  posts.id, posts.users_id, posts.category_id, posts.type,   posts.title, posts.content, posts.status,\n" +
            "posts.published_time, posts.modified_time,  AVG(rates.value) AS mark, COUNT(rates.value) as mark_count\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id LEFT JOIN rates ON posts.id = rates.posts_id\n" +
            "WHERE posts.type = 'question' AND posts.status != 'deleted'  AND users.id = ?\n" +
            "GROUP BY posts.id ORDER BY published_time DESC";                                //---

    private static final String SQL_SELECT_USERS_ANSWERS = "SELECT  posts.id, posts.users_id, posts.category_id, posts.type, posts.content, posts.status,\n" +
            "posts.published_time, posts.modified_time,  AVG(rates.value) AS mark, COUNT(rates.value) as mark_count\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id LEFT JOIN rates ON posts.id = rates.posts_id\n" +
            "WHERE posts.type = 'answer' AND posts.status != 'deleted'  AND users.id = ?\n" +
            "GROUP BY posts.id ORDER BY published_time DESC";

// I LIKED IT
    private static final String SQL_SELECT_LIKED_QUESTIONS = "SELECT  questions.id, questions.users_id, questions.category_id, questions.type, questions.title, questions.content, questions.published_time, questions.status, questions.modified_time, \n" +
        "AVG(rates.value) AS mark, COUNT(rates.value) as mark_amount, users.login, users.avatar\n" +
        "FROM posts AS questions JOIN users  ON users.id = questions.users_id JOIN rates ON questions.id = rates.posts_id\n" +
        "WHERE questions.type = 'question' AND questions.status != 'deleted' AND questions.id IN (SELECT posts_id FROM rates WHERE users_id = 1) \n" +
        "GROUP BY questions.id ORDER BY questions.published_time DESC";


    private static final String SQL_SELECT_LIKED_ANSWERS = "SELECT  answers.id, answers.users_id, answers.category_id, answers.type, answers.content, answers.published_time, answers.status, answers.modified_time,  AVG(rates.value) AS mark, \n" +
            "COUNT(rates.value) as mark_amount, users.login, users.avatar, answers.parent_id, questions.title AS q_title\n" +
            "FROM posts AS answers JOIN users  ON users.id = answers.users_id JOIN posts AS questions ON answers.parent_id = questions.id JOIN rates ON answers.id = rates.posts_id\n" +
            "WHERE answers.type = 'answer' AND answers.status != 'deleted' AND answers.id IN (SELECT posts_id FROM rates WHERE users_id = ?) GROUP BY answers.id ORDER BY answers.published_time DESC";



// MY NEWS
    private static final String SQL_SELECT_ALL_FRIENDS_QUESTIONS = "SELECT posts.id, posts.users_id, posts.category_id, posts.type,   posts.title, posts.status,\n" +
        " posts.published_time, posts.modified_time, posts.content, AVG(rates.value) AS mark, COUNT(rates.value) as mark_amount, users.login, users.avatar\n" +
        "            FROM posts JOIN users  ON users.id = posts.users_id JOIN rates ON posts.id = rates.posts_id \n" +
        "            WHERE posts.type = 'question' AND posts.status != 'deleted' AND posts.users_id IN \n" +
        "            (SELECT users_friend_id FROM friends WHERE friends.users_id = ? AND friends.state = 'friend' )\n" +
        "            GROUP BY posts.id ORDER BY mark DESC, posts.published_time DESC";

    private static final String SQL_SELECT_ALL_FRIENDS_ANSWERS = "SELECT  answers.id, answers.users_id, answers.category_id, answers.type, answers.content,\n" +
            "answers.published_time, answers.status, answers.modified_time,  AVG(rates.value) AS mark, COUNT(rates.value) as mark_amount, users.login, users.avatar, answers.parent_id, questions.title AS q_title\n" +
            "FROM posts AS answers JOIN users  ON users.id = answers.users_id JOIN posts AS questions ON answers.parent_id = questions.id JOIN rates ON answers.id = rates.posts_id\n" +
            "WHERE answers.type = 'answer' AND answers.status != 'deleted' AND answers.users_id IN \n" +
            "(SELECT users_friend_id FROM friends WHERE friends.users_id = ? AND friends.state = 'friend' )\n" +
            "GROUP BY answers.id ORDER BY answers.published_time DESC";



// BEST QUESTIONS
    private static final String SQL_SELECT_BEST_QUESTIONS_Q = "SELECT users.login, users.avatar, posts.id, posts.users_id, posts.category_id, posts.type,   posts.title, posts.status,\n" +
            "  posts.published_time, posts.modified_time, posts.content, AVG(rates.value) AS mark, COUNT(rates.value) as mark_amount\n" +
            "  FROM posts JOIN users  ON users.id = posts.users_id JOIN rates ON posts.id = rates.posts_id \n" +
            "  WHERE posts.type = 'question' AND posts.status != 'deleted'  \n" +
            "  GROUP BY posts.id ORDER BY mark DESC, posts.published_time DESC LIMIT ?, ?;";         // +++++

    private static final String SQL_SELECT_BEST_QUESTIONS_A = "SELECT  answers.id, answers.users_id, answers.category_id, answers.type, answers.title, answers.content,   \n" +
            "answers.published_time, answers.status, answers.modified_time,  AVG(rates.value) AS mark, COUNT(rates.value) as mark_amount,  users.login, \n" +
            "users.avatar,  answers.parent_id, questions.category_id, questions.title AS q_title  \n" +
            "FROM posts AS answers JOIN users  ON users.id = answers.users_id   \n" +
            "JOIN posts AS questions ON answers.parent_id = questions.id  JOIN rates ON answers.id = rates.posts_id   \n" +
            "WHERE answers.type = 'answer' AND answers.status != 'deleted' AND answers.parent_id IN\n" +
            "(SELECT questions.id FROM posts AS questions JOIN users  ON users.id = questions.users_id JOIN rates ON questions.id = rates.posts_id \n" +
            "WHERE questions.type = 'question' AND questions.status != 'deleted' GROUP BY rates.posts_id)\n" +
            "GROUP BY answers.id ORDER BY mark DESC, answers.published_time DESC";           //+++
// BEST ANSWERS
    private static final String SQL_SELECT_BEST_ANSWERS_A_Q = "SELECT  answers.id, answers.users_id, answers.category_id, answers.type, answers.content,\n" +
            "answers.published_time, answers.status, answers.modified_time,  AVG(rates.value) AS mark, COUNT(rates.value) as mark_amount, users.login, users.avatar,\n" +
            " answers.parent_id, questions.title AS q_title\n" +
            " FROM posts AS answers JOIN users  ON users.id = answers.users_id \n" +
            " JOIN posts AS questions ON answers.parent_id = questions.id\n" +
            " JOIN rates ON answers.id = rates.posts_id\n" +
            " WHERE answers.type = 'answer' AND answers.status != 'deleted'\n" +
            " GROUP BY answers.id ORDER BY mark DESC, answers.published_time DESC;";                      // ++++++++

    private static final String SQL_SELECT_FRIENDS = "SELECT friends.users_friend_id AS friend_id, login, surname, name, email, birthday, sex, registration_date, \n" +
            "role, users.state, avatar, country, city, users.status AS u_status, friends.status as friend_group\n" +
            "FROM friends JOIN users ON friends.users_friend_id = users.id\n" +
            "WHERE friends.users_id =  AND friends.state = 'friend' ORDER BY surname, name ";


    private static final String SQL_SELECT_QUESTIONS_BY_CATEGORY = "SELECT  posts.id, posts.users_id, posts.category_id, posts.type,   posts.title, posts.content, posts.status, cast(posts.published_time AS datetime) AS published_time, \n" +
            "cast(posts.modified_time AS datetime) AS modified_time,  AVG(rates.value) AS mark, COUNT(rates.value) as mark_count, categories.title_en, categories.title_ru, users.login, users.avatar, users.role\n" +
            "FROM posts JOIN users  ON users.id = posts.users_id JOIN categories ON categories.id = posts.category_id LEFT JOIN rates ON posts.id = rates.posts_id\n" +
            "WHERE posts.type != 'answer' AND posts.status != 'deleted' AND posts.category_id=? GROUP BY posts.id ORDER BY published_time DESC";



    private static final String CATEGORY_ID = "category_id";
    private static final String CAT_CREATION_DATE = "creation_date";
    private static final String TITLE_EN = "title_en";
    private static final String TITLE_RU = "title_ru";
    private static final String QUESTION_TYPE = "question";
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
    private static final String MARK = "mark";
    private static final String MARK_COUNT = "mark_count";


    public PostDAOImpl(WrappedConnection connection) {
        super(connection);
    }

    @Override
    public Post findEntityById(Integer id) {
        return null;
    }


    @Override
    public List<Post> findQuestionsByCategory(String categoryId) throws DAOException {
        List<Post> questions = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(SQL_SELECT_QUESTIONS_BY_CATEGORY);
            st.setInt(1, Integer.parseInt(categoryId));

            rs = st.executeQuery();
            if (!rs.next()) {
                questions = null;
            } else {
                rs.beforeFirst();
                questions = new ArrayList<>();
                Post question = null;
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
                    question.setMarkCount(rs.getInt(MARK_COUNT));
                    CategoryInfo catInfo = new CategoryInfo();
                    catInfo.setId(rs.getInt(CATEGORY_ID));
                    catInfo.setTitleEn(rs.getString(TITLE_EN));
                    catInfo.setTitleRu(rs.getString(TITLE_RU));
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
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeResultSet(rs);
            connection.closeStatement(st);
        }
        return questions;


    }

    @Override
    public List<Post> findQuestionsByUserId(int userId) throws DAOException {
        return null;
    }

    @Override
    public List<Post> findMyPosts(int userId, String lowLimit, String highLimit) throws DAOException {
        List<Post> myPosts = null;

       /* PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(SQL_SELECT_USERS_POSTS);

            rs = st.executeQuery(SQL_SELECT_ALL_CATEGORIES);

            if (!rs.next()) {
                categories = null;
            } else {
                rs.beforeFirst();
                categories = new ArrayList<>();
                Category category = null;
                while (rs.next()) {
                    category = new Category();

                    category.setId(rs.getInt(CATEGORY_ID));
                    category.setCreationDate(rs.getDate(CAT_CREATION_DATE));
                    category.setTitleEn(rs.getString(TITLE_EN));
                    category.setTitleRu(rs.getString(TITLE_RU));
                    category.setDescriptionEn(rs.getString(DESCRIPTION_EN));
                    category.setDescriptionRu(rs.getString(DESCRIPTION_RU));
                    category.setStatus(Category.CategoryStatus.fromValue(rs.getString(CAT_STATUS)));
                    category.setUserId(rs.getInt(USER_ID));// спросить про дублирование ID

                    User moderator = new User();
                    moderator.setId(rs.getInt(USER_ID));
                    moderator.setRole(Role.fromValue(rs.getString(ROLE)));
                    moderator.setAvatar(rs.getString(AVATAR));
                    moderator.setLogin(rs.getString(LOGIN));
                    moderator.setName(rs.getString(NAME));
                    moderator.setSurname(rs.getString(SURNAME));
                    category.setModerator(moderator);

                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeResultSet(rs);
            connection.closeStatement(st);
        }
        return categories;



*//*        PreparedStatement st = null;

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

        *//*


*/


        return null;
    }


    @Override
    public List<Category> takeAllCategories() throws DAOException {
        List<Category> categories = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(SQL_SELECT_ALL_CATEGORIES);
            rs = st.executeQuery(SQL_SELECT_ALL_CATEGORIES);

            if (!rs.next()) {
                categories = null;
            } else {
                rs.beforeFirst();
                categories = new ArrayList<>();
                Category category = null;
                while (rs.next()) {
                    category = new Category();
                    category.setId(rs.getInt(CATEGORY_ID));
                    category.setCreationDate(rs.getDate(CAT_CREATION_DATE));
                    category.setTitleEn(rs.getString(TITLE_EN));
                    category.setTitleRu(rs.getString(TITLE_RU));
                    category.setDescriptionEn(rs.getString(DESCRIPTION_EN));
                    category.setDescriptionRu(rs.getString(DESCRIPTION_RU));
                    category.setStatus(Category.CategoryStatus.fromValue(rs.getString(CAT_STATUS)));
                    category.setUserId(rs.getInt(USER_ID));// спросить про дублирование ID
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
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeResultSet(rs);
            connection.closeStatement(st);
        }
        return categories;
    }

    @Override
    public List<CategoryInfo> takeCategoriesInfo() throws DAOException {
        List<CategoryInfo> categoriesInfo = null;

        Statement st = null;
        ResultSet rs = null;
        try {
            st = connection.getStatement();
            rs = st.executeQuery(SQL_SELECT_CATEGORIES_INFO);

            if (!rs.next()) {
                categoriesInfo = null;
            } else {
                rs.beforeFirst();
                categoriesInfo = new ArrayList<>();
                CategoryInfo category = null;
                while (rs.next()) {
                    category = new CategoryInfo();
                    category.setId(rs.getInt(CATEGORY_ID));
                    category.setTitleEn(rs.getString(TITLE_EN));
                    category.setTitleRu(rs.getString(TITLE_RU));
                    categoriesInfo.add(category);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("SQL Error, check source", e);
        } finally {
            connection.closeResultSet(rs);
            connection.closeStatement(st);
        }
        return categoriesInfo;
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




}
