package by.alesnax.qanda.command.impl.user;

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
 * Command has method that takes list of posts that were rated by user from service layer ,
 * put it as an attribute to request and returns value of categories page
 * or error_page if exception will be caught. Access for authorised users
 * otherwise user will be redirected to authorization or error page with error message
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoRepostsCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoRepostsCommand.class);

    /**
     * Name of user attribute from session
     */
    private static final String USER = "user";

    /**
     * Keys of attributes in config.properties file, used for pagination, showing posts list and error messages
     */
    private static final String QUESTIONS_ATTR = "attr.request.questions";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String PAGE_NO = "attr.page_no";

    /**
     * Keys of command and page that are located in config.properties file
     */
    private static final String USER_REPOSTS_PATH = "path.page.reposts";

    /**
     * Process redirecting to reposts.jsp and putting attribute into session which contains posts list,
     * which was took from service layer. Method checks if attribute user exists in session,
     * otherwise redirects to authorization or error page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of reposts page if success scenario or error or authorization page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
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
            PaginatedList<Post> posts = postService.findLikedPosts(user.getId(), startPost, POSTS_PER_PAGE);
            String questionsAttr = configurationManager.getProperty(QUESTIONS_ATTR);
            request.setAttribute(questionsAttr, posts);
            String repostsPath = configurationManager.getProperty(USER_REPOSTS_PATH);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + repostsPath;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
