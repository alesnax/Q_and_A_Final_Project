package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.resource.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;

import static by.alesnax.qanda.constant.CommandConstants.*;

;
//static import

/**
 * Command has method that redirects to edit_profile page if user authorised,
 * and to authorisation page otherwise
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class GotoEditProfileCommand implements Command {

    /**
     * Keys of returned command or page that are located in config.properties file
     */
    private static final String EDIT_PROFILE_PATH = "path.page.edit_profile";

    /**
     * Process redirecting to edit_profile.jsp for authorised users, and redirects to authorisation page otherwise.
     *
     * @param request Processed HttpServletRequest
     * @return value of edit_profile page string
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        QueryUtil.savePreviousQueryToSession(request);

        String editProfilePath = configurationManager.getProperty(EDIT_PROFILE_PATH);
        page = REQUEST_TYPE + TYPE_PAGE_DELIMITER + editProfilePath;

        return page;
    }
}
