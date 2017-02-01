package by.alesnax.qanda.command.impl.moderator;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.Friend;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.pagination.PaginatedList;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ModeratorService;
import by.alesnax.qanda.service.ServiceFactory;
import by.alesnax.qanda.service.impl.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Class contains method that process taking list of all users and returns it as attribute set into request.
 * Returns previous query.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoAllUsersCommand implements Command {
    private static Logger logger = LogManager.getLogger(GotoAllUsersCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";
    private static final String ALL_USERS = "all_users";

    /**
     * Keys of error messages attributes and page_no attribute that are located in config.properties file
     */
    private static final String PAGE_NO = "attr.page_no";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";

    /**
     * Keys of returned commands or page that are located in config.properties file
     */
    private static final String ALL_USERS_PATH = "path.page.all_users";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    /**
     * method processes taking list of all users and returns it as attribute set into request. Access for users with role
     * ADMIN or MODERATOR. Method calls method from service layer, which returns list of posts
     * which is set as attribute into request and can throw ServiceException after which method returns value of error500 page.
     * Returns value of all_users page or error500 page if exception will be caught.
     *
     * @param request Processed HttpServletRequest
     * @return value of all_users page or error page
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
                case MODERATOR_ROLE:
                case ADMIN_ROLE:
                    try {
                        ModeratorService moderatorService = ServiceFactory.getInstance().getModeratorService();
                        int pageNo = FIRST_PAGE_NO;
                        int startUser = START_ITEM_NO;
                        String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                        if (request.getParameter(pageNoAttr) != null) {
                            pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                            if (pageNo < FIRST_PAGE_NO) {
                                pageNo = FIRST_PAGE_NO;
                            }
                            startUser = (pageNo - 1) * USERS_PER_PAGE;
                        }
                        PaginatedList<Friend> allUsers = moderatorService.findAllUsers(startUser, USERS_PER_PAGE);
                        request.setAttribute(ALL_USERS, allUsers);
                        String allUsersPath = configurationManager.getProperty(ALL_USERS_PATH);
                        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + allUsersPath;
                    } catch (ServiceException e) {
                        logger.log(Level.ERROR, e);
                        String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                        request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                        page = ERROR_REQUEST_TYPE;
                    }
                    break;
                case USER_ROLE:
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
