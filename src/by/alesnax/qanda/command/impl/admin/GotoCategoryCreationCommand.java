package by.alesnax.qanda.command.impl.admin;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.FIRST_PAGE_NO;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Command has method that redirects user to admins_and_moderators.jsp and put attribute into session which
 * opens category creation block, access for command is only for users with ADMIN role,
 * otherwise user will be redirected to start page
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoCategoryCreationCommand implements Command {
    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";

    /**
     * Keys of attributes in config.properties file, used for pagination and opening category creation block
     */
    private static final String SHOW_CATEGORY_CREATION_ATTR = "attr.show_category_creation";
    private static final String PAGE_NO_QUERY_PART = "command.page_query_part";
    private static final String PAGE_NO = "attr.page_no";

    /**
     * Keys of error attributes that are located in config.properties file
     */
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";

    /**
     * Keys of error messages in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";
    private static final String UNDEFINED_COMMAND_MESSAGE = "error.error_msg.undefined_command";

    /**
     * Keys of commands that are located in config.properties file
     */
    private static final String GO_TO_MODERATED_CATEGORIES = "command.go_to_moderated_categories";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    /**
     * method redirects to moderated_categories.jsp and puts attribute into session which shows category creation block,
     * method checks if attribute user exists in session, and it's role is ADMIN or MODERATOR,
     * otherwise redirects to authorization or profile page with error message.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to moderated_categories page if success scenario or error or authorization page or profile page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        if (user == null) {
            String notRegUserAttr = configurationManager.getProperty(NOT_REGISTERED_USER_YET_ATTR);
            String nextCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            session.setAttribute(notRegUserAttr, WARN_LOGIN_BEFORE_WATCH_PROFILE);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand;
        } else {
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case ADMIN_ROLE:
                    int pageNo = FIRST_PAGE_NO;
                    String pageNoAttr = configurationManager.getProperty(PAGE_NO);
                    if (request.getParameter(pageNoAttr) != null) {
                        pageNo = Integer.parseInt(request.getParameter(pageNoAttr));
                        if (pageNo < FIRST_PAGE_NO) {
                            pageNo = FIRST_PAGE_NO;
                        }
                    }
                    String showCategoryCreationAttr = configurationManager.getProperty(SHOW_CATEGORY_CREATION_ATTR);
                    session.setAttribute(showCategoryCreationAttr, true);
                    String nextCommand = configurationManager.getProperty(GO_TO_MODERATED_CATEGORIES);
                    String pageNoQueryPart = configurationManager.getProperty(PAGE_NO_QUERY_PART);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + nextCommand + pageNoQueryPart + pageNo;
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
