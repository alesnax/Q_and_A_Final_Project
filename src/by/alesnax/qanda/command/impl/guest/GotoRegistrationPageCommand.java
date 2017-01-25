package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// static import
import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 11.12.2016.
 */

public class GotoRegistrationPageCommand implements Command {
    private static final String USER_ROLE = "user";
    private static final String MODERATOR_ROLE = "moderator";
    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ATTR = "user";
    private static final String REGISTRATION_PAGE = "path.page.register_new_user";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);

        QueryUtil.savePreviousQueryToSession(request);
        User user = (User) session.getAttribute(USER_ATTR);

        if (user == null) {
            String registrationPath = configurationManager.getProperty(REGISTRATION_PAGE);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + registrationPath;
        } else {
            String role = user.getRole().getValue();
            switch (role) {
                case USER_ROLE:
                case MODERATOR_ROLE:
                case ADMIN_ROLE:
                    String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                    break;
                default:
                    String registrationPath = configurationManager.getProperty(REGISTRATION_PAGE);
                    page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + registrationPath;
                    break;
            }
        }
        return page;
    }
}
