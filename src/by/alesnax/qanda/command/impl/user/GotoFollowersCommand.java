package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Command has method that redirects to followers page if user authorised,
 * and to authorisation page otherwise
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoFollowersCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoFollowersCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String FRIENDS = "friends";

    /**
     * Keys of attributes in config.properties file, used for pagination, error messages
     */
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";

    /**
     * Key of error message in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";

    /**
     * Keys of command and page that are located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String USER_FOLLOWERS_PATH = "path.page.followers";


    /**
     *  Process redirecting to followers.jsp for authorised users,
     *  and redirects to authorisation page otherwise.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to followers page if success scenario or error or authorization page otherwise)
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
            try {
                UserService userService = ServiceFactory.getInstance().getUserService();
                int pageNo = FIRST_PAGE_NO;
                int startUser = START_ITEM_NO;
                String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                if (request.getParameter(pageNoAttr) != null) {
                    pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                    if (pageNo < FIRST_PAGE_NO) {
                        pageNo = FIRST_PAGE_NO;
                    }
                    startUser = (pageNo - FIRST_PAGE_NO) * USERS_PER_PAGE;
                }
                PaginatedList<Friend> friends = userService.findFollowers(user.getId(), startUser, USERS_PER_PAGE);
                request.setAttribute(FRIENDS, friends);
                String friendsPath = configurationManager.getProperty(USER_FOLLOWERS_PATH);
                page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + friendsPath;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
