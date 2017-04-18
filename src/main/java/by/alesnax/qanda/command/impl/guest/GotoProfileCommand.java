package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Class contains method that redirect user to profile page and processes taking list of their posts
 * and user statistics and returns its as attributes set into request.
 * If attribute 'user' doesn't exist in session, method redirects client to authorisation page
 * Returns value of userProfilePath or value of go_to_authorisation command if session user doesn't exist or
 * error500 age value if exception while taking info will be caught.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoProfileCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoProfileCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String USER_ID = "user_id";
    private static final String PAGE_NO = "attr.page_no";

    /**
     * Keys of error attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String POSTS_ATTR = "attr.request.questions";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String USER_ID_NOT_NUMBER = "profile.error.message.user_id_not_number";
    private static final String USER_ID_NOT_EXIST = "profile.error.message.user_id_not_exist";

    /**
     * Keys of returned command or page that are located in config.properties file
     */
    private static final String USER_PROFILE_PATH = "path.page.profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    /**
     * method that redirect user to profile page and processes taking list of their posts
     * and user statistics and returns its as attributes set into request.
     * If attribute 'user' doesn't exist in session, method redirects client to authorisation page
     * Returns value of userProfilePath or value of go_to_authorisation command if session user doesn't exist or
     * error500 age value if exception while taking info will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return  value of userProfilePath or value of go_to_authorisation command if session user doesn't exist or
     * error500 age value if exception while taking info will be caught.
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            PostService postService = ServiceFactory.getInstance().getPostService();
            UserService userService = ServiceFactory.getInstance().getUserService();
            String requestUserId = request.getParameter(USER_ID);
            int profileUserId = user.getId();
            if (requestUserId != null && !requestUserId.isEmpty()) {
                try {
                    profileUserId = Integer.parseInt(requestUserId);
                } catch (NumberFormatException e) {
                    logger.log(Level.ERROR, e);
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, USER_ID_NOT_NUMBER);
                }
            }
            try {
                User showedUser = userService.findUserById(profileUserId, user.getId());
                int pageNo = FIRST_PAGE_NO;
                int startPost = START_ITEM_NO;
                String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                if (request.getParameter(pageNoAttr) != null) {
                    pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                    if (pageNo < FIRST_PAGE_NO) {
                        pageNo = FIRST_PAGE_NO;
                    }
                    startPost = (pageNo - FIRST_PAGE_NO) * POSTS_PER_PAGE;
                }
                if (showedUser != null) {
                    request.setAttribute(USER, showedUser);
                    PaginatedList<Post> posts = postService.findPostsByUserId(profileUserId, user.getId(), startPost, POSTS_PER_PAGE);
                    String postsAttr = configurationManager.getProperty(POSTS_ATTR);
                    request.setAttribute(postsAttr, posts);
                } else {
                    showedUser = userService.findUserById(user.getId(), user.getId());
                    request.setAttribute(USER, showedUser);
                    PaginatedList<Post> posts = postService.findPostsByUserId(user.getId(), user.getId(), startPost, POSTS_PER_PAGE);
                    String postsAttr = configurationManager.getProperty(POSTS_ATTR);
                    request.setAttribute(postsAttr, posts);
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, USER_ID_NOT_EXIST);
                }
                String userProfilePath = configurationManager.getProperty(USER_PROFILE_PATH);
                page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + userProfilePath;
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

