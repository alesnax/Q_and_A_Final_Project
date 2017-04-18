package by.alesnax.qanda.command.impl.admin;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

// static import

/**
 * Changes user role of other users, access for command is only to users with ADMIN role,
 * otherwise user will be redirected to start page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class ChangeUserRoleCommand implements Command {
    private static Logger logger = LogManager.getLogger(ChangeUserRoleCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String LOGIN_ATTR = "login";
    private static final String ROLE_ATTR = "role";

    /**
     * Keys of success or error messages in loc.properties file
     */
    private static final String NO_USER_WITH_SUCH_LOGIN_OR_MODERATOR = "error.error_msg.no_such_login_or_moderator";
    private static final String EMPTY_LOGIN_ROLE = "error.error_msg.empty_login_and_role";
    private static final String EMPTY_LOGIN = "error.error_msg.empty_login";
    private static final String EMPTY_ROLE = "error.error_msg.empty_role";
    private static final String SUCCESS_CHANGE_ROLE_MESSAGE = "change_role.message.change_saved";

    /**
     * Keys to error attributes that are located in config.properties file
     */
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Keys to commands that are located in config.properties file
     */
    private static final String GO_TO_MANAGEMENT = "command.go_to_admins_and_moderators";

    /**
     * Process changing user role, method checks if attribute user exists in session,
     * and it's role is ADMIN, calls method from service for processing changes,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;

        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        try {
            String login = request.getParameter(LOGIN_ATTR);
            String changedRole = request.getParameter(ROLE_ATTR);
            if ((login != null && !login.isEmpty()) && (changedRole != null && !changedRole.isEmpty())) {
                AdminService adminService = ServiceFactory.getInstance().getAdminService();
                boolean changed = adminService.changeUserRole(login, changedRole);
                if (changed) {
                    String successChangeMessageAttr = configurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
                    session.setAttribute(successChangeMessageAttr, SUCCESS_CHANGE_ROLE_MESSAGE);
                } else {
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, NO_USER_WITH_SUCH_LOGIN_OR_MODERATOR);
                }
            } else if ((login == null || login.isEmpty()) & (changedRole == null || changedRole.isEmpty())) {
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                session.setAttribute(wrongCommandMessageAttr, EMPTY_LOGIN_ROLE);
            } else if (login == null || login.isEmpty()) {
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                session.setAttribute(wrongCommandMessageAttr, EMPTY_LOGIN);
            } else if (changedRole == null || changedRole.isEmpty()) {
                String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                session.setAttribute(wrongCommandMessageAttr, EMPTY_ROLE);
            }
            String nextCommand = configurationManager.getProperty(GO_TO_MANAGEMENT);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
