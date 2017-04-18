package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static by.alesnax.qanda.constant.CommandConstants.*;

//static import

/**
 * Class process changing language of user session. Access for authorised users.
 * Returns to previous page or to error page if exception will be caught.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class ChangeUserLanguageCommand implements Command {
    private static Logger logger = LogManager.getLogger(ChangeUserLanguageCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String USER = "user";
    private static final String LANGUAGE = "language";
    private static final String LOCALE = "locale";

    /**
     * Keys of error or success messages attributes that are located in config.properties file
     */
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String SUCCESS_CHANGE_LANG_MESSAGE = "edit_profile.message.lang_change_saved";

    /**
     * Key of command that is located in config.properties file
     */
    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";

    /**
     * process changing language of user session, calls process method from service layer,
     * if success scenario - returns to previous page with success message,
     * otherwise returns with error messages to previous page or error page.
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        String language = request.getParameter(LANGUAGE);
        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        UserService userService = ServiceFactory.getInstance().getUserService();

        try {
            userService.changeUserLanguage(user.getId(), language);
            user.setLanguage(User.Language.valueOf(language.toUpperCase()));
            session.setAttribute(LOCALE, language);
            logger.log(Level.INFO, "User " + user.getId() + " (" + user.getLogin() + ") has successfully change his used language.");
            String successChangeMessageAttr = configurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
            session.setAttribute(successChangeMessageAttr, SUCCESS_CHANGE_LANG_MESSAGE);

            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        } catch (ServiceException e) {
            logger.log(Level.ERROR, e);
            String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
            request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
            page = ERROR_REQUEST_TYPE;
        }
        return page;
    }
}
