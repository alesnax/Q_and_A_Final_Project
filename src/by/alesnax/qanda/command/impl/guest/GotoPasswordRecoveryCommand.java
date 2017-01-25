package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.REQUEST_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.RESPONSE_TYPE;
import static by.alesnax.qanda.constant.CommandConstants.TYPE_PAGE_DELIMITER;

/**
 * Created by alesnax on 03.01.2017.
 */
public class GotoPasswordRecoveryCommand implements Command {
    private static final String USER_ATTR = "user";

    private static final String PASSWORD_RECOVERING_PAGE = "path.page.password_recovering";
    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";

    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        User user = (User) session.getAttribute(USER_ATTR);
        if (user != null) {
            String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + user.getId();
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
        } else {
            String path = configurationManager.getProperty(PASSWORD_RECOVERING_PAGE);
            page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + path;
        }
        return page;
    }
}
