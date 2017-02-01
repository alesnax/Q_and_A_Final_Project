package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//static import
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Command has method that redirects user to profile page if attribute 'user' exists  in session, otherwise
 * it redirects returns value of go_to_categories command
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoMainPageCommand implements Command {

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ATTR = "user";

    /**
     * Keys of commands that is located in config.properties file
     */
    private static final String GO_TO_CATEGORIES_COMMAND = "command.go_to_quest_categories";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    /**
     * Command has method that redirects user to profile page if attribute 'user' exists  in session, otherwise
     * it redirects returns value of go_to_categories command
     *
     * @param request Processed HttpServletRequest
     * @return value of go_to_categories command if user doesn't exist in session, go_to_profile command otherwise
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER_ATTR);
        if (user == null) {
            String gotoCategoriesCommand = configurationManager.getProperty(GO_TO_CATEGORIES_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoCategoriesCommand;
        } else {
            String role = user.getRole().getValue();
            int userId = user.getId();
            switch (role) {
                case USER_ROLE:
                case MODERATOR_ROLE:
                case ADMIN_ROLE:
                    String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                    break;
                default:
                    String gotoCategoriesCommand = configurationManager.getProperty(GO_TO_CATEGORIES_COMMAND);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoCategoriesCommand;
                    break;
            }
        }
        return page;
    }
}