package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.impl.guest.GotoProfileCommand;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.Post;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.PostService;
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
 * Created by alesnax on 13.12.2016.
 */
public class GotoFriendsCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoFriendsCommand.class);

    private static final String USER = "user";
    private static final String FRIENDS = "friends";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet"; //роверить
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String USER_FRIENDS_PATH= "path.page.friends";



    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = ConfigurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            String nextCommand = ConfigurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            UserService userService = ServiceFactory.getInstance().getUserService();
            try {
                List<Friend> friends = userService.findFriends(user.getId());
                request.setAttribute(FRIENDS, friends);
                String friendsPath = ConfigurationManager.getProperty(USER_FRIENDS_PATH);
                page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + friendsPath;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = ConfigurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }

        return page;
    }
}
