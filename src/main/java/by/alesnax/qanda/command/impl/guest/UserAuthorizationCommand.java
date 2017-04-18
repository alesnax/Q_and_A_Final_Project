package by.alesnax.qanda.command.impl.guest;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.util.QueryUtil;
import by.alesnax.qanda.entity.User;
import by.alesnax.qanda.resource.ConfigurationManager;
import by.alesnax.qanda.service.ServiceException;
import by.alesnax.qanda.service.UserService;
import by.alesnax.qanda.service.impl.ServiceFactory;
import by.alesnax.qanda.validation.UserValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.alesnax.qanda.constant.CommandConstants.*;

/**
 * Created by alesnax on 08.12.2016.
 */
public class UserAuthorizationCommand implements Command {
    private static Logger logger = LogManager.getLogger(UserAuthorizationCommand.class);

    private static final String EMAIL = "email";
    private static final String PASSWORD = "Passwd";
    private static final String USER = "user";
    private static final String NONE_LANG = "none";
    private static final String LOCALE = "locale";

    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String ERROR_NOT_REGISTERED_YET_ATTR = "attr.not_registered_user_yet";
    private static final String ERROR_WRONG_EMAIL_OR_PASS = "guest.user_authorization_page.wrong_email_or_password";

    private static final String GO_TO_PROFILE_COMMAND = "command.go_to_profile";
    private static final String GO_TO_AUTHORIZATION_COMMAND = "path.command.go_to_authorization_page";

    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        String email = request.getParameter(EMAIL);
        String password = request.getParameter(PASSWORD);

        HttpSession session = request.getSession(true);
        QueryUtil.savePreviousQueryToSession(request);

        UserValidation userValidation = new UserValidation();
        List<String> validationErrors = userValidation.validateUserInfo(email, password);

        if (validationErrors.isEmpty()) {
            UserService userService = ServiceFactory.getInstance().getUserService();
            try {
                User user = userService.userAuthorization(email, password);
                if (user != null) {
                    logger.log(Level.INFO, "User " + user.getLogin() + " has successfully login into the system.");
                    int userId = user.getId();
                    session.setAttribute(USER, user);
                    String lang = user.getLanguage().name().toLowerCase();
                    if (!lang.equals(NONE_LANG)) {
                        session.setAttribute(LOCALE, lang);
                    }
                    String gotoProfileCommand = configurationManager.getProperty(GO_TO_PROFILE_COMMAND) + userId;
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoProfileCommand;
                } else {
                    logger.log(Level.WARN, "Failed try to get into the system with email: '" + email + "'.");
                    validationErrors.add(ERROR_WRONG_EMAIL_OR_PASS);
                    String errorNotRegisteredAttr = configurationManager.getProperty(ERROR_NOT_REGISTERED_YET_ATTR);
                    request.getSession(true).setAttribute(errorNotRegisteredAttr, validationErrors);
                    String gotoAuthorizationCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
                    page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoAuthorizationCommand;
                }
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        } else {
            logger.log(Level.WARN, "Failed try to get into the system with incorrect email or password.");
            String errorNotRegisteredAttr = configurationManager.getProperty(ERROR_NOT_REGISTERED_YET_ATTR);
            request.getSession(true).setAttribute(errorNotRegisteredAttr, validationErrors);
            String gotoAuthorizationCommand = configurationManager.getProperty(GO_TO_AUTHORIZATION_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoAuthorizationCommand;
        }
        return page;
    }
}
