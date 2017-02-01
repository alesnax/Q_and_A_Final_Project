package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Command has method that redirects to edit_profile page if user authorised,
 * and to authorisation page otherwise
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoEditProfileCommand implements Command {

    /**
     * Name of user attribute from session
     */
    private static final String USER = "user";

    /**
     * Key of error message attribute located in config.properties file
     */
    private static final String NOT_REGISTERED_USER_YET_ATTR = "attr.not_registered_user_yet";

    /**
     * Key of error message located in loc.properties file
     */
    private static final String WARN_LOGIN_BEFORE_WATCH_PROFILE = "warn.login_before_watch_profile";

    /**
     * Keys of returned command or page that are located in config.properties file
     */
    private static final String EDIT_PROFILE_PATH = "path.page.edit_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    /**
     * Process redirecting to edit_profile.jsp for authorised users, and redirects to authorisation page otherwise.
     *
     * @param request Processed HttpServletRequest
     * @return value of edit_profile page string if user authorised, to authorisation page otherwise
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
            String editProfilePath = configurationManager.getProperty(EDIT_PROFILE_PATH);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + editProfilePath;
        }
        return page;
    }
}
