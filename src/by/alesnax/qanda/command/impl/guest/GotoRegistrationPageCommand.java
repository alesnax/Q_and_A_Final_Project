package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// static import
import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 11.12.2016.
 */

public class GotoRegistrationPageCommand implements Command {
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moder";
    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ATTR = "user";
    private static final String REGISTRATION_PAGE = "path.page.register_new_user";
    private static final String USER_MAIN_PAGE = "path.page.profile";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;

        HttpSession session = request.getSession(true);

        QueryUtil.savePreviousQueryToSession(request);
        User user = (User) session.getAttribute(USER_ATTR);

        if (user == null) {
            String registrationPath = ConfigurationManager.getProperty(REGISTRATION_PAGE);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + registrationPath;
        } else {
            String role = user.getRole().getValue();
            switch (role) {
                case USER_ROLE:
                case MODERATOR_ROLE:
                case ADMIN_ROLE:
                    String userProfilePath = ConfigurationManager.getProperty(USER_MAIN_PAGE);
                    page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + userProfilePath;
                    break;
                default:
                    String registrationPath = ConfigurationManager.getProperty(REGISTRATION_PAGE);
                    page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + registrationPath;
                    break;
            }
        }
        return page;
    }
}
