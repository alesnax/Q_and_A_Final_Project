package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Command implements method that redirects user to user_authorisation.jsp if user attribute
 * doesn't exist in session otherwise it redirects to go_to_profile command
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoAuthorizationPageCommand implements Command {
    /**
     * User attribute taking from session
     */
    private static final String USER_ATTR = "user";

    /**
     * Keys of returned command or page which value are located in config.properties file
     */
    private static final String AUTHORIZATION_PAGE = "path.page.user_authorization";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    /**
     * checks if attribute 'user' exists in session and returns value of authorization page.
     * Otherwise return redirecting to go_to_profile command
     *
     * @param request Processed HttpServletRequest
     * @return value of authorization page (if user exists in session), value of go_to_profile command string otherwise
     */
    @SuppressWarnings("Duplicates")
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER_ATTR);
        if (user == null) {
            String path = configurationManager.getProperty(AUTHORIZATION_PAGE);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + path;
        } else {
            String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
        }
        return page;
    }
}
