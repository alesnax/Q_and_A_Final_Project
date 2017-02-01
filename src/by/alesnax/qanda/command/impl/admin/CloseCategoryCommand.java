package by.alesnax.qanda.command.impl.admin;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// static import
import static by.alesnax.qanda.constant.CommandConstants.ERROR_REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Changes category status to close. Access for command is only for users with ADMIN role,
 * otherwise user will be redirected to start page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */

public class CloseCategoryCommand implements Command {
    private static Logger logger = LogManager.getLogger(CloseCategoryCommand.class);
    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER_ATTR = "user";
    private static final String CAT_ID = "category_id";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_MAKE_OPERATION = "warn.login_before_make_operation";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_MODERATED_CATEGORIES_COMMAND = "command.go_to_moderated_categories";

    /**
     * Keys of error messages attributes that are located in config.properties file
     */
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";

    /**
     * Process changing category status to 'closed'. Method checks if attribute user exists in session,
     * and it's role is ADMIN, calls method from service for processing changes,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to moderated categories page if success scenario or authorization page or profile or error page otherwise)
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
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_MAKE_OPERATION);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            try {
                String role = user.getRole().getValue();
                int userId = user.getId();
                AdminService adminService = ServiceFactory.getInstance().getAdminService();
                int catId = Integer.parseInt(request.getParameter(CAT_ID));
                String nextCommand;
                switch (role) {
                    case ADMIN_ROLE:
                        adminService.closeCategory(catId);
                        nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES_COMMAND);
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        break;
                    case MODERATOR_ROLE:
                    case USER_ROLE:
                    default:
                        String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                        session.setAttribute(wrongCommandMessageAttr, UNDEFINED_COMMAND_MESSAGE);
                        nextCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                        page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
                        break;
                }
            } catch (NumberFormatException | ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        }
        return page;
    }
}
