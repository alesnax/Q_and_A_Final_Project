package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceDuplicatedInfoException;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Class has method that creates new note about relationship between users. Access for authorised users.
 * Returns to previous page or to error page if exception will be caught.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class FollowUserCommand implements Command {
    private static Logger logger = LogManager.getLogger(RemoveFollowingUserCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER_ATTR = "user";
    private static final String USER_ID = "user_id";

    /**
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String USER_ALREADY_FOLLOWER = "profile.error.message.user_already_follower";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    /**
     * creates note that user follow another user, calls process method from service layer,
     * if success scenario - returns to previous page with success message,
     * otherwise returns with error messages to previous page or error page.
     * Acess to operation is only for authorised users.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER_ATTR);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            UserService userService = ServiceFactory.getInstance().getUserService();
            String requestUserId = request.getParameter(USER_ID);
            try {
                int followingUserId = Integer.parseInt(requestUserId);
                userService.addFollower(followingUserId, user.getId());
                String nextCommand = QueryUtil.getPreviousQuery(request);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
            } catch (NumberFormatException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
                request.setAttribute(errorMessageAttr, e.getMessage());
                page = ERROR_REQUEST_TYPE;
            } catch (ServiceDuplicatedInfoException e) {
                logger.log(Level.WARN, e);
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                session.setAttribute(wrongCommandMessageAttr, USER_ALREADY_FOLLOWER);
                String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);// try-catch
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
