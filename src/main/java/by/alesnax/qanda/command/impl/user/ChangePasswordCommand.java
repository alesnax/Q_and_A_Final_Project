package by.alesnax.qanda.command.impl.user;

import by.alesnax.qanda.command.Command;
import by.alesnax.qanda.command.impl.guest.UserAuthorizationCommand;
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

//static import

/**
 * Class process changing password. Access for authorised users.
 * If validation failed, user will be redirected to previous page with error message as an attribute.
 *
 * @author Aliaksandr Nakhankou
 * @see Command
 */
public class ChangePasswordCommand implements Command {
    private static Logger logger = LogManager.getLogger(UserAuthorizationCommand.class);

    /**
     * Names of attributes and parameters taking from request or session
     */
    private static final String OLD_PASSWORD = "OldPasswd";
    private static final String NEW_PASSWORD = "Passwd";
    private static final String REPEATED_NEW_PASSWORD = "PasswdAgain";
    private static final String USER = "user";

    /**
     * Keys of error or success messages attributes that are located in config.properties file
     */
    private static final String WRONG_COMMAND_MESSAGE_ATTR = "attr.wrong_command_message";
    private static final String ERROR_MESSAGE_ATTR = "attr.service_error";
    private static final String ERROR_PASSWORD_VALIDATION_ATTR = "attr.password_validation_error";
    private static final String SUCCESS_CHANGE_MSG_ATTR = "attr.success_profile_change_msg";

    /**
     * Keys of error messages located in loc.properties file
     */
    private static final String SUCCESS_CHANGE_PASSWORD_MESSAGE = "edit_profile.message.change_password_saved";
    private static final String WRONG_PASSWORD_FOUND = "edit_profile.message.wrong_password_found";

    /**
     * Keys of commands that is located in config.properties file
     */
    private static final String GO_TO_EDIT_PROFILE_COMMAND = "command.go_to_edit_profile";

    /**
     * process changing password and validates passwords,
     * calls process method from service layer, if success scenario - returns to previous page with
     * success message , otherwise returns with error messages to previous page or error page
     *
     * @param request Processed HttpServletRequest
     * @return value of page where processed request will be send back
     * (redirection to previous page if success scenario or authorization page or error page otherwise)
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        ConfigurationManager configurationManager = new ConfigurationManager();
        String password1 = request.getParameter(OLD_PASSWORD);
        String password2 = request.getParameter(NEW_PASSWORD);
        String password3 = request.getParameter(REPEATED_NEW_PASSWORD);

        HttpSession session = request.getSession(true);
        QueryUtil.logQuery(request);

        User user = (User) session.getAttribute(USER);
        UserValidation userValidation = new UserValidation();
        List<String> validationErrors = userValidation.validateNewPassword(password1, password2, password3);


        if (validationErrors.isEmpty()) {
            UserService userService = ServiceFactory.getInstance().getUserService();
            try {
                boolean changed = userService.changePassword(user.getId(), password1, password2);
                if (changed) {
                    logger.log(Level.INFO, "User " + user.getId() + " has successfully change his profile information");
                    String successChangeMessageAttr = configurationManager.getProperty(SUCCESS_CHANGE_MSG_ATTR);
                    session.setAttribute(successChangeMessageAttr, SUCCESS_CHANGE_PASSWORD_MESSAGE);
                } else {
                    logger.log(Level.WARN, "User id=" + user.getId() + " :Wrong password was found while changing password try.");
                    String wrongCommandMessageAttr = configurationManager.getProperty(WRONG_COMMAND_MESSAGE_ATTR);
                    session.setAttribute(wrongCommandMessageAttr, WRONG_PASSWORD_FOUND);
                }
                String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
                page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
            } catch (ServiceException e) {
                logger.log(Level.ERROR, e);
                String errorMessageAttr = configurationManager.getProperty(ERROR_MESSAGE_ATTR);
                request.setAttribute(errorMessageAttr, e.getCause() + " : " + e.getMessage());
                page = ERROR_REQUEST_TYPE;
            }
        } else {
            logger.log(Level.WARN, "User id=" + user.getId() + " :Validation of user passwords while changing failed.");
            String errorUserValidationAttr = configurationManager.getProperty(ERROR_PASSWORD_VALIDATION_ATTR);// try-catch
            session.setAttribute(errorUserValidationAttr, validationErrors);
            String gotoEditProfileCommand = configurationManager.getProperty(GO_TO_EDIT_PROFILE_COMMAND);
            page = RESPONSE_TYPE + TYPE_PAGE_DELIMITER + gotoEditProfileCommand;
        }
        return page;
    }
}
