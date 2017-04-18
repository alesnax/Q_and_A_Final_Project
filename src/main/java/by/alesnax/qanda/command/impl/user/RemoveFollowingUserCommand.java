package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
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
 * Command has method that removes user from following users and redirects to following users page if user authorised,
 * and to authorisation page otherwise.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class RemoveFollowingUserCommand implements Command {
    private static Logger logger = LogManager.getLogger(RemoveFollowingUserCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER_ATTR = "user";
    private static final String USER_ID = "user_id";

    /**
     * Keys of error message attributes located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * method  removes user from following users and redirects to following users page if user authorised,
     * or to authorisation page otherwise.
     *
     * @param request Processed HttpServletRequest
     * @return value previous page string if user authorised, to authorisation or error page otherwise
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER_ATTR);
        UserService userService = ServiceFactory.getInstance().getUserService();
        String requestUserId = request.getParameter(USER_ID);
        try {
            int removedUserId = Integer.parseInt(requestUserId);
            userService.removeUserFromFollowing(removedUserId, user.getId());
            String nextCommand = QueryUtil.getPreviousQuery(request);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } catch (NumberFormatException | ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }

        return page;
    }
}
