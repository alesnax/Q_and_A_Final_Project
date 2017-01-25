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

import static by.alesnax.qanda.constant.CommandConstants.*;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 16.01.2017.
 */
public class ChangeUserRoleCommand implements Command {
    private static Logger logger = LogManager.getLogger(ChangeUserRoleCommand.class);

    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";
    private static final String LOGIN_ATTR = "login";
    private static final String ROLE_ATTR = "role";

    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_MANAGEMENT = "command.go_to_admins_and_moderators";

    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String NO_USER_WITH_SUCH_LOGIN_OR_MODERATOR = "error.error_msg.no_such_login_or_moderator";
    private static final String EMPTY_LOGIN_ROLE = "error.error_msg.empty_login_and_role";
    private static final String EMPTY_LOGIN = "error.error_msg.empty_login";
    private static final String EMPTY_ROLE = "error.error_msg.empty_role";
    private static final String SUCCESS_CHANGE_ROLE_MESSAGE = "change_role.message.change_saved";


    @Override
    public String execute(HttpServletRequest request) {
        String page;

        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case ADMIN_ROLE:
                    try {
                        String login = request.getParameter(LOGIN_ATTR);
                        String changedRole = request.getParameter(ROLE_ATTR);
                        if ((login != null && !login.isEmpty()) && (changedRole != null && !changedRole.isEmpty())) {
                            AdminService adminService = ServiceFactory.getInstance().getAdminService();
                            boolean changed = adminService.changeUserRole(login, changedRole);
                            if(changed){
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
                        request.setAttribute(errorMessageAttr, e.getMessage());
                        page = ERROR_REQUEST_TYPE;
                    }
                    break;
                case USER_ROLE:
                case MODERATOR_ROLE:
                default:
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, UNDEFINED_COMMAND_MESSAGE);
                    String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                    break;
            }
        }
        return page;
    }
}
