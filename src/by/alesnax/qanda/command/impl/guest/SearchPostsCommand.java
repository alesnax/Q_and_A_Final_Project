package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 13.12.2016.
 */
public class SearchPostsCommand implements Command {
    private static Logger logger = LogManager.getLogger(SearchPostsCommand.class);

    private static final String USER = "user";
    private static final String POSTS_ATTR = "attr.request.questions";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String CONTENT = "attr.content";
    private static final String PAGE_NO = "attr.page_no";

    private static final String SEARCH_RESULT_PATH = "path.page.search_result";
    private static final String EMPTY_SEARCH_QUERY = "search.error.empty_search_query";
    private static final String SEARCH_QUERY = "attr.back_search_query";


    @SuppressWarnings("Duplicates")
    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        String contentAttr = configurationManager.getProperty(CONTENT);
        String content = request.getParameter(contentAttr);

        User user = (User) session.getAttribute(USER);
        int userId = 0;
        if (user != null) {
            userId = user.getId();
        }
        if (content == null || content.isEmpty()) {
            String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
            session.setAttribute(wrongCommandMessageAttr, EMPTY_SEARCH_QUERY);
        } else {
            try {
                PostService postService = ServiceFactory.getInstance().getPostService();
                int pageNo = 1;
                int startPost = 0;
                String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                if (request.getParameter(pageNoAttr) != null) {
                    pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                    if (pageNo < 1) {
                        pageNo = 1;
                    }
                    startPost = (pageNo - 1) * POSTS_PER_PAGE;
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