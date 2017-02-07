package by.alesnax.qanda.command.impl.admin;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.AdminService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.*;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Command redirects user to admins_and_moderators.jsp, access for command is only for users with ADMIN role,
 * otherwise user will be redirected to start page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoAdminsAndModeratorsCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoAdminsAndModeratorsCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";
    private static final String ADMINS_AND_MODERATORS_ATTR = "admins_moders";

    /**
     * Key of page_no attribute in config.properties file, used for pagination
     */
    private static final String PAGE_NO = "attr.page_no";

    /**
     * Keys of error attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";

    /**
     * Keys of commands or pages that are located in config.properties file
     */
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String USER_MANAGEMENT_PATH = "path.page.admins_and_moderators";

    /**
     * Process redirecting to admins_and_moderators.jsp, method checks if attribute user exists in session,
     * and it's role is ADMIN or MODERATOR, calls method from service for taking data about admins and moderators,
     * and put it into request as attribute,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to admins management page if success scenario or error or authorization page or profile page otherwise)
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
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case ADMIN_ROLE:
                    try {
                        AdminService adminService = ServiceFactory.getInstance().getAdminService();
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
                        PaginatedList<Friend> adminsAndModers = adminService.findManagingUsers(startUser, USERS_PER_PAGE);
                        request.setAttribute(ADMINS_AND_MODERATORS_ATTR, adminsAndModers);
                        String managementPath = configurationManager.getProperty(USER_MANAGEMENT_PATH);
                        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + managementPath;
                    } catch (ServiceException e) {
                        logger.log(Level.ERROR, e);
                        String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                        request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
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
