package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
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
 * Command has method that redirects to following users page if user authorised,
 * and to authorisation page otherwise
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoFriendsCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoFriendsCommand.class);

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

    /**
     * Keys of command and page that are located in config.properties file
     */
    private static final String USER_FRIENDS_PATH = "path.page.friends";

    /**
     * Process redirecting to friends.jsp for authorised users,
     * and redirects to authorisation page otherwise.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to following users page if success scenario or error or authorization page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        try {
            UserService userService = ServiceFactory.getInstance().getUserService();
            int startUser = START_ITEM_NO;
            String pageNoAttr = configurationManager.getProperty(PAGE_NO);
            if (request.getParameter(pageNoAttr) != null) {
                int pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                if (pageNo < FIRST_PAGE_NO) {
                    pageNo = FIRST_PAGE_NO;
                }
                startUser = (pageNo - FIRST_PAGE_NO) * USERS_PER_PAGE;
            }
            PaginatedList<Friend> friends = userService.findFriends(user.getId(), startUser, USERS_PER_PAGE);
            request.setAttribute(FRIENDS, friends);
            String friendsPath = configurationManager.getProperty(USER_FRIENDS_PATH);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + friendsPath;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }

        return page;
    }
}
