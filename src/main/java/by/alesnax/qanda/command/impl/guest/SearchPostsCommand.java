package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Class contains method that process finding questions or answers that related to key search words and returns it as attribute set into request.
 * Returns previous query.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class SearchPostsCommand implements Command {
    private static Logger logger = LogManager.getLogger(SearchPostsCommand.class);

    /**
     * User attribute taking from session
     */
    private static final String USER = "user";

    /**
     * Keys of error messages, page number and other attributes that are located in config.properties file
     */
    private static final String POSTS_ATTR = "attr.request.questions";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String CONTENT = "attr.content";
    private static final String PAGE_NO = "attr.page_no";
    private static final String SEARCH_QUERY = "attr.back_search_query";

    /**
     * Key of error message in loc.properties file
     */
    private static final String EMPTY_SEARCH_QUERY = "search.error.empty_search_query";

    /**
     * Keys of returned page that is located in config.properties file
     */
    private static final String SEARCH_RESULT_PATH = "path.page.search_result";

    /**
     * method processes finding questions and answers that match to user key wirds and returns it as attribute set into request.
     * Method calls method from service layer, which returns list of posts which is set as attribute into request
     * and can throw ServiceException after which method returns value of error500 page.
     * Returns value of best_answers page or error500 page if exception will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of search post page or error page
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        String contentAttr = configurationManager.getProperty(CONTENT);
        String content = request.getParameter(contentAttr);

        User user = (User) session.getAttribute(USER);
        int userId = DEFAULT_USER_ID;
        if (user != null) {
            userId = user.getId();
        }
        if (content == null || content.isEmpty()) {
            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            session.setAttribute(wrongCommandMessageAttr, EMPTY_SEARCH_QUERY);
            String searchResultPath = configurationManager.getProperty(SEARCH_RESULT_PATH);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + searchResultPath;
        } else {
            try {
                PostService postService = ServiceFactory.getInstance().getPostService();
                int startPost = START_ITEM_NO;
                String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                if (request.getParameter(pageNoAttr) != null) {
                    int pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                    if (pageNo < FIRST_PAGE_NO) {
                        pageNo = FIRST_PAGE_NO;
                    }
                    startPost = (pageNo - FIRST_PAGE_NO) * POSTS_PER_PAGE;
                }
                PaginatedList<Post> posts = postService.searchPosts(userId, content, startPost, POSTS_PER_PAGE);

                String postsAttr = configurationManager.getProperty(POSTS_ATTR);
                String backSearchQuery = configurationManager.getProperty(SEARCH_QUERY);
                String searchResultPath = configurationManager.getProperty(SEARCH_RESULT_PATH);
                request.setAttribute(postsAttr, posts);
                request.setAttribute(backSearchQuery, content);
                page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + searchResultPath;
            } catch (ServiceException | NumberFormatException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getClass() + ": " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }

        return page;
    }
}