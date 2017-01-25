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
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 11.01.2017.
 */
public class GotoFollowersCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoFriendsCommand.class);

    private static final String USER = "user";
    private static final String FRIENDS = "friends";
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet"; //роверить
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String USER_FOLLOWERS_PATH = "path.page.followers";



    @SuppressWarnings("Duplicates")
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
                int pageNo = 1;
                int startUser = 0;
                String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                if (request.getParameter(pageNoAttr) != null) {
                    pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                    if (pageNo < 1) {
                        pageNo = 1;
                    }
                    startUser = (pageNo - 1) * USERS_PER_PAGE;
                }
                PaginatedList<Friend> friends = userService.findFollowers(user.getId(), startUser, USERS_PER_PAGE);
                request.setAttribute(FRIENDS, friends);
                String friendsPath = configurationManager.getProperty(USER_FOLLOWERS_PATH);
                page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + friendsPath;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
